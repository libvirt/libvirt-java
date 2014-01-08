package org.libvirt;

import org.libvirt.jna.InterfacePointer;
import static org.libvirt.Library.libvirt;

import com.sun.jna.Pointer;

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
     * @param virConnect
     *            the Interfaces hypervisor
     * @param VIP
     *            the native virInterfacePtr
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
        int returnValue = libvirt.virInterfaceCreate(VIP);
        processError();
        return returnValue;
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
        int returnValue = libvirt.virInterfaceDestroy(VIP);
        processError();
        return returnValue;
    }

    @Override
    protected void finalize() throws LibvirtException {
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
        Pointer xml = libvirt.virInterfaceGetXMLDesc(VIP, flags);
        processError();
        try {
            return Library.getString(xml);
        } finally {
            Library.free(xml);
        }
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
