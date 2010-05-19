package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.StoragePoolPointer;
import org.libvirt.jna.StorageVolPointer;
import org.libvirt.jna.virStorageVolInfo;

/**
 * An acutal storage bucket.
 */
public class StorageVol {

    static final class DeleteFlags {
        /**
         * Delete metadata only (fast)
         */
        static final int VIR_STORAGE_POOL_DELETE_NORMAL = 0;
        /**
         * Clear all data to zeros (slow)
         */
        static final int VIR_STORAGE_POOL_DELETE_ZEROED = 1;
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
    StorageVolPointer VSVP;

    /**
     * The VirConnect Object that represents the Hypervisor of this Domain
     */
    protected Connect virConnect;

    /**
     * the libvirt instance
     */
    protected Libvirt libvirt;

    /**
     * Constructs a VirStorageVol object from a known native virStoragePoolPtr,
     * and a VirConnect object. For use when native libvirt returns a
     * virStorageVolPtr, i.e. error handling.
     * 
     * @param virConnect
     *            the Domain's hypervisor
     * @param VSVP
     *            the native virStorageVolPtr
     */
    StorageVol(Connect virConnect, StorageVolPointer VSVP) {
        this.virConnect = virConnect;
        this.VSVP = VSVP;
        libvirt = virConnect.libvirt;
    }

    /**
     * Delete the storage volume from the pool
     * 
     * @param flags
     *            future flags, use 0 for now
     * @throws LibvirtException
     */
    public void delete(int flags) throws LibvirtException {
        libvirt.virStorageVolDelete(VSVP, flags);
        processError();
    }

    @Override
    public void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the storage volume handle. The underlying storage volume contains
     * to exist
     * 
     * @throws LibvirtException
     * @return number of references left (>= 0) for success, -1 for failure.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VSVP != null) {
            libvirt.virStorageVolFree(VSVP);
            processError();
            VSVP = null;
        }
        return success;
    }

    /**
     * Provides the connection object associated with a storage volume. The
     * reference counter on the connection is not increased by this call.
     * 
     * @return the Connect object
     */
    public Connect getConnect() {
        return virConnect;
    }

    /**
     * Fetches volatile information about the storage volume such as its current
     * allocation
     * 
     * @return StorageVolInfo object
     * @throws LibvirtException
     */
    public StorageVolInfo getInfo() throws LibvirtException {
        virStorageVolInfo vInfo = new virStorageVolInfo();
        libvirt.virStorageVolGetInfo(VSVP, vInfo);
        processError();
        return new StorageVolInfo(vInfo);
    }

    /**
     * Fetch the storage volume key. This is globally unique, so the same volume
     * will have the same key no matter what host it is accessed from
     * 
     * @return the key
     * @throws LibvirtException
     */
    public String getKey() throws LibvirtException {
        String returnValue = libvirt.virStorageVolGetKey(VSVP);
        processError();
        return returnValue;
    }

    /**
     * Fetch the storage volume name. This is unique within the scope of a pool
     * 
     * @return the name
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        String returnValue = libvirt.virStorageVolGetName(VSVP);
        processError();
        return returnValue;
    }

    /**
     * Fetch the storage volume path. Depending on the pool configuration this
     * is either persistent across hosts, or dynamically assigned at pool
     * startup. Consult pool documentation for information on getting the
     * persistent naming
     * 
     * @return the storage volume path
     * @throws LibvirtException
     */
    public String getPath() throws LibvirtException {
        String returnValue = libvirt.virStorageVolGetPath(VSVP);
        processError();
        return returnValue;
    }

    /**
     * Fetch an XML document describing all aspects of this storage volume
     * 
     * @param flags
     *            flags for XML generation (unused, pass 0)
     * @return the XML document
     * @throws LibvirtException
     */
    public String getXMLDesc(int flags) throws LibvirtException {
        String returnValue = libvirt.virStorageVolGetXMLDesc(VSVP, flags);
        processError();
        return returnValue;
    }

    /**
     * Error handling logic which should be called after every libvirt call
     * 
     * @throws LibvirtException
     */
    protected void processError() throws LibvirtException {
        virConnect.processError();
    }

    /**
     * Fetch a storage pool which contains this volume
     * 
     * @return StoragePool object,
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByVolume() throws LibvirtException {
        StoragePoolPointer ptr = libvirt.virStoragePoolLookupByVolume(VSVP);
        processError();
        return new StoragePool(virConnect, ptr);
    }

    /**
     * Ensure data previously on a volume is not accessible to future reads
     * 
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStorageVolWipe">Libvirt
     *      Documentation</a>
     * @return 0 on success, or -1 on error
     * @throws LibvirtException
     */
    public int wipe() throws LibvirtException {
        int returnValue = libvirt.virStorageVolWipe(VSVP, 0);
        processError();
        return returnValue;
    }
}
