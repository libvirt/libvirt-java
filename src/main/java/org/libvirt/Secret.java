package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.SecretPointer;
import org.libvirt.jna.SizeT;
import org.libvirt.jna.SizeTByReference;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * A secret defined by libvirt
 */
public class Secret {

    /**
     * the native virSecretPtr.
     */
    SecretPointer vsp;

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private final Connect virConnect;

    Secret(final Connect virConnect, final SecretPointer vsp) {
        this.virConnect = virConnect;
        this.vsp = vsp;
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
        if (vsp != null) {
            success = processError(libvirt.virSecretFree(vsp));
            vsp = null;
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
        return processError(libvirt.virSecretGetUsageID(vsp));
    }

    public SecretUsageType getUsageType() throws LibvirtException {
        final int ret = processError(libvirt.virSecretGetUsageType(this.vsp));
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
        processError(libvirt.virSecretGetUUID(vsp, bytes));
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
        processError(libvirt.virSecretGetUUIDString(vsp, bytes));
        return Native.toString(bytes);
    }

    /**
     * Fetches the value of the secret as a string (note that
     * this may not always work and getByteValue() is more reliable)
     * This is just kept for backward compatibility
     *
     * @return the value of the secret, or null on failure.
     * @throws org.libvirt.LibvirtException
     */
    public String getValue() throws LibvirtException {
        String returnValue;
        try {
            returnValue = new String(getByteValue(), "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            returnValue = null;
        }
        return returnValue;
    }

    /**
     * Fetches the value of the secret as a byte array
     *
     * @return the value of the secret, or null on failure.
     * @throws org.libvirt.LibvirtException
     */
    public byte[] getByteValue() throws LibvirtException {
        SizeTByReference valueSize = new SizeTByReference();
        Pointer value = processError(libvirt.virSecretGetValue(vsp, valueSize, 0));
        ByteBuffer bb = value.getByteBuffer(0, valueSize.getValue());
        byte[] returnValue = new byte[bb.remaining()];
        bb.get(returnValue);
        return returnValue;
    }

    /**
     * Fetches an XML document describing attributes of the secret.
     *
     * @return the XML document
     * @throws org.libvirt.LibvirtException
     */
    public String getXMLDesc() throws LibvirtException {
        return processError(libvirt.virSecretGetXMLDesc(vsp, 0)).toString();
    }

    /**
     * Sets the value of the secret
     *
     * @param value
     * @return <em>ignore</em> (always 0)
     * @throws org.libvirt.LibvirtException
     */
    public int setValue(final String value) throws LibvirtException {
        return processError(libvirt.virSecretSetValue(vsp, value, new SizeT(value.length()), 0));
    }

    /**
     * Sets the value of the secret
     *
     * @param value
     * @return <em>ignore</em> (always 0)
     * @throws org.libvirt.LibvirtException
     */
    public int setValue(final byte[] value) throws LibvirtException {
        return processError(libvirt.virSecretSetValue(vsp, value, new SizeT(value.length), 0));
    }

    /**
     * Undefines, but does not free, the Secret.
     *
     * @return <em>ignore</em> (always 0)
     * @throws org.libvirt.LibvirtException
     */
    public int undefine() throws LibvirtException {
        return processError(libvirt.virSecretUndefine(vsp));
    }
}
