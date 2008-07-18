package org.libvirt;

/**
 * This exception is thrown by all classes and methods of libvirt when the
 * underlying libvirt library indicates an error
 *
 * @author stoty
 * @see Error
 */
public class LibvirtException extends Exception {

	Error virError;

	LibvirtException(Error virError) {
		super(virError.getMessage());
		this.virError = virError;
	}

	/**
	 * Returns the underlying Error objects that contains details
	 *         about the cause of the exception
	 *
	 * @return the underlying Error object
	 */
	public Error getError() {
		return virError;
	}

}
