package org.libvirt;

/**
 * This exception is thrown by all classes and methods of libvirt when the
 * underlying libvirt library indicates an error
 *
 * @author stoty
 * @see Error
 */
public class LibvirtException extends Exception {

    private static final long serialVersionUID = 5566904363426773529L;

    private Error virError;

    LibvirtException(Error virError) {
        super(virError.getMessage());
        this.virError = virError;
    }

    /**
     * Returns the underlying Error objects that contains details about the
     * cause of the exception
     *
     * @return the underlying Error object
     */
    public Error getError() {
        return virError;
    }

    public LibvirtException() {
    }

    public LibvirtException(final String message) {
        super(message);
    }

    public LibvirtException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public LibvirtException(final Throwable cause) {
        super(cause);
    }

    public LibvirtException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
