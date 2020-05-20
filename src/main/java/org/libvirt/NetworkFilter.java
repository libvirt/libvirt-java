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
    NetworkFilterPointer nfp;

    /**
     * The Connect Object that represents the Hypervisor of this Filter
     */
    private final Connect virConnect;

    public NetworkFilter(final Connect virConnect, final NetworkFilterPointer nfp) {
        this.nfp = nfp;
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
        if (nfp != null) {
            success = processError(libvirt.virNWFilterFree(nfp));
            nfp = null;
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
        return processError(libvirt.virNWFilterGetName(nfp));
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
        processError(libvirt.virNWFilterGetUUID(nfp, bytes));
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
        processError(libvirt.virNWFilterGetUUIDString(nfp, bytes));
        return Native.toString(bytes);
    }

    /**
     * Fetches an XML document describing attributes of the network filter.
     *
     * @throws org.libvirt.LibvirtException
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virNWFilterGetXMLDesc">Libvirt
     *      Documentation</a>
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        return processError(libvirt.virNWFilterGetXMLDesc(nfp, 0)).toString();
    }

    /**
     * undefine the network filter
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        processError(libvirt.virNWFilterUndefine(nfp));
    }
}
