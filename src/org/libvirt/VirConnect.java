package org.libvirt;

/**
 * The VirConnect object represents a connection to a local or remote hypervisor/driver.
 * 
 * @author stoty
 *
 */
public class VirConnect {

	// Load the native part
	static {
		System.loadLibrary("virt_jni");
		_virInitialize();
	}

	
	/**
	 * the native virConnectPtr.
	 */
	long VCP;

	private static native int _virInitialize();

	/**
	 * Construct a VirConnect object from a known native virConnectPtr
	 * For use when native libvirt returns a virConnectPtr, i.e. error handling.
	 * 
	 * @param VCP	the virConnectPtr pointing to an existing native virConnect structure
	 */
	VirConnect(long VCP) {
		this.VCP = VCP;
	}

	/**
	 * Constructs a VirConnect object from the supplied URI.
	 * 
	 * @param uri The connection URI
	 * @param readOnly Whether the connection is read-only
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/uri.html">The URI documentation</a>
	 */
	public VirConnect(String uri, boolean readOnly) throws LibvirtException {
		if (readOnly) {
			VCP = _openReadOnly(uri);
		} else {
			VCP = _open(uri);
		}
	}

	/**
	 * Constructs a VirConnect object from the supplied URI, 
	 * using the supplied authentication callback
	 * 
	 * @param uri The connection URI
	 * @param auth a VirConnectAuth object
	 * @param flags 
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/uri.html">The URI documentation</a>
	 */
	public VirConnect(String uri, VirConnectAuth auth, int flags) throws LibvirtException {	
		VCP = _openAuth(uri, auth, flags);
	}
	
	/**
	 * Constructs a read-write VirConnect object from the supplied URI. 
	 * 
	 * @param uri The connection URI
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/uri.html">The URI documentation</a>
	 */
	public VirConnect(String uri) throws LibvirtException {
		VCP = _open(uri);
	}
	
	public void finalize() throws LibvirtException {
		close();
	}

	
	/**
	 * Closes the connection to the hypervisor/driver. Calling any methods on the object after close() will result in an exception.
	 * 
	 * @throws LibvirtException
	 */
	public void close() throws LibvirtException {
		_close(VCP);
		// If leave an invalid pointer dangling around JVM crashes and burns if
		// someone tries to call a method on us
		// We rely on the underlying libvirt error handling to detect that it's called with a null virConnectPointer
		VCP = 0;
	}

	private native void _close(long VCP) throws LibvirtException;


	/**
	 * Provides capabilities of the hypervisor / driver.
	 * 
	 * @return an XML String describing the capabilities.
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/format.html#Capa1" >The XML format description</a>
	 * 
	 */
	public String getCapabilities() throws LibvirtException {
		return _getCapabilities(VCP);
	}

	private native String _getCapabilities(long VCP) throws LibvirtException;

	
	/**
	 * Returns the system hostname on which the hypervisor is running. 
	 * (the result of the gethostname(2) system call)
	 * If we are connected to a remote system, then this returns the hostname of the remote system.
	 * 
	 * @return the hostname
	 * @throws LibvirtException
	 */
	public String getHostName() throws LibvirtException {
		return _getHostName(VCP);
	}

	private native String _getHostName(long VCP) throws LibvirtException;

	
	/**
	 * Provides the maximum number of virtual CPUs supported for a guest VM of a specific type.
	 * The 'type' parameter here corresponds to the 'type' attribute in the <domain> element of the XML.
	 * 
	 * @param type
	 * @return the number of CPUs
	 * @throws LibvirtException
	 */
	public int getMaxVcpus(String type) throws LibvirtException {
		return _getMaxVcpus(VCP, type);
	}

	private native int _getMaxVcpus(long VCP, String type)
	throws LibvirtException;

	
	/**
	 * Gets the name of the Hypervisor software used.
	 * 
	 * @return the name
	 * @throws LibvirtException
	 */
	public String getType() throws LibvirtException {
		return _getType(VCP);
	}

