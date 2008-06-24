package org.libvirt;

/**
 * This exception signals that a non-existing object was retrieved from the virError object
 * 
 * @author stoty
 *
 */
public class VirErrorException extends Exception {

	public VirErrorException(String message) {
		super(message);
	}

}
