package org.libvirt;

import org.libvirt.jna.virDomainBlockInfo;

public class DomainBlockInfo {
    protected long capacity;
    protected long allocation;
    protected long physical;

    public DomainBlockInfo(final virDomainBlockInfo info) {
        capacity = info.capacity;
        allocation = info.allocation;
        physical = info.physical;
    }

    public long getAllocation() {
        return allocation;
    }

    public long getCapacity() {
        return capacity;
    }

    public long getPhysical() {
        return physical;
    }

    public void setAllocation(final long allocation) {
        this.allocation = allocation;
    }

    public void setCapacity(final long capacity) {
        this.capacity = capacity;
    }

    public void setPhysical(final long physical) {
        this.physical = physical;
    }

    @Override
    public String toString() {
        return String.format("capacity:%d%nallocation:%d%nphysical:%d%n", capacity, allocation, physical);
    }
}
