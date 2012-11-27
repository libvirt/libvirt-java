package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virSchedParameter structure
 */
public class virSchedParameter extends Structure {
    public byte field[] = new byte[Libvirt.VIR_DOMAIN_SCHED_FIELD_LENGTH];
    public int type;
    public virSchedParameterValue value;

    protected java.util.List getFieldOrder() {
        return java.util.Arrays.asList(new String[] {
            "field", "type", "value" });
    }
}
