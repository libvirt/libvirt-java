package org.libvirt;

/**
 * We diverge from the C implementation
 * There is no explicit cbdata field, you should just add any extra data to the child class's instance.
 * 
 * @author stoty
 *
 */
public abstract class VirConnectAuth {
	/**
	 * List of supported VirConnectCredential.VirConnectCredentialType values
	 */
	public  VirConnectCredential.VirConnectCredentialType credType[];
	
	/**
	 * The callback function that fills the credentials in
	 * @param cred the array of credentials passed by libvirt
	 * @return 0 if the defresult field contains a vailde response, -1 otherwise
	 */
	public abstract int callback(VirConnectCredential[] cred);
}
