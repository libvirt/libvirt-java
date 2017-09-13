package org.libvirt;

import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.Library.libvirt;

import org.libvirt.jna.pointers.InterfacePointer;

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
     * Constructs an Interface object from an InterfacePointer, and a Connect
     * object.
     *
     * @param virConnect the Interfaces hypervisor
     * @param VIP        the native virInterfacePtr
     */
    Interface(Connect virConnect, InterfacePointer VIP) {
        this.virConnect = virConnect;
        this.VIP = VIP;
    }

    /**
     * Activate an interface (i.e. call "ifup").
     * <p>
     * If there was an open network config transaction at the time
     * this interface was defined (that is, if
     * virInterfaceChangeBegin() had been called), the interface will
     * be brought back down (and then undefined) if
     * virInterfaceChangeRollback() is called.
     *
     * @throws LibvirtException
     */
    public int create() throws LibvirtException {
        return processError(libvirt.virInterfaceCreate(VIP, 0));
    }

    /**
     * Deactivate an interface (i.e. call "ifdown").
     * <p>
     * This does not remove the interface from the config, and does
     * not free the associated virInterfacePtr object.
     * <p>
     * If there is an open network config transaction at the time this
     * interface is destroyed (that is, if virInterfaceChangeBegin()
     * had been called), and if the interface is later undefined and
     * then virInterfaceChangeRollback() is called, the restoral of
     * the interface definition will also bring the interface back
     * up.
     *
     * @throws LibvirtException
     */
    public int destroy() throws LibvirtException {
        return processError(libvirt.virInterfaceDestroy(VIP, 0));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this interface object. The running instance is kept alive. The data
     * structure is freed and should not be used thereafter.
     *
     * @return number of references left (>= 0)
     * @throws LibvirtException
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VIP != null) {
            success = processError(libvirt.virInterfaceFree(VIP));
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
        return processError(libvirt.virInterfaceGetMACString(VIP));
    }

    /**
     * Returns the name of the interface
     *
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virInterfaceGetName(VIP));
    }

    /**
     * Returns the XML description for theinterface
     *
     * @throws LibvirtException
     */
    public String getXMLDescription(int flags) throws LibvirtException {
        return processError(libvirt.virInterfaceGetXMLDesc(VIP, flags)).toString();
    }

    /**
     * Determine if the interface is currently running
     *
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        return processError(libvirt.virInterfaceIsActive(VIP));
    }

    /**
     * Undefine an interface, ie remove it from the config. This does not free
     * the associated virInterfacePtr object.
     *
     * @throws LibvirtException
     */
    public int undefine() throws LibvirtException {
        return processError(libvirt.virInterfaceUndefine(VIP));
    }
}
