package org.libvirt;

import org.libvirt.jna.virStorageVolInfo;

/**
 * Detailed information about a storage pool.
 */
public class StorageVolInfo {

    public static enum VirStorageVolType {
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
     * The type of the Volume
     */
    public VirStorageVolType type;
    /**
     * Logical size bytes
     */
    public long capacity;

    /**
     * Current allocation bytes
     */
    public long allocation;;

    /**
     * This is meant to be called from the JNI side, as a convenience
     * constructor
     * 
     * @param type
     *            the type, as defined by libvirt
     * @param capacity
     * @param allocation
     */
    StorageVolInfo(int type, long capacity, long allocation) {
        switch (type) {
            case 0:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_FILE;
                break;
            case 1:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_BLOCK;
                break;
            default:
                assert (false);
        }
        this.capacity = capacity;
        this.allocation = allocation;
    }

    StorageVolInfo(virStorageVolInfo volInfo) {
        this(volInfo.type, volInfo.capacity, volInfo.allocation);
    }

    @Override
    public String toString() {
        return String.format("type:%s%ncapacity:%d%nallocation:%d%n", type, capacity, allocation);
    }
}
