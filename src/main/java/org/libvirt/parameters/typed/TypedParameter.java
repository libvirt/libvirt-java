package org.libvirt.parameters.typed;

import java.util.Arrays;

import com.sun.jna.Native;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.structures.virTypedParameter;
import org.libvirt.jna.structures.virTypedParameterValue;

/**
 * The abstract parent of the actual TypedParameter classes
 */
public abstract class TypedParameter {

    public static TypedParameter create(virTypedParameter vParam) {
        TypedParameter returnValue = null;
        if (vParam != null) {
            switch (vParam.type) {
                case (1):
                    returnValue = new TypedIntParameter(vParam.value.i, Native.toString(vParam.field));
                    break;
                case (2):
                    returnValue = new TypedUintParameter(vParam.value.i, Native.toString(vParam.field));
                    break;
                case (3):
                    returnValue = new TypedLongParameter(vParam.value.l, Native.toString(vParam.field));
                    break;
                case (4):
                    returnValue = new TypedUlongParameter(vParam.value.l, Native.toString(vParam.field));
                    break;
                case (5):
                    returnValue = new TypedDoubleParameter(vParam.value.d, Native.toString(vParam.field));
                    break;
                case (6):
                    returnValue = new TypedBooleanParameter(vParam.value.b, Native.toString(vParam.field));
                    break;
            }
        }
        return returnValue;
    }

    public static virTypedParameter toNative(TypedParameter param) {
        virTypedParameter returnValue = new virTypedParameter();
        returnValue.value = new virTypedParameterValue();
        returnValue.field = copyOf(param.field.getBytes(), Libvirt.VIR_TYPED_PARAM_FIELD_LENGTH);
        returnValue.type = param.getType();
        switch (param.getType()) {
            case (1):
                returnValue.value.i = ((TypedIntParameter) param).getValue();
                returnValue.value.setType(int.class);
                break;
            case (2):
                returnValue.value.i = ((TypedUintParameter) param).getValue();
                returnValue.value.setType(int.class);
                break;
            case (3):
                returnValue.value.l = ((TypedLongParameter) param).getValue();
                returnValue.value.setType(long.class);
                break;
            case (4):
                returnValue.value.l = ((TypedUlongParameter) param).getValue();
                returnValue.value.setType(long.class);
                break;
            case (5):
                returnValue.value.d = ((TypedDoubleParameter) param).getValue();
                returnValue.value.setType(double.class);
                break;
            case (6):
                returnValue.value.b = (byte) (((TypedBooleanParameter) param).getValue() ? 1 : 0);
                returnValue.value.setType(byte.class);
                break;
        }
        return returnValue;
    }

    public static byte[] copyOf(byte[] original, int length) {
        byte[] returnValue = new byte[length];
        int originalLength = original.length;
        Arrays.fill(returnValue, (byte) 0);
        for (int x = 0; x < originalLength; x++) {
            returnValue[x] = original[x];
        }
        return returnValue;
    }

    /**
     * Parameter name
     */
    private String field;

    public String getField() {
        return field;
    }

    public void setField(final String field) {
        this.field = field;
    }

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
}
