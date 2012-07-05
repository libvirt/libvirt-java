package org.libvirt;

import org.libvirt.jna.InterfacePointer;
import org.libvirt.jna.Libvirt;

/**
 * A device which is attached to a node
 */
public class Interface {

    /**
     * Get XML Flag: dump inactive interface information
     */
    public static int VIR_INTERFACE_XML_INACTIVE = 1;

    /**
     * the native virInterfacePtr.
     */
    InterfacePointer VIP;

    /**
     * The Connect Object that represents the Hypervisor of this Interface
     */
    private Connect virConnect;

    /**
     * The libvirt connection from the hypervisor
     */
    protected Libvirt libvirt;

    /**
     * Constructs an Interface object from an InterfacePointer, and a Connect
     * object.
     *
     * @param virConnect
     *            the Interfaces hypervisor
     * @param VIP
     *            the native virInterfacePtr
     */
    Interface(Connect virConnect, InterfacePointer VIP) {
        this.virConnect = virConnect;
        this.VIP = VIP;
        libvirt = virConnect.libvirt;
    }

    /**
     * Create and start a defined network. If the call succeed the network moves
     * from the defined to the running networks pools.
     *
     * @throws LibvirtException
     */
    public int create() throws LibvirtException {
        int returnValue = libvirt.virInterfaceCreate(VIP);
        processError();
        return returnValue;
    }

    /**
     * Destroy the network object. The running instance is shutdown if not down
     * already and all resources used by it are given back to the hypervisor.
     *
     * @throws LibvirtException
     */
    public int destroy() throws LibvirtException {
        int returnValue = libvirt.virInterfaceDestroy(VIP);
        processError();
        return returnValue;
    }

    @Override
    public void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this interface object. The running instance is kept alive. The data
     * structure is freed and should not be used thereafter.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0) for success, -1 for failure.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VIP != null) {
            success = libvirt.virInterfaceFree(VIP);
            processError();
            VIP = null;
        }

        return success;
    }

    /**
     * Returns the mac string of the interface
     *
     * @throws LibvirtException
     */
    public String getMACString() throws LibvirtException {
        String name = libvirt.virInterfaceGetMACString(VIP);
        processError();
        return name;
    }

    /**
     * Returns the name of the interface
     *
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        String name = libvirt.virInterfaceGetName(VIP);
        processError();
        return name;
    }

    /**
     * Returns the XML description for theinterface
     *
     * @throws LibvirtException
     */
    public String getXMLDescription(int flags) throws LibvirtException {
        String xml = libvirt.virInterfaceGetXMLDesc(VIP, flags);
        processError();
        return xml;
    }

    /**
     * Determine if the interface is currently running
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virInterfaceIsActive">Libvirt
     *      Documentation</a>
     * @return 1 if running, 0 if inactive, -1 on error
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        int returnValue = libvirt.virInterfaceIsActive(VIP);
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
     * Undefine an interface, ie remove it from the config. This does not free
     * the associated virInterfacePtr object.
     *
     * @throws LibvirtException
     */
    public int undefine() throws LibvirtException {
        int returnValue = libvirt.virInterfaceUndefine(VIP);
        processError();
        return returnValue;
    }
}