	private native String _getType(long VCP) throws LibvirtException;

	
	/**
	 * Returns the URI (name) of the hypervisor connection. 
	 * Normally this is the same as or similar to the string passed to the virConnectOpen/virConnectOpenReadOnly call, 
	 * but the driver may make the URI canonical. 
	 * 
	 * @return the URI
	 * @throws LibvirtException
	 */
	public String getURI() throws LibvirtException {
		return _getURI(VCP);
	}

	private native String _getURI(long VCP) throws LibvirtException;

	
	/**
	 * Gets the version level of the Hypervisor running. 
	 * This may work only with hypervisor call, i.e. with priviledged access to the hypervisor, not with a Read-Only connection.
	 * If the version can't be extracted by lack of capacities returns 0.
	 * 
	 * @return major * 1,000,000 + minor * 1,000 + release
	 * @throws LibvirtException
	 */
	public long getVersion() throws LibvirtException {
		return _getVersion(VCP);
	}

	private native long _getVersion(long VCP) throws LibvirtException;

	
	/**
	 * Gets the version of the native libvirt library that the JNI part is linked to.
	 * 
//	 * @return major * 1,000,000 + minor * 1,000 + release
	 * @throws LibvirtException
	 */
	public long virGetLibVirVersion() throws LibvirtException {
		return _virGetLibVirVersion();
	}

	private native long _virGetLibVirVersion() throws LibvirtException;

	
	/**
	 * Returns the version of the hypervisor against which the library was compiled.
	 * The type parameter specified which hypervisor's version is returned
	 * 
	 * @param type
	 * @return major * 1,000,000 + minor * 1,000 + release
	 * @throws LibvirtException
	 */
	public long virGetHypervisorVersion(String type) throws LibvirtException {
		return _virGetHypervisorVersion(type);
	}

	private native long _virGetHypervisorVersion(String type)
	throws LibvirtException;

	
	/**
	 * Lists the names of the defined domains
	 * 
	 * @return an Array of Strings that contains the names of the defined domains
	 * @throws LibvirtException
	 */
	public String[] listDefinedDomains() throws LibvirtException {
		return _listDefinedDomains(VCP);
	}

	private native String[] _listDefinedDomains(long VCP)
	throws LibvirtException;

	
	/**
	 * Lists the inactive networks
	 * 
	 * @return an Array of Strings that contains the names of the inactive networks
	 * @throws LibvirtException
	 */
	public String[] listDefinedNetworks() throws LibvirtException {
		return _listDefinedNetworks(VCP);
	}

	private native String[] _listDefinedNetworks(long VCP)
	throws LibvirtException;

	
	/**
	 * Lists the active domains.
	 * 
	 * @return and array of the IDs of the active domains
	 * @throws LibvirtException
	 */
	public int[] listDomains() throws LibvirtException {
		return _listDomains(VCP);
	}

	private native int[] _listDomains(long VCP) throws LibvirtException;

	/**
	 * Lists the active networks.
	 * 
	 * @return an Array of Strings that contains the names of the active networks
	 * @throws LibvirtException
	 */
	public String[] listNetworks() throws LibvirtException {
		return _listNetworks(VCP);
	}

	private native String[] _listNetworks(long VCP) throws LibvirtException;

	
	/**
	 * Provides the number of inactive domains.
	 * 
	 * @return the number of inactive domains
	 * @throws LibvirtException
	 */
	public int numOfDefinedDomains() throws LibvirtException {
		return _numOfDefinedDomains(VCP);
	}

	private native int _numOfDefinedDomains(long VCP) throws LibvirtException;

	
	/**
	 * Provides the number of inactive networks.
	 * 
	 * @return the number of inactive networks
	 * @throws LibvirtException
	 */
	public int numOfDefinedNetworks() throws LibvirtException {
		return _numOfDefinedNetworks(VCP);
	}

	private native int _numOfDefinedNetworks(long VCP) throws LibvirtException;

