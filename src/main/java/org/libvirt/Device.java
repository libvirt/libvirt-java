package org.libvirt;

import org.libvirt.jna.DevicePointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Pointer;

import com.sun.jna.Pointer;

/**
 * A device which is attached to a node
 */
public class Device {

    /**
     * the native virDomainPtr.
     */
    DevicePointer VDP;

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private Connect virConnect;

    /**
     * Constructs a Device object from a DevicePointer, and a Connect object.
     *
     * @param virConnect
     *            the Domain's hypervisor
     * @param VDP
     *            the native virDomainPtr
     */
    Device(Connect virConnect, DevicePointer VDP) {
        this.virConnect = virConnect;
        this.VDP = VDP;
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
        if (VDP != null) {
            success = processError(libvirt.virNodeDeviceDestroy(VDP));
            VDP = null;
        }

        return success;
    }

    /**
     * Dettach the node device from the node itself so that it may be assigned
     * to a guest domain.
     *
     * @throws LibvirtException
     */
    public int detach() throws LibvirtException {
        return processError(libvirt.virNodeDeviceDettach(VDP));
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
        if (VDP != null) {
            success = processError(libvirt.virNodeDeviceFree(VDP));
            VDP = null;
        }

        return success;
    }

    /**
     * Returns the name of the device
     *
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virNodeDeviceGetName(VDP));
    }

    /**
     * Returns the number of capabilities which the instance has.
     *
     * @throws LibvirtException
     */
    public int getNumberOfCapabilities() throws LibvirtException {
        return processError(libvirt.virNodeDeviceNumOfCaps(VDP));
    }

    /**
     * Returns the parent of the device
     *
     * @throws LibvirtException
     */
    public String getParent() throws LibvirtException {
        return processError(libvirt.virNodeDeviceGetParent(VDP));
    }

    /**
     * Returns the XML description of the device
     *
     * @throws LibvirtException
     */
    public String getXMLDescription() throws LibvirtException {
        return processError(libvirt.virNodeDeviceGetXMLDesc(VDP));
    }

    /**
     * List the capabilities of the device
     *
     * @throws LibvirtException
     */
    public String[] listCapabilities() throws LibvirtException {
        int maxCaps = getNumberOfCapabilities();

        if (maxCaps > 0) {
            Pointer[] ptrs = new Pointer[maxCaps];
            int got = processError(libvirt.virNodeDeviceListCaps(VDP, ptrs, maxCaps));

            return Library.toStringArray(ptrs, got);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * ReAttach a device to the node.
     *
     * @throws LibvirtException
     */
    public int reAttach() throws LibvirtException {
        return processError(libvirt.virNodeDeviceReAttach(VDP));
    }

    /**
     * Reset a previously dettached node device to the node before or after
     * assigning it to a guest.
     *
     * @throws LibvirtException
     */
    public int reset() throws LibvirtException {
        return processError(libvirt.virNodeDeviceReset(VDP));
    }
}
