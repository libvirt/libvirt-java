package org.libvirt;

import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.Library.libvirt;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.NetworkPointer;

/**
 * A network object defined by libvirt.
 */
public class Network {

    /**
     * The native virNetworkPtr
     */
    NetworkPointer VNP;

    /**
     * The Connect Object that represents the Hypervisor of this Network
     */
    protected Connect virConnect;

    /**
     * Constructs a Network object from a known native virNetworkPtr, and a
     * Connect object. For use when native libvirt returns a virConnectPtr, i.e.
     * error handling.
     *
     * @param virConnect
     * @param VNP
     */
    Network(Connect virConnect, NetworkPointer VNP) {
        this.virConnect = virConnect;
        this.VNP = VNP;
    }

    /**
     * Creates and starts this defined network. If the call succeeds the network
     * moves from the defined to the running networks pools.
     *
     * @throws LibvirtException
     */
    public void create() throws LibvirtException {
        processError(libvirt.virNetworkCreate(VNP));
    }

    /**
     * Destroy this network object. The running instance is shutdown if not down
     * already and all resources used by it are given back to the hypervisor.
     * The object becomes invalid and should not be used thereafter if the call
     * does not return an error. This function may require priviledged access
     *
     * @throws LibvirtException
     */
    public void destroy() throws LibvirtException {
        processError(libvirt.virNetworkDestroy(VNP));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this network object. The running instance is kept alive. The object
     * becomes invalid and should not be used thereafter if the call does not
     * return an error.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VNP != null) {
            success = processError(libvirt.virNetworkFree(VNP));
            VNP = null;
        }

        return success;
    }

    /**
     * Provides a boolean value indicating whether this network is configured to
     * be automatically started when the host machine boots.
     *
     * @return true if autostarted, false otherwise
     * @throws LibvirtException
     */
    public boolean getAutostart() throws LibvirtException {
        IntByReference autoStart = new IntByReference();
        processError(libvirt.virNetworkGetAutostart(VNP, autoStart));
        return (autoStart.getValue() != 0) ? true : false;
    }

    /**
     * Provides a bridge interface name to which a domain may connect a network
     * interface in order to join this network.
     *
     * @return the interface name
     * @throws LibvirtException
     */
    public String getBridgeName() throws LibvirtException {
        return processError(libvirt.virNetworkGetBridgeName(VNP)).toString();
    }

    /**
     * Provides the connection pointer associated with this network.
     *
     * @return the Connect object
     */
    public Connect getConnect() {
        return virConnect;
    }

    /**
     * Gets the public name for this network
     *
     * @return the public name
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virNetworkGetName(VNP));
    }

    /**
     * Gets the UUID for this network
     *
     * @return the UUID as an unpacked int array
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public int[] getUUID() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        processError(libvirt.virNetworkGetUUID(VNP, bytes));
        return Connect.convertUUIDBytes(bytes);
    }

    /**
     * Gets the UUID for a network as string.
     *
     * @return the UUID in canonical String format
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public String getUUIDString() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_STRING_BUFLEN];
        processError(libvirt.virNetworkGetUUIDString(VNP, bytes));
        return Native.toString(bytes);
    }

    /**
     * Provides an XML description of this network. The description may be
     * reused later to relaunch the network with
     * Virconnect.virNetworkCreateXML().
     *
     * @param flags
     *            and OR'ed set of extraction flags, not used yet
     * @return The XML representation of this network
     * @throws LibvirtException
     */
    public String getXMLDesc(int flags) throws LibvirtException {
        return processError(libvirt.virNetworkGetXMLDesc(VNP, flags)).toString();
    }

    /**
     * Determine if the network is currently running
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virNetworkIsActive">Libvirt
     *      Documentation</a>
     * @return 1 if running, 0 if inactive, -1 on error
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        return processError(libvirt.virNetworkIsActive(VNP));
    }

    /**
     * Determine if the network has a persistent configuration which means it
     * will still exist after shutting down
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virNetworkIsPersistent">Libvirt
     *      Documentation</a>
     * @return 1 if persistent, 0 if transient, -1 on error
     * @throws LibvirtException
     */
    public int isPersistent() throws LibvirtException {
        return processError(libvirt.virNetworkIsPersistent(VNP));
    }

    /**
     * Configures this network to be automatically started when the host machine
     * boots.
     *
     * @param autostart
     *            whether the network should be automatically started 0 or 1
     * @throws LibvirtException
     */
    public void setAutostart(boolean autostart) throws LibvirtException {
        int autoValue = autostart ? 1 : 0;
        processError(libvirt.virNetworkSetAutostart(VNP, autoValue));
    }

    /**
     * Undefines this network but does not stop it if it is running
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        processError(libvirt.virNetworkUndefine(VNP));
    }

}
