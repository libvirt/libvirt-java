package org.libvirt;

public class VirNetwork {

	/**
	 * The native virNetworkPtr
	 */
	private long VNP;
	/**
	 * The VirConnect Object that represents the Hypervisor of this Network
	 */
	private VirConnect virConnect;

	/**
	 * Constructs a VirNetwork object from a known native virNetworkPtr, and a VirConnect object.
	 * For use when native libvirt returns a virConnectPtr, i.e. error handling.
	 *
	 * @param virConnect
	 * @param VNP
	 */
	VirNetwork(VirConnect virConnect, long VNP){
		this.virConnect = virConnect;
		this.VNP = VNP;
	}

	public void finalize() throws LibvirtException{
		free();
	}

	/**
	 * Provides an XML description of this network.
	 * The description may be reused later to relaunch the network with Virconnect.virNetworkCreateXML().
	 *
	 * @param flags and OR'ed set of extraction flags, not used yet
	 * @return The XML representation of this network
	 * @throws LibvirtException
	 */
	public String getXMLDesc(int flags) throws LibvirtException{
		return _getXMLDesc(VNP, flags);
	}

	private native String _getXMLDesc(long VNP, int flags) throws LibvirtException;

	/**
	 * Provides a boolean value indicating whether this network is configured to be automatically started when the host machine boots.
	 *
	 * @return true if autostarted, false otherwise
	 * @throws LibvirtException
	 */
	public boolean getAutostart() throws LibvirtException{
		return _getAutostart(VNP);
	}

	private native boolean _getAutostart(long VNP) throws LibvirtException;


	/**
	 * Configures this network to be automatically started when the host machine boots.
	 *
	 * @param autostart whether the network should be automatically started 0 or 1
	 * @throws LibvirtException
	 */
	public void setAutostart(boolean autostart) throws LibvirtException{
		_setAutostart(VNP, autostart);
	}

	private native int _setAutostart(long VNP, boolean autostart) throws LibvirtException;

	/**
	 * Provides a bridge interface name to which a domain may connect a network interface in order to join this network.
	 *
	 * @return the interface name
	 * @throws LibvirtException
	 */
	public String getBridgeName() throws LibvirtException{
		return _getBridgeName(VNP);
	}

	private native String _getBridgeName(long VNP) throws LibvirtException;


	/**
	 * Provides the connection pointer associated with this network.
	 *
	 * @return the VirConnect object
	 */
	public VirConnect getConnect(){
		return virConnect;
	}


	/**
	 * Gets the public name for this network
	 *
	 * @return the public name
	 * @throws LibvirtException
	 */
	public String getName() throws LibvirtException{
		return _getName(VNP);
	}

	private native String _getName(long VNP) throws LibvirtException;

	/**
	 * Gets the UUID for this network
	 *
	 * @return the UUID as an unpacked int array
	 * @throws LibvirtException
	 * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
	 */
	public int[] getUUID() throws LibvirtException{
		return _getUUID(VNP);
	}

	private native int[] _getUUID(long VNP) throws LibvirtException;

	/**
	 * Gets the UUID for a network as string.
	 *
	 * @return the UUID in canonical String format
	 * @throws LibvirtException
	 * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
	 */
	public String getUUIDString() throws LibvirtException{
		return _getUUIDString(VNP);
	}

	private native String _getUUIDString(long VNP) throws LibvirtException;

	/**
	 * Creates and starts this defined network.
	 * If the call succeeds the network moves from the defined to the running networks pools.
	 *
	 * @throws LibvirtException
	 */
	public void create() throws LibvirtException{
		_create(VNP);
	}

	private native int _create(long VNP) throws LibvirtException;

	/**
	 * Destroy this network object.
	 * The running instance is shutdown if not down already and all resources used by it are given back to the hypervisor.
	 * The object becomes invalid and should not be used thereafter if the call does not return an error.
	 * This function may require priviledged access
	 *
	 * @throws LibvirtException
	 */
	public void destroy() throws LibvirtException{
		_destroy(VNP);
	}

	private native int _destroy(long VNP) throws LibvirtException;

	/**
	 * Frees this network object.
	 * The running instance is kept alive.
	 * The object becomes invalid and should not be used thereafter if the call does not return an error.
	 *
	 * @throws LibvirtException
	 */
	public void free() throws LibvirtException{
		_free(VNP);
		VNP=0;
	}

	private native int _free(long VNP) throws LibvirtException;

	/**
	 * Undefines this network but does not stop it if it is running
	 *
	 * @throws LibvirtException
	 */
	public void undefine() throws LibvirtException{
		_undefine(VNP);
	}

	private native int _undefine(long VNP) throws LibvirtException;

}
