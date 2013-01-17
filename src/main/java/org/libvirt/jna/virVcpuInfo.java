package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

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

    private static final List fields = Arrays.asList(
            "number", "state", "cpuTime", "cpu");

    @Override
    protected List getFieldOrder() {
        return fields;
    }
}
