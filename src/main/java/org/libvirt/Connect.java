package org.libvirt;

import java.util.UUID;

import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.DevicePointer;
import org.libvirt.jna.DomainPointer;
import org.libvirt.jna.InterfacePointer;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.NetworkFilterPointer;
import org.libvirt.jna.NetworkPointer;
import org.libvirt.jna.SecretPointer;
import org.libvirt.jna.StoragePoolPointer;
import org.libvirt.jna.StorageVolPointer;
import org.libvirt.jna.StreamPointer;
import org.libvirt.jna.virConnectAuth;
import org.libvirt.jna.virNodeInfo;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.LongByReference;

/**
 * The Connect object represents a connection to a local or remote
 * hypervisor/driver.
 *
 * @author stoty
 */
public class Connect {

    // Load the native part
    static {
        Libvirt.INSTANCE.virInitialize();
        try {
            ErrorHandler.processError(Libvirt.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new connection object from the domain. If all you want is the
     * existing domain's connection, use the getConnection method directly. Thie
     * method returns a new connection.
     *
     * @param domain
     * @return a new connection
     */
    public static Connect connectionForDomain(Domain domain) {
        ConnectionPointer conn = Libvirt.INSTANCE.virDomainGetConnect(domain.VDP);
        return new Connect(conn);
    }

    /**
     * Creates a new connection object from the network. If all you want is the
     * existing network's connection, use the getConnection method directly.
     * Thie method returns a new connection.
     *
     * @param network
     * @return a new connection
     */
    public static Connect connectionForNetwork(Network network) {
        ConnectionPointer conn = Libvirt.INSTANCE.virNetworkGetConnect(network.VNP);
        return new Connect(conn);
    }

    /**
     * Creates a new connection object from the network. If all you want is the
     * existing network's connection, use the getConnection method directly.
     * Thie method returns a new connection.
     *
     * @param secret
     * @return a new connection
     */
    public static Connect connectionForSecret(Secret secret) {
        ConnectionPointer conn = Libvirt.INSTANCE.virSecretGetConnect(secret.VSP);
        return new Connect(conn);
    }

    /**
     * Get the version of a connection.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectGetLibVersion">Libvirt
     *      Documentation</a>
     * @param conn
     *            the connection to use.
     * @return -1 in case of failure, versions have the format major * 1,000,000
     *         + minor * 1,000 + release.
     */
    public static long connectionVersion(Connect conn) {
        LongByReference libVer = new LongByReference();
        int result = Libvirt.INSTANCE.virConnectGetLibVersion(conn.VCP, libVer);
        return result != -1 ? libVer.getValue() : -1;
    }

    /**
     * Helper function to convert bytes into ints for the UUID calls
     */
    public static int[] convertUUIDBytes(byte bytes[]) {
        int[] returnValue = new int[Libvirt.VIR_UUID_BUFLEN];
        for (int x = 0; x < Libvirt.VIR_UUID_BUFLEN; x++) {
            // For some reason, the higher bytes come back wierd.
            // We only want the lower 2 bytes.
            returnValue[x] = (bytes[x] & 255);
        }
        return returnValue;
    }

    /**
     * Helper function to convert UUIDs into a stirng for the UUID calls
     */
    public static byte[] createUUIDBytes(int[] UUID) {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        for (int x = 0; x < Libvirt.VIR_UUID_BUFLEN; x++) {
            bytes[x] = (byte) UUID[x];
        }
        return bytes;
    }

    /**
     * Sets the error function to a user defined callback
     *
     * @param callback
     *            a Class to perform the callback
     */
    public static void setErrorCallback(Libvirt.VirErrorCallback callback) throws LibvirtException {
        Libvirt.INSTANCE.virSetErrorFunc(null, callback);
        ErrorHandler.processError(Libvirt.INSTANCE);
    }

    /**
     * The native virConnectPtr.
     */
    protected ConnectionPointer VCP;

    /**
     * The libvirt library
     */
    Libvirt libvirt = Libvirt.INSTANCE;

    /**
     * Protected constructor to return a Connection with ConnectionPointer
     */
    Connect(ConnectionPointer ptr) {
        VCP = ptr;
    }

    /**
     * Construct a Connect object from a known native virConnectPtr For use when
     * native libvirt returns a virConnectPtr, i.e. error handling.
     *
     * @param VCP
     *            the virConnectPtr pointing to an existing native virConnect
     *            structure
     */
    @Deprecated
    Connect(long VCP) {
        throw new RuntimeException("No longer supported");
    }

    /**
     * Constructs a read-write Connect object from the supplied URI.
     *
     * @param uri
     *            The connection URI
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/uri.html">The URI documentation</a>
     */
    public Connect(String uri) throws LibvirtException {
        VCP = libvirt.virConnectOpen(uri);
        // Check for an error
        processError();
        ErrorHandler.processError(Libvirt.INSTANCE);
    }

    /**
     * Constructs a Connect object from the supplied URI.
     *
     * @param uri
     *            The connection URI
     * @param readOnly
     *            Whether the connection is read-only
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/uri.html">The URI documentation</a>
     */
    public Connect(String uri, boolean readOnly) throws LibvirtException {
        if (readOnly) {
            VCP = libvirt.virConnectOpenReadOnly(uri);
        } else {
            VCP = libvirt.virConnectOpen(uri);
        }
        // Check for an error
        processError();
        ErrorHandler.processError(Libvirt.INSTANCE);
    }

    /**
     * Constructs a Connect object from the supplied URI, using the supplied
     * authentication callback
     *
     * @param uri
     *            The connection URI
     * @param auth
     *            a ConnectAuth object
     * @param flags
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/uri.html">The URI documentation</a>
     */
    public Connect(String uri, ConnectAuth auth, int flags) throws LibvirtException {
        virConnectAuth vAuth = new virConnectAuth();
        vAuth.cb = auth;
        vAuth.cbdata = null;
        vAuth.ncredtype = auth.credType.length;
        int[] authInts = new int[vAuth.ncredtype];

        for (int x = 0; x < vAuth.ncredtype; x++) {
            authInts[x] = auth.credType[x].mapToInt();
        }

        Memory mem = new Memory(4 * vAuth.ncredtype);
        mem.write(0, authInts, 0, vAuth.ncredtype);
        vAuth.credtype = mem.share(0);

        VCP = libvirt.virConnectOpenAuth(uri, vAuth, flags);
        // Check for an error
        processError();
        ErrorHandler.processError(Libvirt.INSTANCE);
    }

    /**
     * Computes the most feature-rich CPU which is compatible with all given
     * host CPUs.
     *
     * @param xmlCPUs
     *            array of XML descriptions of host CPUs
     * @return XML description of the computed CPU or NULL on error.
     * @throws LibvirtException
     */
    public String baselineCPU(String[] xmlCPUs) throws LibvirtException {
        String returnValue = libvirt.virConnectBaselineCPU(VCP, xmlCPUs, xmlCPUs.length, 0);
        processError();
        return returnValue;
    }

    /**
     * Closes the connection to the hypervisor/driver. Calling any methods on
     * the object after close() will result in an exception.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0) for success, -1 for failure.
     */
    public int close() throws LibvirtException {
        int success = 0;
        if (VCP != null) {
            success = libvirt.virConnectClose(VCP);
            processError();
            // If leave an invalid pointer dangling around JVM crashes and burns
            // if someone tries to call a method on us
            // We rely on the underlying libvirt error handling to detect that
            // it's called with a null virConnectPointer
            VCP = null;
        }
        return success;
    }

    /**
     * Compares the given CPU description with the host CPU
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectCompareCPU">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     * @return comparison result according to enum CPUCompareResult
     * @throws LibvirtException
     */
    public CPUCompareResult compareCPU(String xmlDesc) throws LibvirtException {
        int rawResult = libvirt.virConnectCompareCPU(VCP, xmlDesc, 0);
        processError();
        return CPUCompareResult.get(rawResult);
    }

    /**
     * Create a new device on the VM host machine, for example, virtual HBAs
     * created using vport_create.
     *
     * @param xmlDesc
     *            the device to create
     * @return the Device object
     * @throws LibvirtException
     */
    public Device deviceCreateXML(String xmlDesc) throws LibvirtException {
        Device returnValue = null;
        DevicePointer ptr = libvirt.virNodeDeviceCreateXML(VCP, xmlDesc, 0);
        processError();
        if (ptr != null) {
            returnValue = new Device(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a device based on its unique name
     *
     * @param name
     *            name of device to fetch
     * @return Device object
     * @throws LibvirtException
     */
    public Device deviceLookupByName(String name) throws LibvirtException {
        DevicePointer ptr = libvirt.virNodeDeviceLookupByName(VCP, name);
        processError();
        return new Device(this, ptr);
    }

    /**
     * Launches a new Linux guest domain. The domain is based on an XML
     * description similar to the one returned by virDomainGetXMLDesc(). This
     * function may require priviledged access to the hypervisor.
     *
     * @param xmlDesc
     *            the Domain description in XML
     * @param flags
     *            an optional set of flags (unused)
     * @return the Domain object
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Normal1" > The XML format
     *      description </a>
     */
    public Domain domainCreateLinux(String xmlDesc, int flags) throws LibvirtException {
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainCreateLinux(VCP, xmlDesc, flags);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Launch a new guest domain, based on an XML description
     *
     * @param xmlDesc
     * @return the Domain object
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Normal1" > The XML format
     *      description </a>
     */
    public Domain domainCreateXML(String xmlDesc, int flags) throws LibvirtException {
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainCreateXML(VCP, xmlDesc, flags);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Defines a domain, but does not start it
     *
     * @param xmlDesc
     * @return the Domain object
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Normal1" > The XML format
     *      description </a>
     */
    public Domain domainDefineXML(String xmlDesc) throws LibvirtException {
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainDefineXML(VCP, xmlDesc);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Removes an event callback.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventDeregisterAny">Libvirt
     *      Documentation</a>
     * @param callbackID
     *            the callback to deregister
     * @return 0 on success, -1 on failure
     * @throws LibvirtException
     */
    public int domainEventDeregisterAny(int callbackID) throws LibvirtException {
        int returnValue = libvirt.virConnectDomainEventDeregisterAny(VCP, callbackID);
        processError();
        return returnValue;
    }

    /**
     * Adds a callback to receive notifications of arbitrary domain events
     * occurring on a domain.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny">Libvirt
     *      Documentation</a>
     * @param domain
     *            option domain to limit the events monitored
     * @param eventId
     *            the events to monitor
     * @param cb
     *            the callback function to use.
     * @return . The return value from this method is a positive integer
     *         identifier for the callback. -1 if an error
     * @throws LibvirtException
     */
    public int domainEventRegisterAny(Domain domain, int eventId, Libvirt.VirConnectDomainEventGenericCallback cb)
            throws LibvirtException {
        DomainPointer ptr = domain == null ? null : domain.VDP;
        int returnValue = libvirt.virConnectDomainEventRegisterAny(VCP, ptr, eventId, cb, null, null);
        processError();
        return returnValue;
    }

    /**
     * Finds a domain based on the hypervisor ID number.
     *
     * @param id
     *            the hypervisor id
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByID(int id) throws LibvirtException {
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainLookupByID(VCP, id);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Looks up a domain based on its name.
     *
     * @param name
     *            the name of the domain
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByName(String name) throws LibvirtException {
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainLookupByName(VCP, name);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Looks up a domain based on its UUID in array form. The UUID Array
     * contains an unpacked representation of the UUID, each int contains only
     * one byte.
     *
     * @param UUID
     *            the UUID as an unpacked int array
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainLookupByUUID(VCP, uuidBytes);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a domain based on its globally unique id
     *
     * @param uuid
     *            a java UUID
     * @return a new domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByUUID(UUID uuid) throws LibvirtException {
        return domainLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a domain based on its UUID in String form.
     *
     * @param UUID
     *            the UUID in canonical String representation
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByUUIDString(String UUID) throws LibvirtException {
        Domain returnValue = null;
        DomainPointer ptr = libvirt.virDomainLookupByUUIDString(VCP, UUID);
        processError();
        if (ptr != null) {
            returnValue = new Domain(this, ptr);
        }
        return returnValue;
    }

    /**
     * Reads a native XML configuration document, and generates generates a
     * domain configuration file describing the domain. The format of the native
     * data is hypervisor dependant.
     *
     * @return domain XML as String, or {@code null} on error
     * @throws LibvirtException
     */
    public String domainXMLFromNative(String nativeFormat, String nativeConfig, int flags) throws LibvirtException {
        String returnValue = libvirt.virConnectDomainXMLFromNative(VCP, nativeFormat, nativeConfig, 0);
        processError();
        return returnValue;
    }

    /**
     * Reads a domain XML configuration document, and generates generates a
     * native configuration file describing the domain. The format of the native
     * data is hypervisor dependant.
     *
     * @return domain XML as String, or {@code null} on error
     * @throws LibvirtException
     */
    public String domainXMLToNative(String nativeFormat, String domainXML, int flags) throws LibvirtException {
        String returnValue = libvirt.virConnectDomainXMLToNative(VCP, nativeFormat, domainXML, 0);
        processError();
        return returnValue;
    }

    @Override
    public void finalize() throws LibvirtException {
        close();
    }

    /**
     * Talks to a storage backend and attempts to auto-discover the set of
     * available storage pool sources. e.g. For iSCSI this would be a set of
     * iSCSI targets. For NFS this would be a list of exported paths. The
     * srcSpec (optional for some storage pool types, e.g. local ones) is an
     * instance of the storage pool&apos;s source element specifying where to
     * look for the pools. srcSpec is not required for some types (e.g., those
     * querying local storage resources only)
     *
     * @param type
     *            type of storage pool to discover
     * @param srcSpecs
     *            XML document specifying discovery sourc
     * @param flags
     *            unused
     * @return an xml document consisting of a SourceList element containing a
     *         source document appropriate to the given pool type for each
     *         discovered source.
     * @throws LibvirtException
     */
    public String findStoragePoolSources(String type, String srcSpecs, int flags) throws LibvirtException {
        String returnValue = libvirt.virConnectFindStoragePoolSources(VCP, type, srcSpecs, flags);
        processError();
        return returnValue;
    }

    /**
     * Provides capabilities of the hypervisor / driver.
     *
     * @return an XML String describing the capabilities.
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Capa1" >The XML format
     *      description</a>
     */
    public String getCapabilities() throws LibvirtException {
        String returnValue = libvirt.virConnectGetCapabilities(VCP);
        processError();
        return returnValue;
    }

    /**
     * NUMA Support
     */
    public long getCellsFreeMemory(int startCells, int maxCells) throws LibvirtException {
        LongByReference returnValue = new LongByReference();
        libvirt.virNodeGetCellsFreeMemory(VCP, returnValue, startCells, maxCells);
        processError();
        return returnValue.getValue();
    }

    /**
     * Returns the free memory for the connection
     */
    public long getFreeMemory() throws LibvirtException {
        long returnValue = 0;
        returnValue = libvirt.virNodeGetFreeMemory(VCP);
        processError();
        return returnValue;
    }

    /**
     * Returns the system hostname on which the hypervisor is running. (the
     * result of the gethostname(2) system call) If we are connected to a remote
     * system, then this returns the hostname of the remote system.
     *
     * @return the hostname
     * @throws LibvirtException
     */
    public String getHostName() throws LibvirtException {
        String returnValue = libvirt.virConnectGetHostname(VCP);
        processError();
        return returnValue;

    }

    /**
     * Returns the version of the hypervisor against which the library was
     * compiled. The type parameter specified which hypervisor's version is
     * returned
     *
     * @param type
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     */
    public long getHypervisorVersion(String type) throws LibvirtException {
        LongByReference libVer = new LongByReference();
        LongByReference typeVer = new LongByReference();
        libvirt.virGetVersion(libVer, type, typeVer);
        processError();
        return libVer.getValue();
    }

    /**
     * Gets the version of the native libvirt library that the JNI part is
     * linked to.
     *
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     */
    public long getLibVirVersion() throws LibvirtException {
        LongByReference libVer = new LongByReference();
        LongByReference typeVer = new LongByReference();
        libvirt.virGetVersion(libVer, null, typeVer);
        processError();
        return libVer.getValue();
    }

    /**
     * Provides the maximum number of virtual CPUs supported for a guest VM of a
     * specific type. The 'type' parameter here corresponds to the 'type'
     * attribute in the <domain> element of the XML.
     *
     * @param type
     * @return the number of CPUs
     * @throws LibvirtException
     */
    public int getMaxVcpus(String type) throws LibvirtException {
        int returnValue = libvirt.virConnectGetMaxVcpus(VCP, type);
        processError();
        return returnValue;
    }

    /**
     * Gets the name of the Hypervisor software used.
     *
     * @return the name
     * @throws LibvirtException
     */
    public String getType() throws LibvirtException {
        String returnValue = libvirt.virConnectGetType(VCP);
        processError();
        return returnValue;
    }

    /**
     * Returns the URI (name) of the hypervisor connection. Normally this is the
     * same as or similar to the string passed to the
     * virConnectOpen/virConnectOpenReadOnly call, but the driver may make the
     * URI canonical.
     *
     * @return the URI
     * @throws LibvirtException
     */
    public String getURI() throws LibvirtException {
        String returnValue = libvirt.virConnectGetURI(VCP);
        processError();
        return returnValue;
    }

    /**
     * Gets the version level of the Hypervisor running. This may work only with
     * hypervisor call, i.e. with priviledged access to the hypervisor, not with
     * a Read-Only connection. If the version can't be extracted by lack of
     * capacities returns 0.
     *
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     */
    public long getVersion() throws LibvirtException {
        LongByReference hvVer = new LongByReference();
        libvirt.virConnectGetVersion(VCP, hvVer);
        processError();
        return hvVer.getValue();
    }

    /**
     * Define an interface (or modify existing interface configuration)
     *
     * @param xmlDesc
     *            the interface to create
     * @return the Interface object
     * @throws LibvirtException
     */
    public Interface interfaceDefineXML(String xmlDesc) throws LibvirtException {
        Interface returnValue = null;
        InterfacePointer ptr = libvirt.virInterfaceDefineXML(VCP, xmlDesc, 0);
        processError();
        if (ptr != null) {
            returnValue = new Interface(this, ptr);
        }
        return returnValue;
    }

    /**
     * Try to lookup an interface on the given hypervisor based on its MAC.
     *
     * @throws LibvirtException
     */
    public Interface interfaceLookupByMACString(String mac) throws LibvirtException {
        Interface returnValue = null;
        InterfacePointer ptr = libvirt.virInterfaceLookupByMACString(VCP, mac);
        processError();
        if (ptr != null) {
            returnValue = new Interface(this, ptr);
        }
        return returnValue;
    }

    /**
     * Try to lookup an interface on the given hypervisor based on its name.
     *
     * @throws LibvirtException
     */
    public Interface interfaceLookupByName(String name) throws LibvirtException {
        Interface returnValue = null;
        InterfacePointer ptr = libvirt.virInterfaceLookupByName(VCP, name);
        processError();
        if (ptr != null) {
            returnValue = new Interface(this, ptr);
        }
        return returnValue;
    }

    /**
     * Determine if the connection is encrypted
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectIsEncrypted">Libvirt
     *      Documentation</a>
     * @return 1 if encrypted, 0 if not encrypted, -1 on error
     * @throws LibvirtException
     */
    public int isEncrypted() throws LibvirtException {
        int returnValue = libvirt.virConnectIsEncrypted(VCP);
        processError();
        return returnValue;
    }

    /**
     * Determine if the connection is secure
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectIsSecure">Libvirt
     *      Documentation</a>
     * @return 1 if secure, 0 if not secure, -1 on error
     * @throws LibvirtException
     */
    public int isSecure() throws LibvirtException {
        int returnValue = libvirt.virConnectIsSecure(VCP);
        processError();
        return returnValue;
    }

    /**
     * Lists the names of the defined but inactive domains
     *
     * @return an Array of Strings that contains the names of the defined
     *         domains currently inactive
     * @throws LibvirtException
     */
    public String[] listDefinedDomains() throws LibvirtException {
        int maxnames = numOfDefinedDomains();
        String[] names = new String[maxnames];
        if (maxnames > 0) {
            libvirt.virConnectListDefinedDomains(VCP, names, maxnames);
            processError();
        }
        return names;
    }

    /**
     * Provides the list of names of defined interfaces on this host
     *
     * @return an Array of Strings that contains the names of the interfaces on
     *         this host
     * @throws LibvirtException
     */
    public String[] listDefinedInterfaces() throws LibvirtException {
        int num = numOfDefinedInterfaces();
        String[] returnValue = new String[num];
        if (num > 0) {
            libvirt.virConnectListDefinedInterfaces(VCP, returnValue, num);
            processError();
        }
        return returnValue;
    }

    /**
     * Lists the inactive networks
     *
     * @return an Array of Strings that contains the names of the inactive
     *         networks
     * @throws LibvirtException
     */
    public String[] listDefinedNetworks() throws LibvirtException {
        int maxnames = numOfDefinedNetworks();
        String[] names = new String[maxnames];

        if (maxnames > 0) {
            libvirt.virConnectListDefinedNetworks(VCP, names, maxnames);
            processError();
        }
        return names;
    }

    /**
     * Provides the list of names of inactive storage pools.
     *
     * @return an Array of Strings that contains the names of the defined
     *         storage pools
     * @throws LibvirtException
     */
    public String[] listDefinedStoragePools() throws LibvirtException {
        int num = numOfDefinedStoragePools();
        String[] returnValue = new String[num];
        libvirt.virConnectListDefinedStoragePools(VCP, returnValue, num);
        processError();
        return returnValue;
    }

    /**
     * List the names of the devices on this node
     *
     * @param capabilityName
     *            optional capability name
     */
    public String[] listDevices(String capabilityName) throws LibvirtException {
        int maxDevices = numOfDevices(capabilityName);
        String[] names = new String[maxDevices];

        if (maxDevices > 0) {
            libvirt.virNodeListDevices(VCP, capabilityName, names, maxDevices, 0);
            processError();
        }
        return names;
    }

    /**
     * Lists the active domains.
     *
     * @return and array of the IDs of the active domains
     * @throws LibvirtException
     */
    public int[] listDomains() throws LibvirtException {
        int maxids = numOfDomains();
        int[] ids = new int[maxids];

        if (maxids > 0) {
            libvirt.virConnectListDomains(VCP, ids, maxids);
            processError();
        }
        return ids;
    }

    /**
     * Provides the list of names of interfaces on this host
     *
     * @return an Array of Strings that contains the names of the interfaces on
     *         this host
     * @throws LibvirtException
     */
    public String[] listInterfaces() throws LibvirtException {
        int num = numOfInterfaces();
        String[] returnValue = new String[num];
        if (num > 0) {
            libvirt.virConnectListInterfaces(VCP, returnValue, num);
            processError();
        }
        return returnValue;
    }

    /**
     * Lists the names of the network filters
     *
     * @return an Array of Strings that contains the names network filters
     * @throws LibvirtException
     */
    public String[] listNetworkFilters() throws LibvirtException {
        int maxnames = numOfNetworkFilters();
        String[] names = new String[maxnames];
        if (maxnames > 0) {
            libvirt.virConnectListNWFilters(VCP, names, maxnames);
            processError();
        }
        return names;
    }

    /**
     * Lists the active networks.
     *
     * @return an Array of Strings that contains the names of the active
     *         networks
     * @throws LibvirtException
     */
    public String[] listNetworks() throws LibvirtException {
        int maxnames = numOfNetworks();
        String[] names = new String[maxnames];

        if (maxnames > 0) {
            libvirt.virConnectListNetworks(VCP, names, maxnames);
            processError();
        }
        return names;
    }

    /**
     * Retrieve the List UUIDs of defined secrets
     *
     * @return an Array of Strings that contains the uuids of the defined
     *         secrets
     */
    public String[] listSecrets() throws LibvirtException {
        int num = numOfSecrets();
        String[] returnValue = new String[num];
        libvirt.virConnectListSecrets(VCP, returnValue, num);
        processError();
        return returnValue;
    }

    /**
     * Provides the list of names of active storage pools.
     *
     * @return an Array of Strings that contains the names of the defined
     *         storage pools
     * @throws LibvirtException
     */
    public String[] listStoragePools() throws LibvirtException {
        int num = numOfStoragePools();
        String[] returnValue = new String[num];
        libvirt.virConnectListStoragePools(VCP, returnValue, num);
        processError();
        return returnValue;
    }

    /**
     * Creates and starts a new virtual network. The properties of the network
     * are based on an XML description similar to the one returned by
     * virNetworkGetXMLDesc()
     *
     * @param xmlDesc
     *            the Network Description
     * @return the Network object representing the created network
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Net1" >The XML format
     *      description</a>
     */
    public Network networkCreateXML(String xmlDesc) throws LibvirtException {
        Network returnValue = null;
        NetworkPointer ptr = libvirt.virNetworkCreateXML(VCP, xmlDesc);
        processError();
        if (ptr != null) {
            returnValue = new Network(this, ptr);
        }
        return returnValue;
    }

    /**
     * Defines a network, but does not create it. The properties of the network
     * are based on an XML description similar to the one returned by
     * virNetworkGetXMLDesc()
     *
     * @param xmlDesc
     * @return the resulting Network object
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Net1" >The XML format
     *      description</a>
     */
    public Network networkDefineXML(String xmlDesc) throws LibvirtException {
        Network returnValue = null;
        NetworkPointer ptr = libvirt.virNetworkDefineXML(VCP, xmlDesc);
        processError();
        if (ptr != null) {
            returnValue = new Network(this, ptr);
        }
        return returnValue;
    }

    /**
     * Defines a networkFilter
     *
     * @param xmlDesc
     *            the descirption of the filter
     * @return the new filer
     * @throws LibvirtException
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virNWFilterDefineXML"
     *      > Libvirt Documentation </a>
     */
    public NetworkFilter networkFilterDefineXML(String xmlDesc) throws LibvirtException {
        NetworkFilter returnValue = null;
        NetworkFilterPointer ptr = libvirt.virNWFilterDefineXML(VCP, xmlDesc);
        processError();
        if (ptr != null) {
            returnValue = new NetworkFilter(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a network filter based on its unique name
     *
     * @param name
     *            name of network filter to fetch
     * @return network filter object
     * @throws LibvirtException
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virNWFilterLookupByName"
     *      > Libvirt Documentation </a>
     */
    public NetworkFilter networkFilterLookupByName(String name) throws LibvirtException {
        NetworkFilter returnValue = null;
        NetworkFilterPointer ptr = libvirt.virNWFilterLookupByName(VCP, name);
        processError();
        if (ptr != null) {
            returnValue = new NetworkFilter(this, ptr);
        }
        return returnValue;
    }

    /**
     * Looks up a network filter based on its UUID in array form. The UUID Array
     * contains an unpacked representation of the UUID, each int contains only
     * one byte.
     *
     * @param UUID
     *            the UUID as an unpacked int array
     * @return the network filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        NetworkFilter returnValue = null;
        NetworkFilterPointer ptr = libvirt.virNWFilterLookupByUUID(VCP, uuidBytes);
        processError();
        if (ptr != null) {
            returnValue = new NetworkFilter(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a network filter based on its globally unique id
     *
     * @param uuid
     *            a java UUID
     * @return a new network filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByUUID(UUID uuid) throws LibvirtException {
        return networkFilterLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a network filter based on its UUID in String form.
     *
     * @param UUID
     *            the UUID in canonical String representation
     * @return the Network Filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByUUIDString(String UUID) throws LibvirtException {
        NetworkFilter returnValue = null;
        NetworkFilterPointer ptr = libvirt.virNWFilterLookupByUUIDString(VCP, UUID);
        processError();
        if (ptr != null) {
            returnValue = new NetworkFilter(this, ptr);
        }
        return returnValue;
    }

    /**
     * Looks up a network on the based on its name.
     *
     * @param name
     *            name of the network
     * @return The Network object found
     * @throws LibvirtException
     */
    public Network networkLookupByName(String name) throws LibvirtException {
        Network returnValue = null;
        NetworkPointer ptr = libvirt.virNetworkLookupByName(VCP, name);
        processError();
        if (ptr != null) {
            returnValue = new Network(this, ptr);
        }
        return returnValue;
    }

    /**
     * Looks up a network based on its UUID represented as an int array. The
     * UUID Array contains an unpacked representation of the UUID, each int
     * contains only one byte.
     *
     * @param UUID
     *            the UUID as an unpacked int array
     * @return The Network object found
     * @throws LibvirtException
     * @deprecated use the UUIDString or UUID API.
     */
    @Deprecated
    public Network networkLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        Network returnValue = null;
        NetworkPointer ptr = libvirt.virNetworkLookupByUUID(VCP, uuidBytes);
        processError();
        if (ptr != null) {
            returnValue = new Network(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a network based on its globally unique id
     *
     * @param uuid
     *            a java UUID
     * @return a new network object
     * @throws LibvirtException
     */
    public Network networkLookupByUUID(UUID uuid) throws LibvirtException {
        return networkLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a network based on its UUID represented as a String.
     *
     * @param UUID
     *            the UUID in canonical String representation
     * @return The Network object found
     * @throws LibvirtException
     */
    public Network networkLookupByUUIDString(String UUID) throws LibvirtException {
        Network returnValue = null;
        NetworkPointer ptr = libvirt.virNetworkLookupByUUIDString(VCP, UUID);
        processError();
        if (ptr != null) {
            returnValue = new Network(this, ptr);
        }
        return returnValue;
    }

    /**
     * Returns a NodeInfo object describing the hardware configuration of the
     * node.
     *
     * @return a NodeInfo object
     * @throws LibvirtException
     */
    public NodeInfo nodeInfo() throws LibvirtException {
        virNodeInfo vInfo = new virNodeInfo();
        libvirt.virNodeGetInfo(VCP, vInfo);
        processError();
        return new NodeInfo(vInfo);
    }

    /**
     * Provides the number of inactive domains.
     *
     * @return the number of inactive domains
     * @throws LibvirtException
     */
    public int numOfDefinedDomains() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfDefinedDomains(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of defined interfaces.
     *
     * @return the number of interfaces
     * @throws LibvirtException
     */
    public int numOfDefinedInterfaces() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfDefinedInterfaces(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of inactive networks.
     *
     * @return the number of inactive networks
     * @throws LibvirtException
     */
    public int numOfDefinedNetworks() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfDefinedNetworks(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of inactive storage pools
     *
     * @return the number of pools found
     * @throws LibvirtException
     */
    public int numOfDefinedStoragePools() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfDefinedStoragePools(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of node devices.
     *
     * @return the number of inactive domains
     * @throws LibvirtException
     */
    public int numOfDevices(String capabilityName) throws LibvirtException {
        int returnValue = libvirt.virNodeNumOfDevices(VCP, capabilityName, 0);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of active domains.
     *
     * @return the number of active domains
     * @throws LibvirtException
     */
    public int numOfDomains() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfDomains(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of interfaces.
     *
     * @return the number of interfaces
     * @throws LibvirtException
     */
    public int numOfInterfaces() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfInterfaces(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of network filters
     *
     * @return the number of network filters
     * @throws LibvirtException
     */
    public int numOfNetworkFilters() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfNWFilters(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of active networks.
     *
     * @return the number of active networks
     * @throws LibvirtException
     */
    public int numOfNetworks() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfNetworks(VCP);
        processError();
        return returnValue;
    }

    /**
     * Fetch number of currently defined secrets.
     *
     * @return the number of secrets
     */
    public int numOfSecrets() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfSecrets(VCP);
        processError();
        return returnValue;
    }

    /**
     * Provides the number of active storage pools
     *
     * @return the number of pools found
     * @throws LibvirtException
     */
    public int numOfStoragePools() throws LibvirtException {
        int returnValue = libvirt.virConnectNumOfStoragePools(VCP);
        processError();
        return returnValue;
    }

    /**
     * call the error handling logic. Should be called after every libvirt call
     *
     * @throws LibvirtException
     */
    protected void processError() throws LibvirtException {
        ErrorHandler.processError(libvirt);
    }

    /**
     * Restores a domain saved to disk by Domain.save().
     *
     * @param from
     *            the path of the saved file on the remote host
     * @throws LibvirtException
     */
    public void restore(String from) throws LibvirtException {
        libvirt.virDomainRestore(VCP, from);
        processError();
    }

    /**
     * If XML specifies a UUID, locates the specified secret and replaces all
     * attributes of the secret specified by UUID by attributes specified in xml
     * (any attributes not specified in xml are discarded). Otherwise, creates a
     * new secret with an automatically chosen UUID, and initializes its
     * attributes from xml.
     *
     * @param xmlDesc
     *            the secret to create
     * @return the Secret object
     * @throws LibvirtException
     */
    public Secret secretDefineXML(String xmlDesc) throws LibvirtException {
        Secret returnValue = null;
        SecretPointer ptr = libvirt.virSecretDefineXML(VCP, xmlDesc, 0);
        processError();
        if (ptr != null) {
            returnValue = new Secret(this, ptr);
        }
        return returnValue;
    }

    /**
     * Looks up a secret based on its UUID in array form. The UUID Array
     * contains an unpacked representation of the UUID, each int contains only
     * one byte.
     *
     * @param UUID
     *            the UUID as an unpacked int array
     * @return the Secret object
     * @throws LibvirtException
     */
    public Secret secretLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        Secret returnValue = null;
        SecretPointer ptr = libvirt.virSecretLookupByUUID(VCP, uuidBytes);
        processError();
        if (ptr != null) {
            returnValue = new Secret(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a secret based on its globally unique id
     *
     * @param uuid
     *            a java UUID
     * @return a new domain object
     * @throws LibvirtException
     */
    public Secret secretLookupByUUID(UUID uuid) throws LibvirtException {
        return secretLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a secret based on its UUID in String form.
     *
     * @param UUID
     *            the UUID in canonical String representation
     * @return the Domain object
     * @throws LibvirtException
     */
    public Secret secretLookupByUUIDString(String UUID) throws LibvirtException {
        Secret returnValue = null;
        SecretPointer ptr = libvirt.virSecretLookupByUUIDString(VCP, UUID);
        processError();
        if (ptr != null) {
            returnValue = new Secret(this, ptr);
        }
        return returnValue;
    }

    public void setConnectionErrorCallback(Libvirt.VirErrorCallback callback) throws LibvirtException {
        libvirt.virConnSetErrorFunc(VCP, null, callback);
        processError();
    }

    /**
     * change the amount of memory reserved to Domain0. Domain0 is the domain
     * where the application runs. This function may requires priviledged access
     * to the hypervisor.
     *
     * @param memory
     *            in kilobytes
     * @throws LibvirtException
     */
    public void setDom0Memory(long memory) throws LibvirtException {
        libvirt.virDomainSetMemory(null, new NativeLong(memory));
        processError();
    }

    /**
     * Create a new storage based on its XML description. The pool is not
     * persistent, so its definition will disappear when it is destroyed, or if
     * the host is restarted
     *
     * @param xmlDesc
     *            XML description for new pool
     * @param flags
     *            future flags, use 0 for now
     * @return StoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolCreateXML(String xmlDesc, int flags) throws LibvirtException {
        StoragePoolPointer ptr = libvirt.virStoragePoolCreateXML(VCP, xmlDesc, flags);
        processError();
        return new StoragePool(this, ptr);
    }

    /**
     * Define a new inactive storage pool based on its XML description. The pool
     * is persistent, until explicitly undefined.
     *
     * @param xml
     *            XML description for new pool
     * @param flags
     *            flags future flags, use 0 for now
     * @return StoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolDefineXML(String xml, int flags) throws LibvirtException {
        StoragePoolPointer ptr = libvirt.virStoragePoolDefineXML(VCP, xml, flags);
        processError();
        return new StoragePool(this, ptr);
    }

    /**
     * Fetch a storage pool based on its unique name
     *
     * @param name
     *            name of pool to fetch
     * @return StoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByName(String name) throws LibvirtException {
        StoragePoolPointer ptr = libvirt.virStoragePoolLookupByName(VCP, name);
        processError();
        return new StoragePool(this, ptr);
    }

    /**
     * Fetch a storage pool based on its globally unique id
     *
     * @param UUID
     *            globally unique id of pool to fetch
     * @return a new network object
     * @throws LibvirtException
     * @deprecated Use the UUIDString or UUID APIs.
     */
    @Deprecated
    public StoragePool storagePoolLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        StoragePool returnValue = null;
        StoragePoolPointer ptr = libvirt.virStoragePoolLookupByUUID(VCP, uuidBytes);
        processError();
        if (ptr != null) {
            returnValue = new StoragePool(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a storage pool based on its globally unique id
     *
     * @param uuid
     *            a java UUID
     * @return a new network object
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByUUID(UUID uuid) throws LibvirtException {
        return storagePoolLookupByUUIDString(uuid.toString());
    }

    /**
     * Fetch a storage pool based on its globally unique id
     *
     * @param UUID
     *            globally unique id of pool to fetch
     * @return VirStoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByUUIDString(String UUID) throws LibvirtException {
        StoragePool returnValue = null;
        StoragePoolPointer ptr = libvirt.virStoragePoolLookupByUUIDString(VCP, UUID);
        processError();
        if (ptr != null) {
            returnValue = new StoragePool(this, ptr);
        }
        return returnValue;
    }

    /**
     * Fetch a a storage volume based on its globally unique key
     *
     * @param key
     *            globally unique key
     * @return a storage volume
     */
    public StorageVol storageVolLookupByKey(String key) throws LibvirtException {
        StorageVolPointer sPtr = libvirt.virStorageVolLookupByKey(VCP, key);
        processError();
        return new StorageVol(this, sPtr);
    }

    /**
     * Fetch a storage volume based on its locally (host) unique path
     *
     * @param path
     *            locally unique path
     * @return a storage volume
     */
    public StorageVol storageVolLookupByPath(String path) throws LibvirtException {
        StorageVolPointer sPtr = libvirt.virStorageVolLookupByPath(VCP, path);
        processError();
        return new StorageVol(this, sPtr);
    }

    /**
     * Creates a new stream object which can be used to perform streamed I/O
     * with other public API function.
     *
     * @param flags
     *            use Stream.VIR_STREAM_NONBLOCK if non-blocking is required
     * @return the new object
     */
    public Stream streamNew(int flags) throws LibvirtException {
        StreamPointer sPtr = libvirt.virStreamNew(VCP, flags);
        processError();
        return new Stream(this, sPtr);
    }

    /**
     * Verify the connect is active.
     *
     * @return boolean   The true connected, or false not.
     * @throws LibvirtException
     */
    public boolean isConnected() throws LibvirtException {
        return ( ( VCP != null ) ? true : false );
    }
}
