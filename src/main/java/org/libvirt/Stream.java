package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.StreamPointer;

import com.sun.jna.NativeLong;

public class Stream {

    public static int VIR_STREAM_NONBLOCK = (1 << 0);

    /**
     * the native virStreamPtr.
     */
    StreamPointer VSP;

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private Connect virConnect;

    /**
     * The libvirt connection from the hypervisor
     */
    protected Libvirt libvirt;

    Stream(Connect virConnect, StreamPointer VSP) {
        this.virConnect = virConnect;
        this.VSP = VSP;
        libvirt = virConnect.libvirt;
    }

    /**
     * Request that the in progress data transfer be cancelled abnormally before
     * the end of the stream has been reached
     */
    public int abort() throws LibvirtException {
        int returnValue = libvirt.virStreamAbort(VSP);
        processError();
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
     * @return 0 for success, -1 for failure
     * @throws LibvirtException
     */
    public int addCallback(int events, Libvirt.VirStreamEventCallback cb) throws LibvirtException {
        int returnValue = libvirt.virStreamEventAddCallback(VSP, events, cb, null, null);
        processError();
        return returnValue;
    }

    @Override
    public void finalize() throws LibvirtException {
        free();
    }

    /**
     * Indicate that there is no further data is to be transmitted on the
     * stream.
     * 
     * @return 0 if success, -1 if failure
     * @throws LibvirtException
     */
    public int finish() throws LibvirtException {
        int returnValue = libvirt.virStreamFinish(VSP);
        processError();
        return returnValue;
    }

    /**
     * Decrement the reference count on a stream, releasing the stream object if
     * the reference count has hit zero.
     * 
     * @throws LibvirtException
     * @return 0 on success, or -1 on error.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VSP != null) {
            success = libvirt.virStreamFree(VSP);
            processError();
            VSP = null;
        }

        return success;
    }

    /**
     * Error handling logic to throw errors. Must be called after every libvirt
     * call.
     */
    protected void processError() throws LibvirtException {
        virConnect.processError();
    }

    /**
     * Receieves data from teh stream into the buffer provided.
     * 
     * @param data
     *            the put the sata into
     * @return the number of bytes read, -1 on error, -2 if the buffer is empty
     * @throws LibvirtException
     */
    public int receive(byte[] data) throws LibvirtException {
        int returnValue = libvirt.virStreamRecv(VSP, data, new NativeLong(data.length));
        processError();
        return returnValue;
    }

    /**
     * Batch receive method
     * 
     * @see http://www.libvirt.org/html/libvirt-libvirt.html#virStreamRecvAll
     * @param handler
     *            the callback handler
     * @return 0 if successfule, -1 otherwise
     * @throws LibvirtException
     */
    public int receiveAll(Libvirt.VirStreamSinkFunc handler) throws LibvirtException {
        int returnValue = libvirt.virStreamRecvAll(VSP, handler, null);
        processError();
        return returnValue;
    }

    /**
     * Remove an event callback from the stream
     * 
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamEventRemoveCallback">Libvirt
     *      Docs</a>
     * @return 0 for success, -1 for failure
     * @throws LibvirtException
     */
    public int removeCallback() throws LibvirtException {
        int returnValue = libvirt.virStreamEventRemoveCallback(VSP);
        processError();
        return returnValue;
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
    public int send(String data) throws LibvirtException {
        int returnValue = libvirt.virStreamSend(VSP, data, new NativeLong(data.length()));
        processError();
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
     * @return 0 if successfule, -1 otherwise
     * @throws LibvirtException
     */
    public int sendAll(Libvirt.VirStreamSourceFunc handler) throws LibvirtException {
        int returnValue = libvirt.virStreamSendAll(VSP, handler, null);
        processError();
        return returnValue;
    }

    /**
     * Changes the set of events to monitor for a stream.
     * 
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStreamEventUpdateCallback">Libvirt
     *      Docs</a>
     * @param events
     *            the events to monitor
     * @return 0 for success, -1 for failure
     * @throws LibvirtException
     */
    public int updateCallback(int events) throws LibvirtException {
        int returnValue = libvirt.virStreamEventUpdateCallback(VSP, events);
        processError();
        return returnValue;
    }
}