	/**
	 * Provides the number of active domains.
	 * 
	 * @return the number of active domains
	 * @throws LibvirtException
	 */
	public int numOfDomains() throws LibvirtException {
		return _numOfDomains(VCP);
	}

	private native int _numOfDomains(long VCP) throws LibvirtException;

	
	/**
	 * Provides the number of active networks.
	 * 
	 * @return the number of active networks
	 * @throws LibvirtException
	 */
	public int numOfNetworks() throws LibvirtException {
		return _numOfNetworks(VCP);
	}

	private native int _numOfNetworks(long VCP) throws LibvirtException;

	// open
	private native long _open(String uri) throws LibvirtException;

	// openReadOnly
	private native long _openReadOnly(String uri) throws LibvirtException;

	// openAuth
	private native long _openAuth(String uri, VirConnectAuth auth, int flags) throws LibvirtException;
	
	// virNetwork stuff

	/**
	 * Looks up a network on the based on its name.
	 * 
	 * @param name name of the network
	 * @return The VirNetwork object found
	 * @throws LibvirtException
	 */
	public VirNetwork virNetworkLookupByName(String name)
	throws LibvirtException {
		return new VirNetwork(this, _virNetworkLookupByName(VCP, name));
	}

	private native long _virNetworkLookupByName(long VCP, String name)
	throws LibvirtException;


	/**
	 * Looks up a network based on its UUID represented as an int array.
	 * The UUID Array contains an unpacked representation of the UUID, each int contains only one byte.
	 * 
	 * @param UUID the UUID as an unpacked int array
	 * @return The VirNetwork object found
	 * @throws LibvirtException
	 */
	public VirNetwork virNetworkLookupByUUID(int[] UUID)
	throws LibvirtException {
		return new VirNetwork(this, _virNetworkLookupByUUID(VCP, UUID));
	}

	private native long _virNetworkLookupByUUID(long VCP, int[] UUID);

	/**
	 * Looks up a network based on its UUID represented as a String.
	 * 
	 * @param UUID the UUID in canonical String representation
	 * @return The VirNetwork object found
	 * @throws LibvirtException
	 */
	public VirNetwork virNetworkLookupByUUIDString(String UUID)
	throws LibvirtException {
		return new VirNetwork(this, _virNetworkLookupByUUIDString(VCP, UUID));
	}

	private native long _virNetworkLookupByUUIDString(long VCP, String UUID)
	throws LibvirtException;

	
	/**
	 * Creates and starts a new virtual network. 
	 * The properties of the network are based on an XML description similar to the one returned by virNetworkGetXMLDesc()
	 * 
	 * @param xmlDesc the Network Description
	 * @return the VirNetwork object representing the created network 
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/format.html#Net1" >The XML format description</a>
	 */
	public VirNetwork virNetworkCreateXML(String xmlDesc)
	throws LibvirtException {
		return new VirNetwork(this, _virNetworkCreateXML(VCP, xmlDesc));
	}

	private native long _virNetworkCreateXML(long VCP, String xmlDesc)
	throws LibvirtException;

	
	/**
	 * Defines a network, but does not create it.
	 * The properties of the network are based on an XML description similar to the one returned by virNetworkGetXMLDesc()
	 * 
	 * @param xmlDesc
	 * @return
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/format.html#Net1" >The XML format description</a>
	 */
	public VirNetwork virNetworkDefineXML(String xmlDesc)
	throws LibvirtException {
		return new VirNetwork(this, _virNetworkDefineXML(VCP, xmlDesc));
	}

	private native long _virNetworkDefineXML(long VCP, String xmlDesc)
	throws LibvirtException;

	/**
	 * Finds a domain based on the hypervisor ID number.
	 * 
	 * @param id the hypervisor id
	 * @return the VirDomain object
	 * @throws LibvirtException
	 */
	public VirDomain virDomainLookupByID(int id) throws LibvirtException {
		return new VirDomain(this, _virDomainLookupByID(VCP, id));
	}

