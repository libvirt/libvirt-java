package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

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

    private static final List fields = Arrays.asList(
            "rx_bytes", "rx_packets", "rx_errs", "rx_drop",
            "tx_bytes", "tx_packets", "tx_errs", "tx_drop");

    @Override
    protected List getFieldOrder() {
        return fields;
    }
}
