package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virDomainInterfaceStats structure
 */
public class virDomainInterfaceStats extends Structure {
    public long rx_bytes; // this is a long long in the code, so a long mapping
    // is correct
    public long rx_packets; // this is a long long in the code, so a long
    // mapping is correct
    public long rx_errs; // this is a long long in the code, so a long mapping
    // is correct
    public long rx_drop; // this is a long long in the code, so a long mapping
    // is correct
    public long tx_bytes; // this is a long long in the code, so a long mapping
    // is correct
    public long tx_packets; // this is a long long in the code, so a long
    // mapping is correct
    public long tx_errs; // this is a long long in the code, so a long mapping
    // is correct
    public long tx_drop; // this is a long long in the code, so a long mapping
    // is correct

}
