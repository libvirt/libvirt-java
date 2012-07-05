package org.libvirt;

/**
 * This exception signals that a non-existing object was retrieved from the
 * virError object
 *
 * @author stoty
 * @deprecated
 */
public class ErrorException extends Exception {
    private static final long serialVersionUID = -4329050530233404971L;

    public ErrorException(String message) {
        super(message);
    }

}
