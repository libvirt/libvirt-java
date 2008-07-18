package org.libvirt;

public class Domain {

	static final class CreateFlags{
		static final int VIR_DOMAIN_NONE = 0;
	}

	static final class MigrateFlags{
		/**
		 * live migration
		 */
		static final int VIR_MIGRATE_LIVE = 1;
	}

	static final class XMLFlags{
		/**
		 * dump security sensitive information too
		 */
		static final int VIR_DOMAIN_XML_SECURE = 1;
		/**
		 * dump inactive domain information
		 */
		static final int VIR_DOMAIN_XML_INACTIVE = 2;
	}

	/**
	 * the native virDomainPtr.
	 */
	private long  VDP;

	/**
	 * The Connect Object that represents the Hypervisor of this Domain
	 */
	private Connect virConnect;


	/**
	 * Constructs a Domain object from a known native virDomainPtr, and a Connect object.
	 * For use when native libvirt returns a virConnectPtr, i.e. error handling.
	 *
	 * @param virConnect the Domain's hypervisor
	 * @param VDP the native virDomainPtr
	 */
	Domain(Connect virConnect, long VDP){
		this.virConnect = virConnect;
		this.VDP = VDP;
	}

	/**
	 * Provides an XML description of the domain.
	 * The description may be reused later to relaunch the domain with createLinux().
	 *
	 * @param flags not used
	 * @return the XML description String
	 * @throws LibvirtException
	 * @see <a href="http://libvirt.org/format.html#Normal1" >The XML Description format </a>
	 */
	public String getXMLDesc(int flags) throws LibvirtException{
		return _getXMLDesc(VDP, flags);
	}

	private native String _getXMLDesc(long VDP, int flags) throws LibvirtException;

	/**
	 * Provides a boolean value indicating whether the network is configured to be automatically started when the host machine boots.
	 *
	 * @return the result
	 * @throws LibvirtException
	 */
	public boolean getAutostart() throws LibvirtException{
		return _getAutostart(VDP);
	}


	private native boolean _getAutostart(long VDP) throws LibvirtException;

	/**
	 * Configures the network to be automatically started when the host machine boots.
	 *
	 * @param autostart
	 * @throws LibvirtException
	 */
	public void setAutostart(boolean autostart) throws LibvirtException{
			_setAutostart(VDP, autostart);
	}

	private native int _setAutostart(long VDP, boolean autostart) throws LibvirtException;

	/**
	 * Provides the connection object associated with a domain.
	 *
	 * @return the Connect object
	 */
	public Connect getConnect(){
		return virConnect;
	}

	/**
	 * Gets the hypervisor ID number for the domain
	 *
	 * @return the hypervisor ID
	 * @throws LibvirtException
	 */
	public int getID() throws LibvirtException{
		return _getID(VDP);
	}

	private native int _getID(long VDP) throws LibvirtException;


	/**
	 * Extract information about a domain.
	 * Note that if the connection used to get the domain is limited only a partial set of the information can be extracted.
	 *
	 * @return a DomainInfo object describing this domain
	 * @throws LibvirtException
	 */
	public DomainInfo getInfo() throws LibvirtException{
		return _getInfo(VDP);
	}

	private native DomainInfo _getInfo(long VDP) throws LibvirtException;

	/**
	 * Retrieve the maximum amount of physical memory allocated to a domain.
	 *
	 * @return the memory in kilobytes
	 * @throws LibvirtException
	 */
	public long getMaxMemory() throws LibvirtException{
		return _getMaxMemory(VDP);
	}

	private native long _getMaxMemory(long VDP) throws LibvirtException;

	/**
	 * * Dynamically change the maximum amount of physical memory allocated to a domain.
	 * This function requires priviledged access to the hypervisor.
	 *
	 * @param memory the amount memory in kilobytes
	 * @throws LibvirtException
	 */
	public void setMaxMemory(long memory) throws LibvirtException{
		 _setMaxMemory(VDP, memory);
	}

	private native long _setMaxMemory(long VDP,  long memory) throws LibvirtException;


	/**
	 * Provides the maximum number of virtual CPUs supported for the guest VM.
	 * If the guest is inactive, this is basically the same as virConnectGetMaxVcpus.
	 * If the guest is running this will reflect the maximum number of virtual CPUs the guest was booted with.
	 *
	 * @return the number of VCPUs
	 * @throws LibvirtException
	 */
	public int getMaxVcpus() throws LibvirtException{
		return _getMaxVcpus(VDP);
	}

