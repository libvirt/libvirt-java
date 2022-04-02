package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * JNA mapping for the virDomainInterface structure
 */
public class virDomainInterface extends Structure {
    private static final List<String> FIELDS = Collections.unmodifiableList(Arrays.asList("name", "hwaddr", "naddrs", "addrs"));

    public static class Ptr extends PointerType {
        public Ptr() {}
        private Ptr(Pointer p) { super(p); }
    }

    /** Interface name */
    public String name;

    /** Hardware address, may be null */
    public String hwaddr;

    /** Number of items pointed to by addrs */
    public int naddrs;

    /** Array of IP addresses */
    public virDomainIpAddress.ByReference addrs;

    public virDomainInterface() {}

    public virDomainInterface(Pointer p) {
        super(p);
        read();
    }

    public Ptr getPtr() {
        Pointer p = getPointer();
        return p == null ? null : new Ptr(p);
    }

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
