package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.NetworkFilterPointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;

public class NetworkFilter {
    /**
     * the native virNWFilterPtr.
     */
    NetworkFilterPointer NFP;

    /**
     * The Connect Object that represents the Hypervisor of this Filter
     */
    private Connect virConnect;

    public NetworkFilter(Connect virConnect, NetworkFilterPointer NFP) {
        this.NFP = NFP;
        this.virConnect = virConnect;
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the network filter handle. The underlying snapshot continues to
     * exist.
     *
     * @throws LibvirtException
     * @return <em>ignore</em> (always 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (NFP != null) {
            success = processError(libvirt.virNWFilterFree(NFP));
            NFP = null;
        }

        return success;
    }

    /**
     * Gets the public name for this network filter
     *
     * @return the name
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virNWFilterGetName(NFP));
    }

    /**
     * Get the UUID for this network filter.
     *
     * @return the UUID as an unpacked int array
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public int[] getUUID() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        processError(libvirt.virNWFilterGetUUID(NFP, bytes));
        return Connect.convertUUIDBytes(bytes);
    }

    /**
     * Gets the UUID for this network filter as string.
     *
     * @return the UUID in canonical String format
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public String getUUIDString() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_STRING_BUFLEN];
        processError(libvirt.virNWFilterGetUUIDString(NFP, bytes));
        return Native.toString(bytes);
    }

    /**
     * Fetches an XML document describing attributes of the network filter.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virNWFilterGetXMLDesc">Libvirt
     *      Documentation</a>
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        return processError(libvirt.virNWFilterGetXMLDesc(NFP, 0));
    }

    /**
     * undefine the network filter
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        processError(libvirt.virNWFilterUndefine(NFP));
    }
}
