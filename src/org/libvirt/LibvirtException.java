package org.libvirt;

/**
 * This exception is thrown by all classes and methods of libvirt when the
 * underlying libvirt library indicates an error
 * 
 * @author stoty
 * @see VirError
 */
public class LibvirtException extends Exception {

	VirError virError;

	LibvirtException(VirError virError) {
		super(virError.getMessage());
		this.virError = virError;
	}

	/**
	 * Returns the underlying VirError objects that contains details
	 *         about the cause of the exception
	 * 
	 * @return the underlying VirError object
	 */
	public VirError getVirError() {
		return virError;
	}

}
