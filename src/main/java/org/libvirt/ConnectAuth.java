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
    public static class Credential {

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
        Credential(final int type, final String prompt,
                   final String challenge, final String defresult) {
            this.type = CredentialType.mapFromInt(type);
            this.prompt = prompt;
            this.challenge = challenge;
            this.defresult = defresult;
        }

    }

    /**
     * @author stoty
     *
     */
    public enum CredentialType {

        /**
         * Fake credential so that the ordinal value equls the c value.
         */
        VIR_CRED_NONE(0),
        /**
         * Identity to act as
         */
        VIR_CRED_USERNAME(1),
        /**
         * Identify to authorize as
         */
        VIR_CRED_AUTHNAME(2),
        /**
         * RFC 1766 languages, comma separated
         */
        VIR_CRED_LANGUAGE(3),
        /**
         * client supplies a nonce
         */
        VIR_CRED_CNONCE(4),
        /**
         * Passphrase secret
         */
        VIR_CRED_PASSPHRASE(5),
        /**
         * Challenge response
         */
        VIR_CRED_ECHOPROMPT(6),
        /**
         * Challenge response
         */
        VIR_CRED_NOECHOPROMPT(7),
        /**
         * Authentication realm
         */
        VIR_CRED_REALM(8),
        /**
         * Externally managed credential More may be added - expect the
         * unexpected
         */
        VIR_CRED_EXTERNAL(9);

        private final int value;

        CredentialType(final int val) {
            this.value = val;
        }

        /**
         * Maps the java CredentialType Enum to libvirt's integer constant
         *
         * @return The integer equivalent
         */
        public int mapToInt() {
            return value;
        }

        public static CredentialType mapFromInt(final int t) {
            for (CredentialType type : CredentialType.values()) {
                if (t == type.value) {
                    return type;
                }
            }
            assert false;
            return VIR_CRED_NONE;
        }
    }

    /**
     * List of supported ConnectCredential.CredentialType values
     */
    public CredentialType[] credType;

    public int authCallback(final virConnectCredential cred, final int ncred,
                            final Pointer cbdata) {
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
