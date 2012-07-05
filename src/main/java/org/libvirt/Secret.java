package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.SecretPointer;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.Pointer;
import java.nio.ByteBuffer;

/**
 * A secret defined by libvirt
 */
public class Secret {

    /**
     * the native virSecretPtr.
     */
    SecretPointer VSP;

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private Connect virConnect;

    /**
     * The libvirt connection from the hypervisor
     */
    protected Libvirt libvirt;

    Secret(Connect virConnect, SecretPointer VSP) {
        this.virConnect = virConnect;
        this.VSP = VSP;
        libvirt = virConnect.libvirt;
    }

    @Override
    public void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the secret handle. The underlying secret continues to exist.
     *
     * @throws LibvirtException
     * @return 0 on success, or -1 on error.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VSP != null) {
            success = libvirt.virSecretFree(VSP);
            processError();
            VSP = null;
        }

        return success;
    }

    /**
     * Get the unique identifier of the object with which this secret is to be
     * used.
     *
     * @return a string identifying the object using the secret, or NULL upon
     *         error
     * @throws LibvirtException
     */
    public String getUsageID() throws LibvirtException {
        String returnValue = libvirt.virSecretGetUsageID(VSP);
        processError();
        return returnValue;
    }

    /**
     * Get the UUID for this secret.
     *
     * @return the UUID as an unpacked int array
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public int[] getUUID() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        int success = libvirt.virSecretGetUUID(VSP, bytes);
        processError();
        int[] returnValue = new int[0];
        if (success == 0) {
            returnValue = Connect.convertUUIDBytes(bytes);
        }
        return returnValue;
    }

    /**
     * Gets the UUID for this secret as string.
     *
     * @return the UUID in canonical String format
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public String getUUIDString() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_STRING_BUFLEN];
        int success = libvirt.virSecretGetUUIDString(VSP, bytes);
        processError();
        String returnValue = null;
        if (success == 0) {
            returnValue = Native.toString(bytes);
        }
        return returnValue;
    }

    /**
     * Fetches the value of the secret as a string (note that
     * this may not always work and getByteValue() is more reliable)
     * This is just kept for backward compatibility
     *
     * @return the value of the secret, or null on failure.
     */
    public String getValue() throws LibvirtException {
        String returnValue = new String(getByteValue());
        return returnValue;
    }

    /**
     * Fetches the value of the secret as a byte array
     *
     * @return the value of the secret, or null on failure.
     */
    public byte[] getByteValue() throws LibvirtException {
        LongByReference value_size = new LongByReference();
        Pointer value = libvirt.virSecretGetValue(VSP, value_size, 0);
        processError();
        ByteBuffer bb = value.getByteBuffer(0, value_size.getValue());
        byte[] returnValue = new byte[bb.remaining()];
        bb.get(returnValue);
        return returnValue;
    }

    /**
     * Fetches an XML document describing attributes of the secret.
     *
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        String returnValue = libvirt.virSecretGetXMLDesc(VSP, 0);
        processError();
        return returnValue;
    }

    /**
     * Error handling logic to throw errors. Must be called after every libvirt
     * call.
     */
    protected void processError() throws LibvirtException {
        virConnect.processError();
    }

    /**
     * Sets the value of the secret
     *
     * @return 0 on success, -1 on failure.
     */
    public int setValue(String value) throws LibvirtException {
        int returnValue = libvirt.virSecretSetValue(VSP, value, new NativeLong(value.length()), 0);
        processError();
        return returnValue;
    }

    /**
     * Sets the value of the secret
     *
     * @return 0 on success, -1 on failure.
     */
    public int setValue(byte[] value) throws LibvirtException {
        int returnValue = libvirt.virSecretSetValue(VSP, value, new NativeLong(value.length), 0);
        processError();
        return returnValue;
    }

    /**
     * Undefines, but does not free, the Secret.
     *
     * @return 0 on success, -1 on failure.
     */
    public int undefine() throws LibvirtException {
        int returnValue = libvirt.virSecretUndefine(VSP);
        processError();
        return returnValue;
    }
}
