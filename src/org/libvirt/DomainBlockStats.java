package org.libvirt;

/**
 * This class holds the counters for block device statistics.
 *
 * @author stoty
 * @see Domain#blockStats
 */
public class DomainBlockStats {
	public long  rd_req;
	public long  rd_bytes;
	public long  wr_req;
	public long  wr_bytes;
	public long  errs;
}
