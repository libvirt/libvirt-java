package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virVcpuInfo structure
 */
public class virVcpuInfo extends Structure {
    public int number;
    public int state;
    public long cpuTime; // this is a long long in the code, so a long mapping
    // is correct
    public int cpu;

    protected java.util.List getFieldOrder() {
        return java.util.Arrays.asList(new String[] {
            "number", "state", "cpuTime", "cpu" });
    }
}
