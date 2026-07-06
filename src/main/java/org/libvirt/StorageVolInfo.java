package org.libvirt;

import org.libvirt.jna.virStorageVolInfo;

/**
 * Detailed information about a storage pool.
 */
public class StorageVolInfo {

    public enum VirStorageVolType {
        /**
         * Regular file based volumes
         */
        VIR_STORAGE_VOL_FILE,
        /**
         * Block based volumes
         */
        VIR_STORAGE_VOL_BLOCK,
        /**
         * Directory-passthrough based volume
         */
        VIR_STORAGE_VOL_DIR,
        /**
         * Network volumes like RBD (RADOS Block Device)
         */
        VIR_STORAGE_VOL_NETWORK,
        /**
         * Network accessible directory that can contain other network volumes
         */
        VIR_STORAGE_VOL_NETDIR,
        /**
         * Ploop based volumes
         */
        VIR_STORAGE_VOL_PLOOP
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
    StorageVolInfo(final int type, final long capacity, final long allocation) {
        switch (type) {
            case 0:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_FILE;
                break;
            case 1:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_BLOCK;
                break;
            case 2:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_DIR;
                break;
            case 3:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_NETWORK;
                break;
            case 4:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_NETDIR;
                break;
            case 5:
                this.type = VirStorageVolType.VIR_STORAGE_VOL_PLOOP;
                break;
            default:
                assert false;
        }
        this.capacity = capacity;
        this.allocation = allocation;
    }

    StorageVolInfo(final virStorageVolInfo volInfo) {
        this(volInfo.type, volInfo.capacity, volInfo.allocation);
    }

    @Override
    public String toString() {
        return String.format("type:%s%ncapacity:%d%nallocation:%d%n", type, capacity, allocation);
    }
}
