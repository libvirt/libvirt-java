package org.libvirt;

import org.libvirt.jna.virDomainMemoryStats;

public class MemoryStatistic {

    protected int tag;
    protected long val;

    public static final class MemoryStatisticTags {
        public static final int SWAP_IN        = 0;

        public static final int SWAP_OUT       = 1;

        public static final int MAJOR_FAULT    = 2;

        public static final int MINOR_FAULT    = 3;

        public static final int UNUSED         = 4;

        public static final int AVAILABLE      = 5;

        public static final int ACTUAL_BALLOON = 6;

        public static final int RSS            = 7;

        public static final int USABLE         = 8;

        public static final int LAST_UPDATE    = 9;

        public static final int DISK_CACHES    = 10;

        public static final int PGALLOC        = 11;

        public static final int HUGETLB_PGFAIL = 12;

        public static final int NR             = 13;
    }

    public MemoryStatistic(final virDomainMemoryStats stat) {
        tag = stat.tag;
        val = stat.val;
    }

    public int getTag() {
        return tag;
    }

    public long getValue() {
        return val;
    }

    public void setTag(final int tag) {
        this.tag = tag;
    }

    public void setValue(final long val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return String.format("tag:%d%nval:%d%n", tag, val);
    }
}
