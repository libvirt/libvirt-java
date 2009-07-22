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
}
