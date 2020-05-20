package org.libvirt;

import org.libvirt.jna.virDomainMemoryStats;

public class MemoryStatistic {

    protected int tag;
    protected long val;

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
