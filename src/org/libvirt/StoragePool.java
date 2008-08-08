package org.libvirt;

public class StoragePool {

	static final class BuildFlags{
		/**
		 * Regular build from scratch
		 */
		static final int VIR_STORAGE_POOL_BUILD_NEW	 =	0;
		/**
		 * Repair / reinitialize
		 */
		static final int VIR_STORAGE_POOL_BUILD_REPAIR	=	1;
		/**
		 * Extend existing pool
		 */
		static final int VIR_STORAGE_POOL_BUILD_RESIZE	=	2;
	}

	static final class DeleteFlags{
		/**
		 * Delete metadata only (fast)
		 */
		static final int VIR_STORAGE_POOL_DELETE_NORMAL	 =	0;
		/**
		 * Clear all data to zeros (slow)
		 */
		static final int VIR_STORAGE_POOL_DELETE_ZEROED	=	1;
	}

	/**
	 * the native virStoragePoolPtr.
	 */
	private long  VSPP;

	/**
	 * The VirConnect Object that represents the Hypervisor of this Domain
	 */
	private Connect virConnect;


	/**
	 * Constructs a VirStoragePool object from a known native virStoragePoolPtr, and a VirConnect object.
	 * For use when native libvirt returns a virStoragePoolPtr, i.e. error handling.
	 *
	 * @param virConnect the Domain's hypervisor
	 * @param VSPP the native virStoragePoolPtr
	 */
	StoragePool(Connect virConnect, long VSPP){
		this.virConnect = virConnect;
		this.VSPP = VSPP;
	}

	/**
	 * Build the underlying storage pool
	 *
	 * @param flags future flags, use 0 for now
	 */
	public void build(int flags) throws LibvirtException{
		_build(VSPP, flags);
	}

	private native int _build(long VSPP, int flags) throws LibvirtException;

	/**
	 * Starts this inactive storage pool
	 *
	 * @param flags future flags, use 0 for now
	 */
	public void create(int flags) throws LibvirtException{
		_create(VSPP, flags);
	}

	private native int _create(long VSPP, int flags) throws LibvirtException;

	/**
	 * Delete the underlying pool resources. This is a non-recoverable operation.
	 * The virStoragePool object itself is not free'd.
	 *
	 * @param flags flags for obliteration process
	 */
	public void delete(int flags) throws LibvirtException{
		_delete(VSPP, flags);
	}

	private native int _delete(long VSPP, int flags) throws LibvirtException;

	/**
	 * Destroy an active storage pool.
	 * This will deactivate the pool on the host, but keep any persistent config associated with it.
	 * If it has a persistent config it can later be restarted with virStoragePoolCreate().
	 * This does not free the associated virStoragePoolPtr object.
	 */
	public void destroy() throws LibvirtException{
		_destroy(VSPP);
	}

	private native int _destroy(long VSPP) throws LibvirtException;

	/**
	 * Free a storage pool object, releasing all memory associated with it.
	 * Does not change the state of the pool on the host.
	 */
	public void free() throws LibvirtException{
		_free(VSPP);
	}

	private native int _free(long VSPP) throws LibvirtException;


	/**
	 * Fetches the value of the autostart flag, which determines whether the pool is automatically started at boot time
	 *
	 * @return the result
	 * @throws LibvirtException
	 */
	public boolean getAutostart() throws LibvirtException{
		return _getAutostart(VSPP);
	}

	private native boolean _getAutostart(long VSPP) throws LibvirtException;

	/**
	 * Provides the connection pointer associated with a storage pool.
	 *
	 * @return the Connect object
	 */
	public Connect getConnect(){
		return virConnect;
	}
	/**
	 * Get volatile information about the storage pool such as free space / usage summary
	 *
	 * @return a StoragePoolInfo object describing this storage pool
	 * @throws LibvirtException
	 */
	public StoragePoolInfo getInfo() throws LibvirtException{
		return _getInfo(VSPP);
	}

	private native StoragePoolInfo _getInfo(long VSPP) throws LibvirtException;

