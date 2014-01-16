package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.NativeLong;
import com.sun.jna.Structure;

/**
 * JNA mapping for the virNodeInfo structure
 */
public class virNodeInfo extends Structure {
    public class ByReference extends virNodeInfo implements Structure.ByReference {
    };

    public class ByValue extends virNodeInfo implements Structure.ByValue {
    };

    public byte model[] = new byte[32];
    public NativeLong memory;
    public int cpus;
    public int mhz;
    public int nodes;
    public int sockets;
    public int cores;
    public int threads;

    private static final List<String> fields = Arrays.asList(
            "model", "memory", "cpus", "mhz", "nodes",
            "sockets", "cores", "threads");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
