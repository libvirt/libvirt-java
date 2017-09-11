package org.libvirt.flags;

public final class DomainBlockJobAbortFlags {
    /**
     * Request only, do not wait for completion
     */
    public static final int VIR_DOMAIN_BLOCK_JOB_ABORT_ASYNC = (1 << 0);
    /**
     * Pivot to new file when ending a copy or active commit job
     */
    public static final int VIR_DOMAIN_BLOCK_JOB_ABORT_PIVOT = (1 << 1);
}
