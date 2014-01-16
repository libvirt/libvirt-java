package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class virDomainMemoryStats extends Structure {
    public int tag ;
    public long val ;

    private static final List<String> fields = Arrays.asList( "tag", "val");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
