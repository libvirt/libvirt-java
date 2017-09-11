package org.libvirt.flags;

public final class DomainMigrateFlags {
    /**
     * live migration
     */
    static final int VIR_MIGRATE_LIVE = (1 << 0);
    /**
     * direct source -> dest host control channel
     */
    static final int VIR_MIGRATE_PEER2PEER = (1 << 1);
    /**
     * Note the less-common spelling that we're stuck with: VIR_MIGRATE_TUNNELLED should be VIR_MIGRATE_TUNNELED
     * <p>
     * tunnel migration data over libvirtd connection
     */
    static final int VIR_MIGRATE_TUNNELLED = (1 << 2);
    /**
     * persist the VM on the destination
     */
    static final int VIR_MIGRATE_PERSIST_DEST = (1 << 3);
    /**
     * undefine the VM on the source
     */
    static final int VIR_MIGRATE_UNDEFINE_SOURCE = (1 << 4);
    /**
     * pause on remote side
     */
    static final int VIR_MIGRATE_PAUSED = (1 << 5);
    /**
     * migration with non-shared storage with full disk copy
     */
    static final int VIR_MIGRATE_NON_SHARED_DISK = (1 << 6);
    /**
     * migration with non-shared storage with incremental copy
     * <p>
     * (same base image shared between source and destination)
     */
    static final int VIR_MIGRATE_NON_SHARED_INC = (1 << 7);
    /**
     * protect for changing domain configuration through the
     * whole migration process; this will be used automatically
     * when supported
     */
    static final int VIR_MIGRATE_CHANGE_PROTECTION = (1 << 8);
    /**
     * force migration even if it is considered unsafe
     */
    static final int VIR_MIGRATE_UNSAFE = (1 << 9);
}
