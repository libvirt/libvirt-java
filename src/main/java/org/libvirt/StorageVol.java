package org.libvirt;

import org.libvirt.jna.StoragePoolPointer;
import org.libvirt.jna.StorageVolPointer;
import org.libvirt.jna.virStorageVolInfo;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

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

    public static final class ResizeFlags {
        /**
         * force allocation of new size
        */
        public static final int ALLOCATE = 1;

        /**
         * size is relative to current
         */
        public static final int DELTA = 2;

        /**
         * allow decrease in capacity
         */
        public static final int SHRINK = 4;
    }

    public enum Type {
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
    StorageVolPointer vsvp;

    /**
     * The VirConnect Object that represents the Hypervisor of this Domain
     */
    protected Connect virConnect;

    /**
     * Constructs a VirStorageVol object from a known native virStoragePoolPtr,
     * and a VirConnect object. For use when native libvirt returns a
     * virStorageVolPtr, i.e. error handling.
     *
     * @param virConnect
     *            the Domain's hypervisor
     * @param vsvp
     *            the native virStorageVolPtr
     */
    StorageVol(final Connect virConnect, final StorageVolPointer vsvp) {
        this.virConnect = virConnect;
        this.vsvp = vsvp;
    }

    /**
     * Delete the storage volume from the pool
     *
     * @param flags
     *            future flags, use 0 for now
     * @throws LibvirtException
     */
    public void delete(final int flags) throws LibvirtException {
        processError(libvirt.virStorageVolDelete(vsvp, flags));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the storage volume handle. The underlying storage volume contains
     * to exist
     *
     * @throws LibvirtException
     * @return number of references left (>= 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vsvp != null) {
            success = processError(libvirt.virStorageVolFree(vsvp));
            vsvp = null;
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
        processError(libvirt.virStorageVolGetInfo(vsvp, vInfo));
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
        return processError(libvirt.virStorageVolGetKey(vsvp));
    }

    /**
     * Fetch the storage volume name. This is unique within the scope of a pool
     *
     * @return the name
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virStorageVolGetName(vsvp));
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
        return processError(libvirt.virStorageVolGetPath(vsvp)).toString();
    }

    /**
     * Fetch an XML document describing all aspects of this storage volume
     *
     * @param flags
     *            flags for XML generation (unused, pass 0)
     * @return the XML document
     * @throws LibvirtException
     */
    public String getXMLDesc(final int flags) throws LibvirtException {
        return processError(libvirt.virStorageVolGetXMLDesc(vsvp, flags)).toString();
    }

    /**
     * Fetch a storage pool which contains this volume
     *
     * @return StoragePool object, or {@code null} if not found.
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByVolume() throws LibvirtException {
        StoragePoolPointer ptr = processError(libvirt.virStoragePoolLookupByVolume(vsvp));
        return (ptr == null) ? null : new StoragePool(virConnect, ptr);
    }

    /**
     * Ensure data previously on a volume is not accessible to future reads
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virStorageVolWipe">
        Libvirt Documentation</a>
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int wipe() throws LibvirtException {
        return processError(libvirt.virStorageVolWipe(vsvp, 0));
    }

    /**
     * Resize a volume
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virStorageVolResize">
        Libvirt Documentation</a>
     * @param capacity
     *               new capacity for volume
     * @param flags
     *               flags for resizing, see libvirt API for exact flags
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int resize(final long capacity, final int flags)
            throws LibvirtException {
        return processError(libvirt.virStorageVolResize(vsvp, capacity, flags));
    }
}
