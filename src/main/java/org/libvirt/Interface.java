package org.libvirt;

import org.libvirt.jna.InterfacePointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

/**
 * A device which is attached to a node
 */
public class Interface {

    /**
     * Get XML Flag: dump inactive interface information
     */
    public static final int VIR_INTERFACE_XML_INACTIVE = 1;

    /**
     * the native virInterfacePtr.
     */
    InterfacePointer vip;

    /**
     * The Connect Object that represents the Hypervisor of this Interface
     */
    private final Connect virConnect;

    /**
     * Constructs an Interface object from an InterfacePointer, and a Connect
     * object.
     *
     * @param virConnect
     *            the Interfaces hypervisor
     * @param vip
     *            the native virInterfacePtr
     */
    Interface(final Connect virConnect, final InterfacePointer vip) {
        this.virConnect = virConnect;
        this.vip = vip;
    }

    /**
     * Activate an interface (i.e. call "ifup").<p>
     * If there was an open network config transaction at the time
     * this interface was defined (that is, if
     * virInterfaceChangeBegin() had been called), the interface will
     * be brought back down (and then undefined) if
     * virInterfaceChangeRollback() is called.
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int create() throws LibvirtException {
        return processError(libvirt.virInterfaceCreate(vip, 0));
    }

    /**
     * Deactivate an interface (i.e. call "ifdown").<p>
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
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int destroy() throws LibvirtException {
        return processError(libvirt.virInterfaceDestroy(vip, 0));
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
     * @return number of references left (>= 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vip != null) {
            success = processError(libvirt.virInterfaceFree(vip));
            vip = null;
        }

        return success;
    }

    /**
     * Returns the mac string of the interface
     *
     * @return String or null
     * @throws LibvirtException
     */
    public String getMACString() throws LibvirtException {
        return processError(libvirt.virInterfaceGetMACString(vip));
    }

    /**
     * Returns the name of the interface
     *
     * @return String or null
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virInterfaceGetName(vip));
    }

    /**
     * Returns the XML description for theinterface
     *
     * @param flags
     * @return String or null
     * @throws LibvirtException
     */
    public String getXMLDescription(final int flags) throws LibvirtException {
        return processError(libvirt.virInterfaceGetXMLDesc(vip, flags)).toString();
    }

    /**
     * Determine if the interface is currently running
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virInterfaceIsActive">Libvirt
     *      Documentation</a>
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        return processError(libvirt.virInterfaceIsActive(vip));
    }

    /**
     * Undefine an interface, ie remove it from the config. This does not free
     * the associated virInterfacePtr object.
     *
     * @return 0 on success or -1 on error
     * @throws LibvirtException
     */
    public int undefine() throws LibvirtException {
        return processError(libvirt.virInterfaceUndefine(vip));
    }
}
