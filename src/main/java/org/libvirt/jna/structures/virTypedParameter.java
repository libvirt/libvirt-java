package org.libvirt.jna.structures;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;
import org.libvirt.jna.Libvirt;

/**
 * JNA mapping for the virTypedParameter structure
 */
public class virTypedParameter extends Structure {
    public byte field[] = new byte[Libvirt.VIR_TYPED_PARAM_FIELD_LENGTH];
    public int type;
    public virTypedParameterValue value;

    private static final List<String> fields = Arrays.asList("field", "type", "value");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
