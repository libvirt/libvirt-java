package org.libvirt.parameters;

/**
 * Class for representing a boolean typed parameter
 */
public final class TypedBooleanParameter extends TypedParameter {
    public boolean value;

    public TypedBooleanParameter() {
    }

    public TypedBooleanParameter(boolean value) {
        this.value = value;
    }

    public TypedBooleanParameter(byte value) {
        this.value = (value) != 0;
    }

    public int getType() {
        return 6;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_BOOLEAN";
    }

    public String getValueAsString() {
        return Boolean.toString(value);
    }
}
