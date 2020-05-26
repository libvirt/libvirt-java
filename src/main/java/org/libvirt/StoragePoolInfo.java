package org.libvirt;

import org.libvirt.jna.virStoragePoolInfo;

/**
 * Detailed information about a storage pool.
 */
public class StoragePoolInfo {

    public enum StoragePoolState {
        /**
         * Not running
         */
        VIR_STORAGE_POOL_INACTIVE,
        /**
         * Initializing pool, not available
         */
        VIR_STORAGE_POOL_BUILDING,
        /**
         * Running normally
         */
        VIR_STORAGE_POOL_RUNNING,
        /**
         * Running degraded
         */
        VIR_STORAGE_POOL_DEGRADED
    }

    /**
     * the running state
     */
    public StoragePoolState state;

    /**
     * Logical size bytes
     */
    public long capacity;

    /**
     * Current allocation bytes
     */
    public long allocation;

    /**
     * Remaining free space bytes
     */
    public long available;;

    /**
     * This is meant to be called from the JNI side, as a convenience
     * constructor
     *
     * @param state
     *            the state, as defined by libvirt
     * @param capacity
     * @param allocation
     * @param available
     */
    StoragePoolInfo(final int state, final long capacity,
                    final long allocation, final long available) {
        switch (state) {
            case 0:
                this.state = StoragePoolState.VIR_STORAGE_POOL_INACTIVE;
                break;
            case 1:
                this.state = StoragePoolState.VIR_STORAGE_POOL_BUILDING;
                break;
            case 2:
                this.state = StoragePoolState.VIR_STORAGE_POOL_RUNNING;
                break;
            case 3:
                this.state = StoragePoolState.VIR_STORAGE_POOL_DEGRADED;
                break;
            default:
                assert false;
        }
        this.capacity = capacity;
        this.allocation = allocation;
        this.available = available;
    }

    StoragePoolInfo(final virStoragePoolInfo vInfo) {
        this(vInfo.state, vInfo.capacity, vInfo.allocation, vInfo.available);
    }

    @Override
    public String toString() {
        return String.format("state:%s%ncapacity:%d%nallocation:%d%navailable:%d%n",
                state, capacity, allocation, available);
    }
}