	/**
	 * Fetch the locally unique name of the storage pool
	 *
	 * @return the name
	 * @throws LibvirtException
	 */
	public String getName() throws LibvirtException{
		return _getName(VSPP);
	}

	private native String _getName(long VSPP) throws LibvirtException;

	/**
	 * Fetch the globally unique ID of this storage pool
	 *
	 * @return the UUID as an unpacked int array
	 * @throws LibvirtException
	 */
	public int[] getUUID() throws LibvirtException{
		return _getUUID(VSPP);
	}

	private native int[] _getUUID(long VSPP) throws LibvirtException;


	/**
	 * Fetch the globally unique ID of the storage pool as a string
	 *
	 * @return the UUID in canonical String format
	 * @throws LibvirtException
	 */
	public String getUUIDString() throws LibvirtException{
		return _getUUIDString(VSPP);
	}

	private native String _getUUIDString(long VSPP) throws LibvirtException;

	/**
	 * Fetch an XML document describing all aspects of the storage pool.
	 * This is suitable for later feeding back into the virStoragePoolCreateXML method.
	 *
	 * @param flags flags for XML format options (set of virDomainXMLFlags)
	 * @return a XML document
	 *-java @throws LibvirtException
	 */
	public String getXMLDesc(int flags) throws LibvirtException{
		return _getXMLDesc(VSPP, flags);
	}

	private native String _getXMLDesc(long VSPP, int flags) throws LibvirtException;

	/**
	 * Fetch list of storage volume names
	 *
	 * @return an Array of Strings that contains the names of the storage volumes
	 * @throws LibvirtException
	 */
	public String[] listVolumes() throws LibvirtException {
		return _listVolumes(VSPP);
	}

	private native String[] _listVolumes(long VSPP)
	throws LibvirtException;

	/**
	 * Fetch the number of storage volumes within a pool
	 *
	 * @return the number of storage pools
	 * @throws LibvirtException
	 */
	public int numOfVolumes() throws LibvirtException {
		return _numOfVolumes(VSPP);
	}

	private native int _numOfVolumes(long VSPP) throws LibvirtException;

	/**
	 * Request that the pool refresh its list of volumes.
	 * This may involve communicating with a remote server, and/or initializing new devices at the OS layer
	 *
	 * @param flags flags to control refresh behaviour (currently unused, use 0)
	 * @throws LibvirtException
	 */
	public void refresh(int flags) throws LibvirtException {
		_refresh(VSPP, flags);
	}

	private native int _refresh(long VSPP, int flags) throws LibvirtException;

	/**
	 * Sets the autostart flag
	 *
	 * @param autostart	new flag setting
	 * @throws LibvirtException
	 */
	public void setAutostart(int autostart) throws LibvirtException {
		_setAutostart(VSPP, autostart);
	}

	private native int _setAutostart(long VSPP, int autostart) throws LibvirtException;

	/**
	 * Undefine an inactive storage pool
	 *
	 * @throws LibvirtException
	 */
	public void undefine() throws LibvirtException {
		_undefine(VSPP);
	}

	private native int _undefine(long VSPP) throws LibvirtException;

	/**
	 * Fetch an object representing to a storage volume based on its name within a pool
	 *
	 * @param name name of storage volume
	 * @return The StorageVol object found
	 * @throws LibvirtException
	 */
	public StorageVol storageVolLookupByName(String name)
	throws LibvirtException {
		return new StorageVol(virConnect, _storageVolLookupByName(VSPP, name));
	}

	private native long _storageVolLookupByName(long VSPP, String name)
	throws LibvirtException;

	/**
	 * Create a storage volume within a pool based on an XML description. Not all pools support creation of volumes
	 *
	 * @param xmlDesc description of volume to create
	 * @param flags flags for creation (unused, pass 0)
	 * @return the storage volume
	 * @throws LibvirtException
	 */
	public StorageVol storageVolCreateXML(String xmlDesc, int flags)
	throws LibvirtException {
		return new StorageVol(virConnect, _storageVolCreateXML(VSPP, xmlDesc, flags));
	}

	private native long _storageVolCreateXML(long VSPP, String xmlDesc, int flags)
	throws LibvirtException;

}
