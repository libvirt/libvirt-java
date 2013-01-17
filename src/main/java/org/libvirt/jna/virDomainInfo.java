package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

/**
 * JNA mapping for the virDomainInfo structure
 */
public class virDomainInfo extends Structure {
    public int state;
    public NativeLong maxMem;
    public NativeLong memory;
    public short nrVirtCpu;
    public long cpuTime;

    private static final List fields = Arrays.asList(
            "state", "maxMem", "memory", "nrVirtCpu", "cpuTime");

    @Override
    protected List getFieldOrder() {
        return fields;
    }
}
