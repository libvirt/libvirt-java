package org.libvirt.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * JNA mapping for the virTypedParameter structure
 *
 * This is the preferred alias over virSchedParameter, virBlkioParameter,
 * virMemoryParameter since Libvirt 0.9.2.
 */
public class virTypedParameter extends Structure {
    public byte[] field = new byte[Libvirt.VIR_TYPED_PARAM_FIELD_LENGTH];
    public int type;
    public virTypedParameterValue value;

    private static final List<String> FIELDS = Arrays.asList(
        "field", "type", "value");

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
