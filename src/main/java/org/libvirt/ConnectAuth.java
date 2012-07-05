package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virConnectCredential;

import com.sun.jna.Pointer;

/**
 * We diverge from the C implementation There is no explicit cbdata field, you
 * should just add any extra data to the child class's instance.
 *
 * @author stoty
 *
 */
public abstract class ConnectAuth implements Libvirt.VirConnectAuthCallback {
    public class Credential {

        /**
         * One of virConnectCredentialType constants
         */
        public CredentialType type;
        /**
         * Prompt to show to user
         */
        public String prompt;
        /**
         * Additional challenge to show
         */
        public String challenge;
        /**
         * Optional default result
         */
        public String defresult;
        /**
         * Result to be filled with user response (or defresult)
         */
        public String result;

        /**
         * Convenience constructor to be called from the JNI side
         *
         * @param type
         * @param prompt
         * @param challenge
         * @param defresult
         */
        Credential(int type, String prompt, String challenge, String defresult) {
            switch (type) {
                case 1:
                    this.type = CredentialType.VIR_CRED_USERNAME;
                    break;
                case 2:
                    this.type = CredentialType.VIR_CRED_AUTHNAME;
                    break;
                case 3:
                    this.type = CredentialType.VIR_CRED_LANGUAGE;
                    break;
                case 4:
                    this.type = CredentialType.VIR_CRED_CNONCE;
                    break;
                case 5:
                    this.type = CredentialType.VIR_CRED_PASSPHRASE;
                    break;
                case 6:
                    this.type = CredentialType.VIR_CRED_ECHOPROMPT;
                    break;
                case 7:
                    this.type = CredentialType.VIR_CRED_NOECHOPROMPT;
                    break;
                case 8:
                    this.type = CredentialType.VIR_CRED_REALM;
                    break;
                case 9:
                    this.type = CredentialType.VIR_CRED_EXTERNAL;
                    break;
                default:
                    assert (false);
            }
            this.prompt = prompt;
            this.challenge = challenge;
            this.defresult = defresult;
        }

    }

    /**
     * @author stoty
     *
     */
    public static enum CredentialType {

        /**
         * Fake credential so that the ordinal value equls the c value.
         */
        VIR_CRED_NONE,
        /**
         * Identity to act as
         */
        VIR_CRED_USERNAME,
        /**
         * Identify to authorize as
         */
        VIR_CRED_AUTHNAME,
        /**
         * RFC 1766 languages, comma separated
         */
        VIR_CRED_LANGUAGE,
        /**
         * client supplies a nonce
         */
        VIR_CRED_CNONCE,
        /**
         * Passphrase secret
         */
        VIR_CRED_PASSPHRASE,
        /**
         * Challenge response
         */
        VIR_CRED_ECHOPROMPT,
        /**
         * Challenge response
         */
        VIR_CRED_NOECHOPROMPT,
        /**
         * Authentication realm
         */
        VIR_CRED_REALM,
        /**
         * Externally managed credential More may be added - expect the
         * unexpected
         */
        VIR_CRED_EXTERNAL;

        /**
         * Maps the java CredentialType Enum to libvirt's integer constant
         *
         * @return The integer equivalent
         */
        @SuppressWarnings("all")
        public int mapToInt() {
            switch (this) {
                case VIR_CRED_USERNAME:
                    return 1;
                case VIR_CRED_AUTHNAME:
                    return 2;
                case VIR_CRED_LANGUAGE:
                    return 3;
                case VIR_CRED_CNONCE:
                    return 4;
                case VIR_CRED_PASSPHRASE:
                    return 5;
                case VIR_CRED_ECHOPROMPT:
                    return 6;
                case VIR_CRED_NOECHOPROMPT:
                    return 7;
                case VIR_CRED_REALM:
                    return 8;
                case VIR_CRED_EXTERNAL:
                    return 9;
            }
            // We may never reach this point
            assert (false);
            return 0;
        }
    }

    /**
     * List of supported ConnectCredential.CredentialType values
     */
    public CredentialType credType[];

    public int authCallback(virConnectCredential cred, int ncred, Pointer cbdata) {
        virConnectCredential[] nativeCreds = (virConnectCredential[]) cred.toArray(ncred);
        Credential[] creds = new Credential[ncred];
        for (int x = 0; x < ncred; x++) {
            virConnectCredential vCred = nativeCreds[x];
            creds[x] = new Credential(vCred.type, vCred.prompt, vCred.challenge, vCred.defresult);
        }
        int returnValue = callback(creds);
        for (int x = 0; x < ncred; x++) {
            virConnectCredential vCred = nativeCreds[x];
            String result = creds[x].result;
            vCred.result = result;
            vCred.resultlen = result.length();
            vCred.write();
        }

        return returnValue;
    }

    /**
     * The callback function that fills the credentials in
     *
     * @param cred
     *            the array of credentials passed by libvirt
     * @return 0 if the defresult field contains a vailde response, -1 otherwise
     */
    public abstract int callback(Credential[] cred);

}
