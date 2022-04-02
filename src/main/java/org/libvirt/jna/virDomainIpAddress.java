package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JNA mapping for the virDomainIpAddress structure
 */
public class virDomainIpAddress extends Structure {
    public static class ByReference extends virDomainIpAddress implements Structure.ByReference {}

    private static final List<String> FIELDS = Collections.unmodifiableList(Arrays.asList("type", "addr", "prefix"));

    /** @see org.libvirt.Network.IP_Addr_Type */
    public int type;

    /** IP address */
    public String addr;

    /** IP address prefix length */
    public int prefix;

    public virDomainIpAddress() {}

    public virDomainIpAddress(Pointer p) {
        super(p);
        read();
    }

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
