package org.libvirt.jna;

import com.sun.jna.Structure;

public class virDomainMemoryStats extends Structure {
    public int tag ;
    public long val ;

    protected java.util.List getFieldOrder() {
        return java.util.Arrays.asList(new String[] { "tag", "val" });
    }
}
