package org.libvirt;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virTypedParameter;
import org.libvirt.jna.virTypedParameterValue;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class TypedParameter {
    protected static final int TYPED_PARAM_INT = virTypedParameter.TYPED_PARAM_INT;
    protected static final int TYPED_PARAM_UINT = virTypedParameter.TYPED_PARAM_UINT;
    protected static final int TYPED_PARAM_LONG = virTypedParameter.TYPED_PARAM_LONG;
    protected static final int TYPED_PARAM_ULONG = virTypedParameter.TYPED_PARAM_ULONG;
    protected static final int TYPED_PARAM_DOUBLE = virTypedParameter.TYPED_PARAM_DOUBLE;
    protected static final int TYPED_PARAM_BOOLEAN = virTypedParameter.TYPED_PARAM_BOOLEAN;
    protected static final int TYPED_PARAM_STRING = virTypedParameter.TYPED_PARAM_STRING;

    /**
     * Parameter name
     */
    public String field;

    /**
     * The type of the parameter
     *
     * @return the Type of the parameter
     */
    public abstract int getType();

    /**
     * Utility function for displaying the type
     *
     * @return the Type of the parameter as string
     */
    public abstract String getTypeAsString();

    /**
     * Utility function for displaying the value
     *
     * @return the value of the parameter in String form
     */
    public abstract String getValueAsString();

    private static final TypedParameter[] EMPTY = new TypedParameter[0];

    public static TypedParameter create(final virTypedParameter vParam) {
        TypedParameter returnValue = null;
        if (vParam != null) {
            switch (vParam.type) {
                case TYPED_PARAM_INT:
                    returnValue = new TypedIntParameter(vParam.value.i);
                    break;
                case TYPED_PARAM_UINT:
                    returnValue = new TypedUintParameter(vParam.value.i);
                    break;
                case TYPED_PARAM_LONG:
                    returnValue = new TypedLongParameter(vParam.value.l);
                    break;
                case TYPED_PARAM_ULONG:
                    returnValue = new TypedUlongParameter(vParam.value.l);
                    break;
                case TYPED_PARAM_DOUBLE:
                    returnValue = new TypedDoubleParameter(vParam.value.d);
                    break;
                case TYPED_PARAM_BOOLEAN:
                    returnValue = new TypedBooleanParameter(vParam.value.b);
                    break;
                case TYPED_PARAM_STRING:
                    returnValue = new TypedStringParameter(vParam.value.s);
                    break;
            default:
                    // Unknown type: nothing to do.
            }
            if (returnValue != null) {
                returnValue.field = Native.toString(vParam.field);
            }
        }
        return returnValue;
    }

    public static virTypedParameter toNative(final TypedParameter param) {
        virTypedParameter returnValue = new virTypedParameter();
        returnValue.value = new virTypedParameterValue();
        returnValue.field = copyOf(param.field.getBytes(StandardCharsets.UTF_8), Libvirt.VIR_TYPED_PARAM_FIELD_LENGTH);
        returnValue.type = param.getType();
        switch (param.getType()) {
            case TYPED_PARAM_INT:
                returnValue.value.i = ((TypedIntParameter) param).value;
                returnValue.value.setType(int.class);
                break;
            case TYPED_PARAM_UINT:
                returnValue.value.i = ((TypedUintParameter) param).value;
                returnValue.value.setType(int.class);
                break;
            case TYPED_PARAM_LONG:
                returnValue.value.l = ((TypedLongParameter) param).value;
                returnValue.value.setType(long.class);
                break;
            case TYPED_PARAM_ULONG:
                returnValue.value.l = ((TypedUlongParameter) param).value;
                returnValue.value.setType(long.class);
                break;
            case TYPED_PARAM_DOUBLE:
                returnValue.value.d = ((TypedDoubleParameter) param).value;
                returnValue.value.setType(double.class);
                break;
            case TYPED_PARAM_BOOLEAN:
                returnValue.value.b = (byte) (((TypedBooleanParameter) param).value ? 1 : 0);
                returnValue.value.setType(byte.class);
                break;
            case TYPED_PARAM_STRING:
                returnValue.value.s = ((TypedStringParameter)param).value;
                returnValue.value.setType(String.class);
                break;
        default:
                // Unknown type: nothing to do.
        }
        return returnValue;
    }

    public static byte[] copyOf(final byte[] original, final int length) {
        byte[] returnValue = new byte[length];
        int originalLength = original.length;
        Arrays.fill(returnValue, (byte) 0);
        System.arraycopy(original, 0, returnValue, 0, originalLength);
        return returnValue;
    }

    public static TypedParameter[] fromPointer(Pointer ptr, int n) {
        if (n == 0) {
            return EMPTY;
        }
        virTypedParameter param = new virTypedParameter(ptr);
        param.read();
        virTypedParameter[] params = (virTypedParameter[]) param.toArray(n);
        TypedParameter[] stats = new TypedParameter[n];
        for (int i = 0; i < n; i++) {
            stats[i] = TypedParameter.create(params[i]);
        }
        Libvirt.INSTANCE.virTypedParamsFree(ptr, n);
        return stats;
    }
}
