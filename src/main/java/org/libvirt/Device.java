package org.libvirt;

import org.libvirt.jna.DevicePointer;
import org.libvirt.jna.Libvirt;

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
     * The libvirt connection from the hypervisor
     */
    protected Libvirt libvirt;

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
        libvirt = virConnect.libvirt;
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
            success = libvirt.virNodeDeviceDestroy(VDP);
            processError();
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
        int num = libvirt.virNodeDeviceDettach(VDP);
        processError();
        return num;
    }

    @Override
    public void finalize() throws LibvirtException {
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
            success = libvirt.virNodeDeviceFree(VDP);
            processError();
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
        String name = libvirt.virNodeDeviceGetName(VDP);
        processError();
        return name;
    }

    /**
     * Returns the number of capabilities which the instance has.
     *
     * @throws LibvirtException
     */
    public int getNumberOfCapabilities() throws LibvirtException {
        int num = libvirt.virNodeDeviceNumOfCaps(VDP);
        processError();
        return num;
    }

    /**
     * Returns the parent of the device
     *
     * @throws LibvirtException
     */
    public String getParent() throws LibvirtException {
        String parent = libvirt.virNodeDeviceGetParent(VDP);
        processError();
        return parent;
    }

    /**
     * Returns the XML description of the device
     *
     * @throws LibvirtException
     */
    public String getXMLDescription() throws LibvirtException {
        String desc = libvirt.virNodeDeviceGetXMLDesc(VDP);
        processError();
        return desc;
    }

    /**
     * List the capabilities of the device
     *
     * @throws LibvirtException
     */
    public String[] listCapabilities() throws LibvirtException {
        int maxCaps = getNumberOfCapabilities();
        String[] names = new String[maxCaps];

        if (maxCaps > 0) {
            libvirt.virNodeDeviceListCaps(VDP, names, maxCaps);
            processError();
        }
        return names;
    }

    /**
     * Error handling logic to throw errors. Must be called after every libvirt
     * call.
     */
    protected void processError() throws LibvirtException {
        virConnect.processError();
    }

    /**
     * ReAttach a device to the node.
     *
     * @throws LibvirtException
     */
    public int reAttach() throws LibvirtException {
        int num = libvirt.virNodeDeviceReAttach(VDP);
        processError();
        return num;
    }

    /**
     * Reset a previously dettached node device to the node before or after
     * assigning it to a guest.
     *
     * @throws LibvirtException
     */
    public int reset() throws LibvirtException {
        int num = libvirt.virNodeDeviceReset(VDP);
        processError();
        return num;
    }
}
