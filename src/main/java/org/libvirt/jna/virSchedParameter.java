package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virSchedParameter structure
 */
public class virSchedParameter extends Structure {
    public byte field[] = new byte[Libvirt.VIR_DOMAIN_SCHED_FIELD_LENGTH];
    public int type;
    public virSchedParameterValue value;
}
