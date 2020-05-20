package org.libvirt;

import org.libvirt.jna.CString;
import org.libvirt.jna.DevicePointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

/**
 * A device which is attached to a node
 */
public class Device {

    /**
     * the native virDomainPtr.
     */
    DevicePointer vdp;

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private final Connect virConnect;

    /**
     * Constructs a Device object from a DevicePointer, and a Connect object.
     *
     * @param virConnect
     *            the Domain's hypervisor
     * @param vdp
     *            the native virDomainPtr
     */
    Device(final Connect virConnect, final DevicePointer vdp) {
        this.virConnect = virConnect;
        this.vdp = vdp;
    }

    /**
     * Destroy the device object. The virtual device is removed from the host
     * operating system. This function may require privileged access.
     *
     * @throws LibvirtException
     * @return 0 for success, -1 for failure.
     */
    public int destroy() throws LibvirtException {
        int success = 0;
        if (vdp != null) {
            success = processError(libvirt.virNodeDeviceDestroy(vdp));
            vdp = null;
        }

        return success;
    }

    /**
     * Dettach the node device from the node itself so that it may be assigned
     * to a guest domain.
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int detach() throws LibvirtException {
        return processError(libvirt.virNodeDeviceDettach(vdp));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this device object. The running instance is kept alive. The data
     * structure is freed and should not be used thereafter.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0) for success, -1 for failure.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vdp != null) {
            success = processError(libvirt.virNodeDeviceFree(vdp));
            vdp = null;
        }

        return success;
    }

    /**
     * Returns the name of the device
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virNodeDeviceGetName(vdp));
    }

    /**
     * Returns the number of capabilities which the instance has.
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int getNumberOfCapabilities() throws LibvirtException {
        return processError(libvirt.virNodeDeviceNumOfCaps(vdp));
    }

    /**
     * Returns the parent of the device
     *
     * @return String or null
     * @throws LibvirtException
     */
    public String getParent() throws LibvirtException {
        return processError(libvirt.virNodeDeviceGetParent(vdp));
    }

    /**
     * Returns the XML description of the device
     *
     * @return String or null
     * @throws LibvirtException
     */
    public String getXMLDescription() throws LibvirtException {
        return processError(libvirt.virNodeDeviceGetXMLDesc(vdp, 0)).toString();
    }

    /**
     * List the capabilities of the device
     *
     * @return String or null
     * @throws LibvirtException
     */
    public String[] listCapabilities() throws LibvirtException {
        int maxCaps = getNumberOfCapabilities();

        if (maxCaps > 0) {
            CString[] strings = new CString[maxCaps];
            int got = processError(libvirt.virNodeDeviceListCaps(vdp, strings, maxCaps));

            return Library.toStringArray(strings, got);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * ReAttach a device to the node.
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int reAttach() throws LibvirtException {
        return processError(libvirt.virNodeDeviceReAttach(vdp));
    }

    /**
     * Reset a previously dettached node device to the node before or after
     * assigning it to a guest.
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int reset() throws LibvirtException {
        return processError(libvirt.virNodeDeviceReset(vdp));
    }
}
