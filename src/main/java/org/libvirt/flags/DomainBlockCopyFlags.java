package org.libvirt.flags;

public final class DomainBlockCopyFlags {
    /**
     * Limit copy to top of source backing chain
     */
    public static final int VIR_DOMAIN_BLOCK_COPY_SHALLOW = (1 << 0);
    /**
     * Reuse existing external file for a copy
     */
    public static final int VIR_DOMAIN_BLOCK_COPY_REUSE_EXT = (1 << 1);
    /**
     * Don't force usage of recoverable job for the copy operation
     */
    public static final int VIR_DOMAIN_BLOCK_COPY_TRANSIENT_JOB = (1 << 2);
}
