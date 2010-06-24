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
        rx_bytes = vStats.rx_bytes;
        rx_packets = vStats.rx_packets;
        rx_errs = vStats.rx_errs;
        rx_drop = vStats.rx_drop;
        tx_bytes = vStats.tx_bytes;
        tx_packets = vStats.tx_packets;
        tx_errs = vStats.tx_errs;
        tx_drop = vStats.tx_drop;
    }

    @Override
    public String toString() {
        return String.format("rx_bytes:%d%nrx_packets:%d%nrx_errs:%d%nrx_drop:%d%ntx_bytes:%d%ntx_packets:%d%ntx_errs:%d%ntx_drop:%d%n", rx_bytes, rx_packets, rx_errs, rx_drop, tx_bytes, tx_packets, tx_errs, tx_drop);
    }

}
