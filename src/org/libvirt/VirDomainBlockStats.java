package org.libvirt;

/**
 * This class holds the counters for block device statistics.
 *
 * @author stoty
 * @see VirDomain#blockStats
 */
public class VirDomainBlockStats {
	public long  rd_req;
	public long  rd_bytes;
	public long  wr_req;
	public long  wr_bytes;
	public long  errs;
}
