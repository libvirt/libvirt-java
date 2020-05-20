package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.LibvirtQemu;
import org.libvirt.jna.Libvirt.VirEventTimeoutCallback;
import org.libvirt.jna.CString;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;

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
public final class Library {
    private static AtomicBoolean runLoop = new AtomicBoolean();
    private static AtomicInteger timerID = new AtomicInteger(-1);
    private static VirEventTimeoutCallback timer = new VirEventTimeoutCallback() {
            @Override
            public void tick(final int id, final Pointer p) {
                // disable myself again right after being triggered
                libvirt.virEventUpdateTimeout(id, -1);
            }
        };

    static final Libvirt libvirt;
    static final LibvirtQemu libvirtQemu;

    // an empty string array constant
    // prefer this over creating empty arrays dynamically.
    static final String[] NO_STRINGS = {};

    // Load the native part
    static {
        libvirt = Libvirt.INSTANCE;
        try {
            processError(libvirt.virInitialize());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            libvirtQemu = getVersion() > 9010 ? LibvirtQemu.INSTANCE : null;
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error get version", e);
        }
    }

    private Library() {}

    /**
     * Returns the version of the native libvirt library.
     *
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     */
    public static long getVersion() throws LibvirtException {
        LongByReference libVer = new LongByReference();
        processError(libvirt.virGetVersion(libVer, null, null));
        return libVer.getValue();
    }

    /**
     * Free memory pointed to by ptr.
     */
    static void free(final Pointer ptr) {
        Native.free(Pointer.nativeValue(ptr));
        Pointer.nativeValue(ptr, 0L);
    }

    /**
     * Convert the given array of UTF-8 encoded C-Strings to an array
     * of Strings.
     *
     * \note The memory used by the elements of the original array
     *       is freed.
     */
    static String[] toStringArray(final CString[] cstrarr, final int size) {
        int i = 0;
        try {
            final String[] result = new String[size];
            for (; i < size; ++i) {
                result[i] = cstrarr[i].toString();
            }
            return result;
        } catch (RuntimeException e) {
            for (; i < size; ++i) {
                if (cstrarr[i] != null) {
                    cstrarr[i].free();
                }
            }
            throw e;
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
            if (Thread.interrupted()) {
                throw new InterruptedException();
            }
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
            if (timer >= 0) {
                libvirt.virEventUpdateTimeout(timer, 0);
            }
        }
    }

    /**
     * Look up a constant of an enum by its ordinal number.
     *
     * @return the corresponding enum constant when such a constant exists,
     *         otherwise the element which has the biggest ordinal number
     *         assigned.
     *
     * @throws IllegalArgumentException if {@code ordinal} is negative
     */
    static <T extends Enum<T>> T getConstant(final Class<T> c, final int ordinal) {
        if (ordinal < 0) {
            throw new IllegalArgumentException("ordinal must be >= 0");
        }

        T[] a = c.getEnumConstants();

        assert a.length > 0 : "there must be at least one enum constant";

        return a[Math.min(ordinal, a.length - 1)];
    }
}
