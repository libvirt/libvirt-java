package org.libvirt;

/**
 * This exception signals that a non-existing object was retrieved from the virError object
 *
 * @author stoty
 *
 */
public class ErrorException extends Exception {

	public ErrorException(String message) {
		super(message);
	}

}
