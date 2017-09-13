package org.libvirt;

import org.libvirt.jna.structures.virDomainBlockJobInfo;

public class DomainBlockJobInfo {
    private long bandwidth;
    private long cur;
    private long end;

    public DomainBlockJobInfo(virDomainBlockJobInfo info) {
        bandwidth = info.bandwidth;
        cur = info.cur;
        end = info.end;
    }

    public long getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(final long bandwidth) {
        this.bandwidth = bandwidth;
    }

    public long getCur() {
        return cur;
    }

    public void setCur(final long cur) {
        this.cur = cur;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(final long end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return String.format("bandwidth:%d%ncur:%d%nend:%d%n", bandwidth, cur, end);
    }
}
