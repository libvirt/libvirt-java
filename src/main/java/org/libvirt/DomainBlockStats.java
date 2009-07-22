package org.libvirt;

import org.libvirt.jna.virDomainBlockStats;

/**
 * This class holds the counters for block device statistics.
 * 
 * @author stoty
 * @see Domain#blockStats
 */
public class DomainBlockStats {
    public long rd_req;
    public long rd_bytes;
    public long wr_req;
    public long wr_bytes;
    public long errs;

    public DomainBlockStats() {
    }

    public DomainBlockStats(virDomainBlockStats vStats) {
        this.rd_req = vStats.rd_req;
        this.rd_bytes = vStats.rd_bytes;
        this.wr_req = vStats.wr_req;
        this.wr_bytes = vStats.wr_bytes;
        this.errs = vStats.errs;
    }
}
