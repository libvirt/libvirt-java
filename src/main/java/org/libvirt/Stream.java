package org.libvirt;

import java.io.IOException;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.SizeT;
import org.libvirt.jna.StreamPointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

/**
 * The Stream class is used to transfer data between a libvirt daemon
 * and a client.
 * <p>
 * It implements the ByteChannel interface.
 * <p>
 * Basic usage:
 *
 * <pre>
 * {@code
 * ByteBuffer buf = ByteBuffer.allocate(1024);
 * Stream str = conn.streamNew(0);
 *
 * ... // open the stream e.g. calling Domain.screenshot
 *
 * while (str.read(buf) != -1) {
 *     buf.flip();
 *     ... // do something with the data
 *     buf.compact();
 * }}</pre>
 * <p>
 * If you want to use this class as an InputStream or OutputStream,
 * convert it using the {@link java.nio.channels.Channels#newInputStream
 *  Channels.newInputStream} and {@link java.nio.channels.Channels#newOutputStream
 *  Channels.newOutputStream} respectively.
 */
public class Stream implements ByteChannel {

    public static final int VIR_STREAM_NONBLOCK = 1;

    /**
     * the native virStreamPtr.
     */
    private StreamPointer vsp;

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private final Connect virConnect;

    private static final int CLOSED   =  0;
    private static final int READABLE =  1;
    private static final int WRITABLE =  2;
    private static final int OPEN     = READABLE | WRITABLE;
    private static final int EOF      =  4;

    /* The status of the stream. A stream starts its live in the
     * "CLOSED" state.
     *
     * It will be opened for input / output by another libvirt
     * operation (e.g. virStorageVolDownload), which means it will
     * be in state "READABLE" or "WRITABLE", exclusively.
     *
     * It will reach state "EOF", if {@link finish()} is called.
     *
     * It will be in the "CLOSED" state again, after calling abort()
     * or close().
     */
    private int state = CLOSED;

    void markReadable() {
        assert !isWritable()
            : "A Stream cannot be readable and writable at the same time";

        state |= READABLE;
    }

    void markWritable() {
        assert !isReadable()
            : "A Stream cannot be readable and writable at the same time";

        state |= WRITABLE;
    }

    boolean isReadable() {
        return (state & READABLE) != 0;
    }

    boolean isWritable() {
        return (state & WRITABLE) != 0;
    }

    protected boolean isEOF() {
        return (state & EOF) != 0;
    }

    private void markEOF() {
        state |= EOF;
    }

    Stream(final Connect virConnect, final StreamPointer vsp) {
        this.virConnect = virConnect;
        this.vsp = vsp;
    }

    /**
     * Request that the in progress data transfer be cancelled abnormally before
     * the end of the stream has been reached
     *
     * @return <em>ignore</em> (always 0)
     */
    public int abort() throws LibvirtException {
        int returnValue = processError(libvirt.virStreamAbort(vsp));
        this.state = CLOSED;
        return returnValue;
    }

    /**
     * Register a callback to be notified when a stream becomes writable, or
     * readable.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamEventAddCallback">Libvirt
     *      Docs</a>
     * @param events
     *            the events to monitor
     * @param cb
     *            the callback method
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int addCallback(final int events, final Libvirt.VirStreamEventCallback cb)
            throws LibvirtException {
        return processError(libvirt.virStreamEventAddCallback(vsp, events, cb, null, null));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Indicate that there is no further data is to be transmitted on the
     * stream.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int finish() throws LibvirtException {
        int returnValue = processError(libvirt.virStreamFinish(vsp));
        markEOF();
        return returnValue;
    }

    /**
     * Decrement the reference count on a stream, releasing the stream object if
     * the reference count has hit zero.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vsp != null) {
            closeStream();
            success = processError(libvirt.virStreamFree(vsp));
            vsp = null;
        }

        return success;
    }

    StreamPointer getVsp() {
        return vsp;
    }

    /**
     * Receives data from the stream into the buffer provided.
     *
     * @param data
     *            buffer to put the data into
     * @return the number of bytes read, -1 on error, -2 if the buffer is empty
     * @throws LibvirtException
     */
    public int receive(final byte[] data) throws LibvirtException {
        return receive(ByteBuffer.wrap(data));
    }

