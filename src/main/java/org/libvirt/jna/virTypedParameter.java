package org.libvirt.jna;

import com.sun.jna.Pointer;
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
    public static final int TYPED_PARAM_INT = 1;
    public static final int TYPED_PARAM_UINT = 2;
    public static final int TYPED_PARAM_LONG = 3;
    public static final int TYPED_PARAM_ULONG = 4;
    public static final int TYPED_PARAM_DOUBLE = 5;
    public static final int TYPED_PARAM_BOOLEAN = 6;
    public static final int TYPED_PARAM_STRING = 7;

    public byte[] field = new byte[Libvirt.VIR_TYPED_PARAM_FIELD_LENGTH];
    public int type;
    public virTypedParameterValue value;

    public virTypedParameter(){
        super();
    }

    public virTypedParameter(Pointer p) {
        super(p);
    }

    @Override
    public void read() {
        super.read();
        if (type == TYPED_PARAM_STRING) {
            value.readField("s");
        }
    }

    private static final List<String> FIELDS = Arrays.asList(
        "field", "type", "value");

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
