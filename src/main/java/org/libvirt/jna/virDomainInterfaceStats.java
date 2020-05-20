package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virDomainInterfaceStats structure
 */
public class virDomainInterfaceStats extends Structure {
    //CHECKSTYLE:OFF: MemberName - public interface: TODO: deprecate and rename
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
    //CHECKSTYLE:ON: MemberName - public interface: TODO: deprecate and rename

    private static final List<String> FIELDS = Arrays.asList(
            "rx_bytes", "rx_packets", "rx_errs", "rx_drop",
            "tx_bytes", "tx_packets", "tx_errs", "tx_drop");

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
