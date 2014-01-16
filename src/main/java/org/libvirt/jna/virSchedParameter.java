package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virSchedParameter structure
 */
public class virSchedParameter extends Structure {
    public byte field[] = new byte[Libvirt.VIR_DOMAIN_SCHED_FIELD_LENGTH];
    public int type;
    public virSchedParameterValue value;

    private static final List<String> fields = Arrays.asList(
            "field", "type", "value");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
