package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.NetworkFilterPointer;

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

    /**
     * The libvirt connection from the hypervisor
     */
    protected Libvirt libvirt;

    public NetworkFilter(Connect virConnect, NetworkFilterPointer NFP) {
        this.NFP = NFP;
        this.virConnect = virConnect;
        libvirt = virConnect.libvirt;
    }

    @Override
    public void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the network filter handle. The underlying snapshot continues to
     * exist.
     *
     * @throws LibvirtException
     * @return 0 on success, or -1 on error.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (NFP != null) {
            success = libvirt.virNWFilterFree(NFP);
            processError();
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
        String returnValue = libvirt.virNWFilterGetName(NFP);
        processError();
        return returnValue;
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
        int success = libvirt.virNWFilterGetUUID(NFP, bytes);
        processError();
        int[] returnValue = new int[0];
        if (success == 0) {
            returnValue = Connect.convertUUIDBytes(bytes);
        }
        return returnValue;
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
        int success = libvirt.virNWFilterGetUUIDString(NFP, bytes);
        processError();
        String returnValue = null;
        if (success == 0) {
            returnValue = Native.toString(bytes);
        }
        return returnValue;
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
        String returnValue = libvirt.virNWFilterGetXMLDesc(NFP, 0);
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
     * undefine the network filter
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        libvirt.virNWFilterUndefine(NFP);
        processError();
    }
}
