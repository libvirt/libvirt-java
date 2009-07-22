package org.libvirt.jna;

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
}