package org.libvirt;

import org.libvirt.jna.virDomainMemoryStats;

public class MemoryStatistic {

    protected int tag;
    protected long val;

    public MemoryStatistic(virDomainMemoryStats stat) {
        tag = stat.tag;
        val = stat.val;
    }

    public int getTag() {
        return tag;
    }

    public long getValue() {
        return val;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setValue(long val) {
        this.val = val;
    }
}
