package org.libvirt.flags;

final class DomainCreateFlags {
    static final int VIR_DOMAIN_NONE = 0;
    /**
     * Restore or alter metadata
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_REDEFINE = (1 << 0);
    /**
     * With redefine, make snapshot current
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_CURRENT = (1 << 1);
    /**
     * Make snapshot without remembering it
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_NO_METADATA = (1 << 2);
    /**
     * Stop running guest after snapshot
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_HALT = (1 << 3);
    /**
     * disk snapshot, not system checkpoint
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_DISK_ONLY = (1 << 4);
    /**
     * reuse any existing external files
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_REUSE_EXT = (1 << 5);
    /**
     * use guest agent to quiesce all mounted file systems within the domain
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_QUIESCE = (1 << 6);
    /**
     * atomically avoid partial changes
     */
    static final int VIR_DOMAIN_SNAPSHOT_CREATE_ATOMIC = (1 << 7);
}