    protected int receive(final ByteBuffer buffer) throws LibvirtException {
        int returnValue = processError(libvirt.virStreamRecv(vsp, buffer, new SizeT(buffer.remaining())));
        buffer.position(buffer.position() + returnValue);
        return returnValue;
    }

    @Override
    public int read(final ByteBuffer buffer) throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }

        if (!isReadable()) {
            throw new NonReadableChannelException();
        }

        if (isEOF()) {
            return -1;
        }

        try {
            int ret = receive(buffer);

            switch (ret) {
            case 0:
                finish();
                return -1;

            case -2:
                throw new UnsupportedOperationException("non-blocking I/O stream not yet supported");

            default:
                return ret;
            }
        } catch (LibvirtException e) {
            throw new IOException("could not read from stream", e);
        }
    }

    @Override
    public int write(final ByteBuffer buffer) throws IOException {
        if (!isOpen()) {
            throw new ClosedChannelException();
        }

        if (!isWritable()) {
            throw new NonWritableChannelException();
        }

        int pos = buffer.position();

        try {
            while (buffer.hasRemaining()) {
                int ret = send(buffer);

                if (ret == -2) {
                    throw new UnsupportedOperationException("non-blocking I/O stream not yet supported");
                }
            }
            return buffer.position() - pos;
        } catch (LibvirtException e) {
            throw new IOException("could not write to stream", e);
        }
    }

    protected void closeStream() throws LibvirtException {
        if (isOpen() && !isEOF()) {
            if (isWritable()) {
                finish();
            } else if (isReadable()) {
                abort();
            }
        }
        this.state = CLOSED;
    }

    @Override
    public void close() throws IOException {
        try {
            closeStream();
        } catch (LibvirtException e) {
            throw new IOException("error while closing Stream", e);
        }
    }

    @Override
    public boolean isOpen() {
        return (this.state & OPEN) != 0;
    }

    /**
     * Batch receive method
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamRecvAll">virStreamRecvAll</a>
     * @param handler
     *            the callback handler
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int receiveAll(final Libvirt.VirStreamSinkFunc handler)
            throws LibvirtException {
        return processError(libvirt.virStreamRecvAll(vsp, handler, null));
    }

    /**
     * Remove an event callback from the stream
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamEventRemoveCallback">Libvirt Docs</a>
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int removeCallback() throws LibvirtException {
        return processError(libvirt.virStreamEventRemoveCallback(vsp));
    }

    /**
     * Write a series of bytes to the stream.
     *
     * @param data
     *            the data to write
     * @return the number of bytes written, -1 on error, -2 if the buffer is
     *         full
     * @throws LibvirtException
     */
    public int send(final byte[] data) throws LibvirtException {
        return send(ByteBuffer.wrap(data));
    }

    protected int send(final ByteBuffer buffer) throws LibvirtException {
        SizeT size = new SizeT(buffer.remaining());
        int returnValue = processError(libvirt.virStreamSend(vsp, buffer, size));
        buffer.position(buffer.position() + returnValue);
        return returnValue;
    }

    /**
     * Batch send method
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamSendAll">Libvirt
     *      Documentation</a>
     * @param handler
     *            the callback handler
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int sendAll(final Libvirt.VirStreamSourceFunc handler)
            throws LibvirtException {
        return processError(libvirt.virStreamSendAll(vsp, handler, null));
    }

    /**
     * Changes the set of events to monitor for a stream.
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamEventUpdateCallback">Libvirt Docs</a>
     * @param events
     *            the events to monitor
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int updateCallback(final int events) throws LibvirtException {
        return processError(libvirt.virStreamEventUpdateCallback(vsp, events));
    }
}
