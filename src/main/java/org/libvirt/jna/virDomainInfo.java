package org.libvirt.jna;

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

    protected java.util.List getFieldOrder() {
        return java.util.Arrays.asList(new String[] {
            "state", "maxMem", "memory", "nrVirtCpu", "cpuTime" });
    }
}