	private native long _virDomainLookupByID(long VCP, int id)
	throws LibvirtException;

	/**
	 * Looks up a domain based on its name.
	 * 
	 * @param name the name of the domain
	 * @return the VirDomain object
	 * @throws LibvirtException
	 */
	public VirDomain virDomainLookupByName(String name) throws LibvirtException {
		return new VirDomain(this, _virDomainLookupByName(VCP, name));
	}

	private native long _virDomainLookupByName(long VCP, String name)
	throws LibvirtException;

	
	/**
	 * Looks up a domain  based on its UUID in array form.
	 * The UUID Array contains an unpacked representation of the UUID, each int contains only one byte.
	 * 
	 * @param UUID the UUID as an unpacked int array
	 * @return the VirDomain object
	 * @throws LibvirtException
	 */
	public VirDomain virDomainLookupByUUID(int[] UUID) throws LibvirtException {
		return new VirDomain(this, _virDomainLookupByUUID(VCP, UUID));
	}

	private native long _virDomainLookupByUUID(long VCP, int[] UUID)
	throws LibvirtException;

	/**
	 * Looks up a domain  based on its UUID in String form.
	 * 
	 * @param UUID the UUID in canonical String representation
	 * @return the VirDomain object
	 * @throws LibvirtException
	 */
	public VirDomain virDomainLookupByUUIDString(String UUID)
	throws LibvirtException {
		return new VirDomain(this, _virDomainLookupByUUIDString(VCP, UUID));
	}

	private native long _virDomainLookupByUUIDString(long VCP, String UUID)
	throws LibvirtException;

	/**
	 * Launches a new Linux guest domain.
	 * The domain is  based on an XML description similar to the one returned by virDomainGetXMLDesc().
	 * This function may require priviledged access to the hypervisor.
	 * 
	 * @param xmlDesc the Domain description in XML
	 * @param flags an optional set of flags (unused)
	 * @return the VirDomain object
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/format.html#Normal1" > The XML format description </a>
	 */
	public VirDomain virDomainCreateLinux(String xmlDesc, int flags)
	throws LibvirtException {
		return new VirDomain(this, _virDomainCreateLinux(VCP, xmlDesc, flags));
	}

	private native long _virDomainCreateLinux(long VCP, String xmlDesc,
			int flags) throws LibvirtException;

	/**
	 * Defines a domain, but does not start it
	 * 
	 * @param xmlDesc
	 * @return the VirDomain object
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/format.html#Normal1" > The XML format description </a>
	 */
	public VirDomain virDomainDefineXML(String xmlDesc) throws LibvirtException {
		return new VirDomain(this, _virDomainDefineXML(VCP, xmlDesc));
	}

	private native long _virDomainDefineXML(long VCP, String xmlDesc)
	throws LibvirtException;

	
	/**
	 * Restores a domain saved to disk by VirDomain.save().
	 * 
	 * @param from the path of the saved file on the remote host
	 * @throws LibvirtException
	 */
	public void restore(String from) throws LibvirtException {
		_virDomainRestore(VCP, from);
	}

	private native int _virDomainRestore(long VCP, String from)
	throws LibvirtException;

	/**
	 * Returns a VirNodeInfo object describing the hardware configuration of the node.
	 * 
	 * @return a VirNodeInfo object
	 * @throws LibvirtException
	 */
	public VirNodeInfo virNodeInfo() throws LibvirtException {
		return _virNodeInfo(VCP);
	}

	private native VirNodeInfo _virNodeInfo(long VCP) throws LibvirtException;

	/**
	 * change the amount of memory reserved to Domain0.
	 * Domain0 is the domain where the application runs. 
	 * This function may requires priviledged access to the hypervisor.
	 * 
	 * @param memory in kilobytes
	 * @throws LibvirtException
	 */
	public void setDom0Memory(long memory) throws LibvirtException {
		_setDom0Memory(memory);
	}

	private native int _setDom0Memory(long memory) throws LibvirtException;

}
