package org.libvirt;


/**
 * The VirDomain.interfaceStats method returns th network counters in this object
 * 
 * @author stoty
 *
 */
public class VirDomainInterfaceStats {
	public long rx_bytes;
	public long rx_packets;
	public long rx_errs;
	public long rx_drop;
	public long tx_bytes;
	public long tx_packets;
	public long tx_errs;
	public long tx_drop;
}
