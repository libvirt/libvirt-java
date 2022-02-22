package org.libvirt;

/**
 * Class for representing a boolean typed parameter
 */
public final class TypedBooleanParameter extends TypedParameter {
    public boolean value;

    public TypedBooleanParameter() {

    }

    public TypedBooleanParameter(final boolean value) {
        this.value = value;
    }

    public TypedBooleanParameter(final byte value) {
        this.value = ((value) != 0);
    }

    public TypedBooleanParameter(final String field, final boolean value) {
        this.field = field;
        this.value = value;
    }

    public int getType() {
        return TYPED_PARAM_BOOLEAN;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_BOOLEAN";
    }

    public String getValueAsString() {
        return Boolean.toString(value);
    }
}
