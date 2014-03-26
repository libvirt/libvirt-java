package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.Libvirt.VirEventTimeoutCallback;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class represents an instance of the JNA mapped libvirt
 * library.
 *
 * The library will get loaded when first accessing this class.
 *
 * Additionally, this class contains internal methods to ease
 * implementing the public API.
 */
final class Library {
    private static AtomicBoolean runLoop = new AtomicBoolean();
    private static AtomicInteger timerID = new AtomicInteger(-1);
    private static VirEventTimeoutCallback timer = new VirEventTimeoutCallback() {
            @Override
            public void tick(int id, Pointer p) {
                // disable myself again right after being triggered
                libvirt.virEventUpdateTimeout(id, -1);
            }
        };

    final static Libvirt libvirt;

    // an empty string array constant
    // prefer this over creating empty arrays dynamically.
    final static String[] NO_STRINGS = {};

    // Load the native part
    static {
        libvirt = Libvirt.INSTANCE;
        try {
            processError(libvirt.virInitialize());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Library() {}

    /**
     * Free memory pointed to by ptr.
     */
    static void free(Pointer ptr) {
        Native.free(Pointer.nativeValue(ptr));
        Pointer.nativeValue(ptr, 0L);
    }

    /**
     * Convert the data pointed to by {@code ptr} to a String.
     */
    static String getString(Pointer ptr) {
        final long len = ptr.indexOf(0, (byte)0);
        assert (len != -1): "C-Strings must be \\0 terminated.";

        final byte[] data = ptr.getByteArray(0, (int)len);
        try {
            return new String(data, "utf-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Libvirt problem: UTF-8 decoding error.", e);
        }
    }

    /**
     * Calls {@link #toStringArray(Pointer[], int)}.
     */
    static String[] toStringArray(Pointer[] ptrArr) {
        return toStringArray(ptrArr, ptrArr.length);
    }

    /**
     * Convert the given array of native pointers to "char" in
     * UTF-8 encoding to an array of Strings.
     *
     * \note The memory used by the elements of the original array
     *       is freed and ptrArr is modified.
     */
    static String[] toStringArray(Pointer[] ptrArr, final int size) {
        try {
            final String[] result = new String[size];
            for (int i = 0; i < size; ++i) {
                result[i] = Library.getString(ptrArr[i]);
            }
            return result;
        } finally {
            for (int i = 0; i < size; ++i) {
                Library.free(ptrArr[i]);
                ptrArr[i] = null;
            }
        }
    }

    /**
     * Initialize the event loop.
     *
     * Registers a default event loop implementation based on the
     * poll() system call.
     * <p>
     * Once registered, the application has to invoke
     * {@link #processEvent} in a loop or call {@link #runEventLoop}
     * in another thread.
     * <p>
     * Note: You must call this function <em>before</em> connecting to
     *       the hypervisor.
     *
     * @throws LibvirtException on failure
     *
     * @see #processEvent
     * @see #runLoop
     */
    public static void initEventLoop() throws LibvirtException {
        if (timerID.get() == -1) {
            processError(libvirt.virEventRegisterDefaultImpl());

            // add a disabled timer which is used later to break out
            // of the event loop
            int id = processError(libvirt.virEventAddTimeout(-1, timer, null, null));

            // remove this timer when there already is another one
            if (!timerID.compareAndSet(-1, id)) {
                libvirt.virEventRemoveTimeout(id);
            }
        }
    }

    /**
     * Run one iteration of the event loop.
     * <p>
     * Applications will generally want to have a thread which invokes
     * this method in an infinite loop:
     * <pre>
     * {@code while (true) connection.processEvent(); }
     * </pre>
     * <p>
     * Failure to do so may result in connections being closed
     * unexpectedly as a result of keepalive timeout.
     *
     * @throws LibvirtException on failure
     *
     * @see #initEventLoop()
     */
    public static void processEvent() throws LibvirtException {
        processError(libvirt.virEventRunDefaultImpl());
    }

    /**
     * Runs the event loop.
     *
     * This method blocks until {@link #stopEventLoop} is called or an
     * exception is thrown.
     * <p>
     * Usually, this method is run in another thread.
     *
     * @throws LibvirtException     if there was an error during the call of a
     *                              native libvirt function
     * @throws InterruptedException if this thread was interrupted by a call to
     *                              {@link java.lang.Thread#interrupt() Thread.interrupt()}
     */
    public static void runEventLoop() throws LibvirtException, InterruptedException {
        runLoop.set(true);
        do {
            processEvent();
            if (Thread.interrupted())
                throw new InterruptedException();
        } while (runLoop.get());
    }

    /**
     * Stops the event loop.
     *
     * This methods stops an event loop when an event loop is
     * currently running, otherwise it does nothing.
     *
     * @see #runEventLoop
     */
    public static void stopEventLoop() throws LibvirtException {
        if (runLoop.getAndSet(false)) {
            // fire the timer immediately
            int timer = timerID.get();
            if (timer >= 0)
                libvirt.virEventUpdateTimeout(timer, 0);
        }
    }
}
