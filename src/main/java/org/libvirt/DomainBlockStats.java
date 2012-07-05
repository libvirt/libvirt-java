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
        rd_req = vStats.rd_req;
        rd_bytes = vStats.rd_bytes;
        wr_req = vStats.wr_req;
        wr_bytes = vStats.wr_bytes;
        errs = vStats.errs;
    }
}