	private native int _getMaxVcpus(long VDP) throws LibvirtException;


	/**
	 * Gets the public name for this domain
	 *
	 * @return the name
	 * @throws LibvirtException
	 */
	public String getName() throws LibvirtException{
		return _getName(VDP);
	}

	private native String _getName(long VDP) throws LibvirtException;

	/**
	 * Gets the type of domain operation system.
	 *
	 * @return the type
	 * @throws LibvirtException
	 */
	public String getOSType() throws LibvirtException{
		return _getOSType(VDP);
	}

	private native String _getOSType(long VDP) throws LibvirtException;


	/**
	 * Gets the scheduler parameters.
	 *
	 * @return an array of SchedParameter objects
	 * @throws LibvirtException
	 */
	public SchedParameter[] getSchedulerParameters() throws LibvirtException{
		return _getSchedulerParameters(VDP);
	}

	private native SchedParameter[] _getSchedulerParameters (long VDP) throws LibvirtException;

	/**
	 * Changes the scheduler parameters
	 *
	 * @param params an array of SchedParameter objects to be changed
	 * @throws LibvirtException
	 */
	public void setSchedulerParameters(SchedParameter[] params) throws LibvirtException{
		_setSchedulerParameters(VDP, params);
	}

	private native int _setSchedulerParameters(long VDP, SchedParameter[] params) throws LibvirtException;

	//getSchedulerType
	//We don't expose the nparams return value, it's only needed for the SchedulerParameters allocations,
	//but we handle that in getSchedulerParameters internally.
	/**
	 * Gets the scheduler type.
	 *
	 * @return the type of the scheduler
	 * @throws LibvirtException
	 */
	public String[] getSchedulerType() throws LibvirtException{
		return _getSchedulerType(VDP);
	}

	private native String[] _getSchedulerType(long VDP) throws LibvirtException;

	/**
	 * Get the UUID for this domain.
	 *
	 * @return the UUID as an unpacked int array
	 * @throws LibvirtException
	 * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
	 */
	public int[] getUUID() throws LibvirtException{
		return _getUUID(VDP);
	}

	private native int[] _getUUID(long VDP) throws LibvirtException;

	/**
	 * Gets the UUID for this domain as string.
	 *
	 * @return the UUID in canonical String format
	 * @throws LibvirtException
	 * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
	 */
	public String getUUIDString() throws LibvirtException{
		return _getUUIDString(VDP);
	}

	private native String _getUUIDString(long VDP) throws LibvirtException;

	/**
	 * Extracts information about virtual CPUs of this domain
	 *
	 * @return an array of VcpuInfo object describing the VCPUs
	 * @throws LibvirtException
	 */
	public VcpuInfo[] getVcpusInfo() throws LibvirtException{
		return _getVcpusInfo(VDP);
	}

	private native VcpuInfo[] _getVcpusInfo(long VDP) throws LibvirtException;

	/**
	 * Returns the cpumaps for this domain
	 * Only the lower 8 bits of each int in the array contain information.
	 *
	 * @return a bitmap of real CPUs for all vcpus of this domain
	 * @throws LibvirtException
	 */
	public int[] getVcpusCpuMaps() throws LibvirtException{
		return _getVcpusCpuMaps(VDP);
	}

	private native int[] _getVcpusCpuMaps(long VDP) throws LibvirtException;


	/**
	 * Dynamically changes the real CPUs which can be allocated to a virtual CPU.
	 * This function requires priviledged access to the hypervisor.
	 *
	 * @param vcpu virtual cpu number
	 * @param cpumap bit map of real CPUs represented by the the lower 8 bits of each int in the array. Each bit set to 1 means that corresponding CPU is usable. Bytes are stored in little-endian order: CPU0-7, 8-15... In each byte, lowest CPU number is least significant bit.
	 * @throws LibvirtException
	 */
	public void pinVcpu(int vcpu, int[] cpumap) throws LibvirtException{
		_pinVcpu(VDP, vcpu, cpumap);
	}

	private native int _pinVcpu(long VDP, int vcpu, int[]cpumap) throws LibvirtException;

	/**
	 * Dynamically changes the number of virtual CPUs used by this domain.
	 * Note that this call may fail if the underlying virtualization hypervisor does not support it or if growing the number is arbitrary limited.
	 * This function requires priviledged access to the hypervisor.
	 *
	 * @param nvcpus the new number of virtual CPUs for this domain
	 * @throws LibvirtException
	 */
	public void setVcpus(int nvcpus) throws LibvirtException{
		_setVcpus(VDP, nvcpus);
	}

