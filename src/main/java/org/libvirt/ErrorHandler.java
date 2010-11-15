package org.libvirt;

import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virError;

/**
 * Utility class which processes the last error from the libvirt library. It
 * turns errors into Libvirt Exceptions.
 *
 * @author bkearney
 */
public class ErrorHandler {

    /**
     * Look for the latest error from libvirt not tied to a connection
     *
     * @param libvirt
     *            the active connection
     * @throws LibvirtException
     */
    public static void processError(Libvirt libvirt) throws LibvirtException {
        virError vError = new virError();
        int errorCode = libvirt.virCopyLastError(vError);
        if (errorCode > 0) {
            Error error = new Error(vError);
            libvirt.virResetLastError();
            /*
             * FIXME: Don't throw exceptions for VIR_ERR_WARNING
             * level errors
             */
            if (error.getLevel() == Error.ErrorLevel.VIR_ERR_ERROR) {
                throw new LibvirtException(error);
            }
        }
    }
}
