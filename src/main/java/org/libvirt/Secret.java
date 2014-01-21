package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.SecretPointer;
import org.libvirt.jna.SizeT;
import org.libvirt.jna.SizeTByReference;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;
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

    Secret(Connect virConnect, SecretPointer VSP) {
        this.virConnect = virConnect;
        this.VSP = VSP;
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the secret handle. The underlying secret continues to exist.
     *
     * @throws LibvirtException
     * @return <em>ignore</em> (always 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VSP != null) {
            success = processError(libvirt.virSecretFree(VSP));
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
        return processError(libvirt.virSecretGetUsageID(VSP));
    }

    public SecretUsageType getUsageType() throws LibvirtException {
        final int ret = processError(libvirt.virSecretGetUsageType(this.VSP));
        return Library.getConstant(SecretUsageType.class, ret);
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
        processError(libvirt.virSecretGetUUID(VSP, bytes));
        return Connect.convertUUIDBytes(bytes);
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
        processError(libvirt.virSecretGetUUIDString(VSP, bytes));
        return Native.toString(bytes);
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
        SizeTByReference value_size = new SizeTByReference();
        Pointer value = processError(libvirt.virSecretGetValue(VSP, value_size, 0));
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
        return processError(libvirt.virSecretGetXMLDesc(VSP, 0));
    }

    /**
     * Sets the value of the secret
     *
     * @return <em>ignore</em> (always 0)
     */
    public int setValue(String value) throws LibvirtException {
        return processError(libvirt.virSecretSetValue(VSP, value, new SizeT(value.length()), 0));
    }

    /**
     * Sets the value of the secret
     *
     * @return <em>ignore</em> (always 0)
     */
    public int setValue(byte[] value) throws LibvirtException {
        return processError(libvirt.virSecretSetValue(VSP, value, new SizeT(value.length), 0));
    }

    /**
     * Undefines, but does not free, the Secret.
     *
     * @return <em>ignore</em> (always 0)
     */
    public int undefine() throws LibvirtException {
        return processError(libvirt.virSecretUndefine(VSP));
    }
}
