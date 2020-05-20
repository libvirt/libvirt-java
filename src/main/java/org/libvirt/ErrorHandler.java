package org.libvirt;

import org.libvirt.jna.virError;

import static org.libvirt.Library.libvirt;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * Utility class which processes the last error from the libvirt library. It
 * turns errors into Libvirt Exceptions.
 *
 * @author bkearney
 */
public class ErrorHandler {

    private static void processError() throws LibvirtException {
        virError vError = libvirt.virGetLastError();
        if (vError != null) {
            Error error = new Error(vError);
            /*
             * Don't throw exceptions for VIR_ERR_WARNING level errors
             */
            if (error.getLevel() == Error.ErrorLevel.VIR_ERR_ERROR) {
                throw new LibvirtException(error);
            }
        }
    }

    /**
     * Calls {@link #processError()} when the given libvirt return code
     * indicates an error.
     *
     * @param  ret libvirt return code, indicating error if -1.
     * @return {@code ret}
     * @throws LibvirtException
     */
    static final int processError(final int ret) throws LibvirtException {
        if (ret == -1) {
            processError();
        }

        return ret;
    }

    /**
     * Calls {@link #processError()} if {@code arg} is null.
     *
     * @param  arg  An arbitrary object returned by libvirt.
     * @return {@code arg}
     * @throws LibvirtException
     */
    static final <T extends PointerType> T processError(final T arg) throws LibvirtException {
        if (arg == null) {
            processError();
        }

        return arg;
    }

    static final Pointer processError(final Pointer arg) throws LibvirtException {
        if (arg == null) {
            processError();
        }

        return arg;
    }

    static final String processError(final String str) throws LibvirtException {
        if (str == null) {
            processError();
        }

        return str;
    }

    static final long processErrorIfZero(final long ret) throws LibvirtException {
        if (ret == 0) {
            processError();
        }

        return ret;
    }
}
