package org.libvirt;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author stoty
 * Implements virConnectAuthPtrDefault functionality from libvirt.c without the external method support
 * It's not officially a part of the libvirt API, but provided here for completeness, testing, and as an example  
 */
public final class VirConnectAuthDefault extends VirConnectAuth {

	{
		credType= new  VirConnectCredential.VirConnectCredentialType[] {
			VirConnectCredential.VirConnectCredentialType.VIR_CRED_AUTHNAME,
			VirConnectCredential.VirConnectCredentialType.VIR_CRED_ECHOPROMPT,
			VirConnectCredential.VirConnectCredentialType.VIR_CRED_REALM,
			VirConnectCredential.VirConnectCredentialType.VIR_CRED_PASSPHRASE,
			VirConnectCredential.VirConnectCredentialType.VIR_CRED_NOECHOPROMPT 
			};
	}
	
	@Override
	public int callback(VirConnectCredential[] cred) {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try{
			for(VirConnectCredential c: cred){
				String response="";
				switch(c.type){
					case VIR_CRED_USERNAME:
					case VIR_CRED_AUTHNAME:
					case VIR_CRED_ECHOPROMPT:
					case VIR_CRED_REALM: 
						System.out.println(c.prompt);
						response= in.readLine();
						break;
					case VIR_CRED_PASSPHRASE:
					case VIR_CRED_NOECHOPROMPT: 
						System.out.println(c.prompt);
						System.out.println("WARNING: THE ENTERED PASSWORD WILL NOT BE MASKED!");
						response= in.readLine();
						break;
				}
				if(response.equals("") && !c.defresult.equals("")){
					c.result=c.defresult;
				} else {
					c.result=response;
				}
				if(c.result.equals("")){
					return -1;
				}
			}
		} catch (Exception e) {
			return -1;
		}
		return 0;
	}

}
