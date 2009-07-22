package org.libvirt;

import org.libvirt.jna.virDomainInterfaceStats;

/**
 * The Domain.interfaceStats method returns th network counters in this object
 * 
 * @author stoty
 * 
 */
public class DomainInterfaceStats {
    public long rx_bytes;
    public long rx_packets;
    public long rx_errs;
    public long rx_drop;
    public long tx_bytes;
    public long tx_packets;
    public long tx_errs;
    public long tx_drop;

    public DomainInterfaceStats() {

    }

    public DomainInterfaceStats(virDomainInterfaceStats vStats) {
        this.rx_bytes = vStats.rx_bytes;
        this.rx_packets = vStats.rx_packets;
        this.rx_errs = vStats.rx_errs;
        this.rx_drop = vStats.rx_drop;
        this.tx_bytes = vStats.tx_bytes;
        this.tx_packets = vStats.tx_packets;
        this.tx_errs = vStats.tx_errs;
        this.tx_drop = vStats.tx_drop;
    }
}
