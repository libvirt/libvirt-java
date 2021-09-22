package org.libvirt;

import org.libvirt.jna.virDomainBlockJobInfo;

public class DomainBlockJobInfo {
    public int type;
    public long bandwidth;
    public long cur;
    public long end;

    public DomainBlockJobInfo() {
    }

    public DomainBlockJobInfo(final virDomainBlockJobInfo virInfo) {
        type = virInfo.type;
        bandwidth = virInfo.bandwidth;
        cur = virInfo.cur;
        end = virInfo.end;
    }

    @Override
    public String toString() {
        return String.format("type:%s%nbandwith:%s%ncur:%s%nend:%s%n",
                type, bandwidth, cur, end);
    }
}