	private native int _setVcpus(long VDP, int nvcpus) throws LibvirtException;

	/**
	 * Creates a virtual device attachment to backend.
	 *
	 * @param xmlDesc XML description of one device
	 * @throws LibvirtException
	 */
	public void attachDevice(String xmlDesc) throws LibvirtException{
		_attachDevice(VDP, xmlDesc);
	}

	private native int _attachDevice(long VDP, String xmlDesc) throws LibvirtException;

	/**
	 * Destroys a virtual device attachment to backend.
	 *
	 * @param xmlDesc XML description of one device
	 * @throws LibvirtException
	 */
	public void detachDevice(String xmlDesc) throws LibvirtException{
		_detachDevice(VDP, xmlDesc);
	}

	private native int _detachDevice(long VDP, String xmlDesc) throws LibvirtException;


	/**
	 * Returns block device (disk) stats for block devices attached to this domain.
	 * The path parameter is the name of the block device.
	 * Get this by calling virDomainGetXMLDesc and finding the <target dev='...'> attribute within //domain/devices/disk.
	 * (For example, "xvda"). Domains may have more than one block device.
	 * To get stats for each you should make multiple calls to this function.
	 * Individual fields within the DomainBlockStats object may be returned as -1, which indicates that the hypervisor does not support that particular statistic.
	 *
	 * @param path path to the block device
	 * @return the statistics in a DomainBlockStats object
	 * @throws LibvirtException
	 */
	public DomainBlockStats blockStats(String path) throws LibvirtException{
		return _blockStats(VDP, path);
	}

	private native DomainBlockStats _blockStats(long VDP, String path) throws LibvirtException;

	/**
	 * Returns network interface stats for interfaces attached to this domain.
	 * The path parameter is the name of the network interface.
	 * Domains may have more than network interface.
	 * To get stats for each you should make multiple calls to this function.
	 * Individual fields within the DomainInterfaceStats object may be returned as -1, which indicates that the hypervisor does not support that particular statistic.
	 *
	 * @param path path to the interface
	 * @return the statistics in a DomainInterfaceStats object
	 * @throws LibvirtException
	 */
	public DomainInterfaceStats interfaceStats(String path) throws LibvirtException{
		return _interfaceStats(VDP, path);
	}

	private native DomainInterfaceStats _interfaceStats(long VDP, String path) throws LibvirtException;

	/**
	 * Dumps the core of this domain on a given file for analysis.
	 * Note that for remote Xen Daemon the file path will be interpreted in the remote host.
	 *
	 * @param to path for the core file
	 * @param flags extra flags, currently unused
	 * @throws LibvirtException
	 */
	public void coreDump(String to, int flags) throws LibvirtException{
		_coreDump(VDP, to, flags);
	}

	private native int _coreDump(long VDP, String to, int flags) throws LibvirtException;

	/**
	 * Launches this defined domain.
	 * If the call succeed the domain moves from the defined to the running domains pools.
	 *
	 * @throws LibvirtException
	 */
	public void create() throws LibvirtException{
		_create(VDP);
	}

	private native int _create(long VDP) throws LibvirtException;

	/**
	 * Destroys this domain object.
	 * The running instance is shutdown if not down already and all resources used by it are given back to the hypervisor.
	 * The data structure is freed and should not be used thereafter if the call does not return an error.
	 * This function may requires priviledged access
	 *
	 * @throws LibvirtException
	 */
	public void destroy() throws LibvirtException{
		_destroy(VDP);
	}

	private native int _destroy(long VDP) throws LibvirtException;

	/**
	 * Frees this domain object.
	 * The running instance is kept alive.
	 * The data structure is freed and should not be used thereafter.
	 *
	 * @throws LibvirtException
	 */
	public void free() throws LibvirtException{
		_free(VDP);
		VDP=0;
	}

	private native int _free(long VDP) throws LibvirtException;


