package org.libvirt.flags;

public final class DomainSnapshotListFlags {
    /**
     * Filter by snapshots with no parents, when listing a domain
     */
    public static final int ROOTS = (1 << 0);

    /**
     * List all descendants, not just children, when listing a snapshot
     */
    public static final int DESCENDANTS = (1 << 0);

    /** For historical reasons, groups do not use contiguous bits. */

    /**
     * Filter by snapshots with no children
     */
    public static final int LEAVES = (1 << 2);

    /**
     * Filter by snapshots that have children
     */
    public static final int NO_LEAVES = (1 << 3);

    /**
     * Filter by snapshots which have metadata
     */
    public static final int METADATA = (1 << 1);

    /**
     * Filter by snapshots with no metadata
     */
    public static final int NO_METADATA = (1 << 4);
}
