package org.libvirt.flags;

public final class UndefineFlags {
    /**
     * Also remove any managed save
     */
    public static final int MANAGED_SAVE = (1 << 0);
    /**
     * If last use of domain, then also remove any snapshot metadata
     */
    public static final int SNAPSHOTS_METADATA = (1 << 1);
}
