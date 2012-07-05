package org.libvirt;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Implements virConnectAuthPtrDefault functionality from libvirt.c without the
 * external method support It's not officially a part of the libvirt API, but
 * provided here for completeness, testing, and as an example
 *
 * @author stoty
 */
public final class ConnectAuthDefault extends ConnectAuth {

    public ConnectAuthDefault() {
        credType = new CredentialType[] { CredentialType.VIR_CRED_AUTHNAME, CredentialType.VIR_CRED_ECHOPROMPT,
                CredentialType.VIR_CRED_REALM, CredentialType.VIR_CRED_PASSPHRASE, CredentialType.VIR_CRED_NOECHOPROMPT };
    }

    @Override
    public int callback(Credential[] cred) {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            for (Credential c : cred) {
                String response = "";
                switch (c.type) {
                    case VIR_CRED_USERNAME:
                    case VIR_CRED_AUTHNAME:
                    case VIR_CRED_ECHOPROMPT:
                    case VIR_CRED_REALM:
                        System.out.println(c.prompt);
                        response = in.readLine();
                        break;
                    case VIR_CRED_PASSPHRASE:
                    case VIR_CRED_NOECHOPROMPT:
                        System.out.println(c.prompt);
                        System.out.println("WARNING: THE ENTERED PASSWORD WILL NOT BE MASKED!");
                        response = in.readLine();
                        break;
                }
                if (response.equals("") && !c.defresult.equals("")) {
                    c.result = c.defresult;
                } else {
                    c.result = response;
                }
                if (c.result.equals("")) {
                    return -1;
                }
            }
        } catch (Exception e) {
            return -1;
        }
        return 0;
    }

}