	/**
	 * Migrate this domain object from its current host to the destination host given by dconn (a connection to the destination host).
	 * Flags may be one of more of the following: Domain.VIR_MIGRATE_LIVE Attempt a live migration.
	 * If a hypervisor supports renaming domains during migration, then you may set the dname parameter to the new name (otherwise it keeps the same name).
	 * If this is not supported by the hypervisor, dname must be NULL or else you will get an error.
	 * Since typically the two hypervisors connect directly to each other in order to perform the migration, you may need to specify a path from the source to the destination.
	 * This is the purpose of the uri parameter.
	 * If uri is NULL, then libvirt will try to find the best method.
	 * Uri may specify the hostname or IP address of the destination host as seen from the source.
	 * Or uri may be a URI giving transport, hostname, user, port, etc. in the usual form.
	 * Refer to driver documentation for the particular URIs supported.
	 * The maximum bandwidth (in Mbps) that will be used to do migration can be specified with the bandwidth parameter.
	 * If set to 0, libvirt will choose a suitable default.
	 * Some hypervisors do not support this feature and will return an error if bandwidth is not 0.
	 * To see which features are supported by the current hypervisor, see Connect.getCapabilities,
	 * /capabilities/host/migration_features.
	 * There are many limitations on migration imposed by the underlying technology - for example it may not be possible to migrate between different processors even with the same architecture, or between different types of hypervisor.
	 *
	 * @param dconn destination host (a Connect object)
	 * @param flags flags
	 * @param dname (optional) rename domain to this at destination
	 * @param uri (optional) dest hostname/URI as seen from the source host
	 * @param bandwidth optional) specify migration bandwidth limit in Mbps
	 * @return the new domain object if the migration was successful, or NULL in case of error. Note that the new domain object exists in the scope of the destination connection (dconn).
	 * @throws LibvirtException
	 */
	public Domain migrate(Connect dconn, long flags, String dname, String uri, long bandwidth) throws LibvirtException{
		return new Domain(dconn, _migrate(VDP, dconn, flags, dname, uri, bandwidth));
	}

	private native long _migrate(long VDP, Connect dconn, long flags, String dname, String uri, long bandwidth) throws LibvirtException;

	/**
	 * Reboot this domain, the domain object is still usable there after but the domain OS is being stopped for a restart.
	 * Note that the guest OS may ignore the request.
	 *
	 * @param flags 	extra flags for the reboot operation, not used yet
	 * @throws LibvirtException
	 */
	public void reboot(int flags) throws LibvirtException{
		_reboot(VDP, flags);
	}

	private native int _reboot(long VDP, int flags) throws LibvirtException;

	/**
	 * Suspends this active domain, the process is frozen without further access to CPU resources and I/O but the memory used by the domain at the hypervisor level will stay allocated.
	 * Use Domain.resume() to reactivate the domain. This function requires priviledged access.
	 *
	 * @throws LibvirtException
	 */
	public void suspend() throws LibvirtException{
		_suspend(VDP);
	}

	private native int _suspend(long VDP) throws LibvirtException;

	/**
	 * Resume this suspended domain, the process is restarted from the state where it was frozen by calling virSuspendDomain().
	 * This function may requires privileged access
	 *
	 * @throws LibvirtException
	 */
	public void resume() throws LibvirtException{
		_resume(VDP);
	}

	private native int _resume(long VDP) throws LibvirtException;

	/**
	 * Suspends this domain and saves its memory contents to a file on disk.
	 * After the call, if successful, the domain is not listed as running anymore (this may be a problem).
	 * Use Connect.virDomainRestore() to restore a domain after saving.
	 *
	 * @param to path for the output file
	 * @throws LibvirtException
	 */
	public void save(String to) throws LibvirtException{
		_save(VDP, to);
	}

	private native int _save(long VDP, String to) throws LibvirtException;

	/**
	 * Shuts down this domain, the domain object is still usable there after but the domain OS is being stopped.
	 * Note that the guest OS may ignore the request.
	 * TODO: should we add an option for reboot, knowing it may not be doable in the general case ?
	 *
	 * @throws LibvirtException
	 */
	public void shutdown() throws LibvirtException{
		_shutdown(VDP);
	}

	private native int _shutdown(long VDP) throws LibvirtException;

	/**
	 * undefines this domain but does not stop it if it is running
	 *
	 * @throws LibvirtException
	 */
	public void undefine() throws LibvirtException{
		_undefine(VDP);
	}

	private native int _undefine(long VDP) throws LibvirtException;

	/**
	 * Dynamically changes the target amount of physical memory allocated to this domain.
	 * This function may requires priviledged access to the hypervisor.
	 *
	 * @param memory in kilobytes
	 * @throws LibvirtException
	 */
	public void setMemory(long memory) throws LibvirtException{
		_setMemory(VDP, memory);
	}

	private native int _setMemory(long VDP, long memory) throws LibvirtException;


}
