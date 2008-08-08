package org.libvirt;

public class StorageVol {

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

	public static enum Type {
		/**
		 * Regular file based volumes
		 */
		VIR_STORAGE_VOL_FILE,
		/**
		 * Block based volumes
		 */
		VIR_STORAGE_VOL_BLOCK
	}

	/**
	 * the native virStorageVolPtr.
	 */
	private long  VSVP;

	/**
	 * The VirConnect Object that represents the Hypervisor of this Domain
	 */
	private Connect virConnect;


	/**
	 * Constructs a VirStorageVol object from a known native virStoragePoolPtr, and a VirConnect object.
	 * For use when native libvirt returns a virStorageVolPtr, i.e. error handling.
	 *
	 * @param virConnect the Domain's hypervisor
	 * @param VSVP the native virStorageVolPtr
	 */
	StorageVol(Connect virConnect, long VSVP){
		this.virConnect = virConnect;
		this.VSVP = VSVP;
	}

	/**
	 * Fetch a storage pool which contains this volume
	 *
	 * @return StoragePool object,
	 * @throws LibvirtException
	 */
	public StoragePool storagePoolLookupByVolume()
	throws LibvirtException {
		return new StoragePool(virConnect, _storagePoolLookupByVolume(VSVP));
	}

	private native long _storagePoolLookupByVolume(long VSVP)
	throws LibvirtException;

	/**
	 * Delete the storage volume from the pool
	 *
	 * @param flags future flags, use 0 for now
	 * @throws LibvirtException
	 */
	public void delete(int flags) throws LibvirtException{
		_delete(VSVP, flags);
	}

	private native int _delete(long VSVP, int flags) throws LibvirtException;

	/**
	 * Release the storage volume handle. The underlying storage volume contains to exist
	 *
	 * @throws LibvirtException
	 */
	public void free() throws LibvirtException{
		_free(VSVP);
	}

	private native int _free(long VSVP) throws LibvirtException;

	/**
	 * Provides the connection object associated with a storage volume. The reference counter on the connection is not increased by this call.
	 *
	 * @return the Connect object
	 */
	public Connect getConnect(){
		return virConnect;
	}

	/**
	 * Fetches volatile information about the storage volume such as its current allocation
	 *
	 * @return StorageVolInfo object
	 * @throws LibvirtException
	 */
	public StorageVolInfo getInfo() throws LibvirtException{
		return _getInfo(VSVP);
	}

	private native StorageVolInfo _getInfo(long VSVP) throws LibvirtException;

	/**
	 * Fetch the storage volume key. This is globally unique, so the same volume will have the same key no matter what host it is accessed from
	 *
	 * @return the key
	 * @throws LibvirtException
	 */
	public String getKey() throws LibvirtException{
		return _getKey(VSVP);
	}

	private native String _getKey(long VSVP) throws LibvirtException;

	/**
	 * Fetch the storage volume name. This is unique within the scope of a pool
	 *
	 * @return the name
	 * @throws LibvirtException
	 */
	public String getName() throws LibvirtException{
		return _getName(VSVP);
	}

	private native String _getName(long VSVP) throws LibvirtException;

	/**
	 * Fetch the storage volume path.
	 * Depending on the pool configuration this is either persistent across hosts, or dynamically assigned at pool startup.
	 * Consult pool documentation for information on getting the persistent naming
	 *
	 * @return the storage volume path
	 * @throws LibvirtException
	 */
	public String getPath() throws LibvirtException{
		return _getPath(VSVP);
	}

	private native String _getPath(long VSVP) throws LibvirtException;

	/**
	 * Fetch an XML document describing all aspects of this storage volume
	 *
	 * @param flags flags for XML generation (unused, pass 0)
	 * @return the XML document
	 * @throws LibvirtException
	 */
	public String getXMLDesc(int flags) throws LibvirtException{
		return _getXMLDesc(VSVP, flags);
	}

	private native String _getXMLDesc(long VSVP, int flags) throws LibvirtException;
}
