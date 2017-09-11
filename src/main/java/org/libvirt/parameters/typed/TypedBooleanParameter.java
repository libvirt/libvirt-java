package org.libvirt.parameters.typed;

/**
 * Class for representing a boolean typed parameter
 */
public final class TypedBooleanParameter extends TypedParameter {
    private boolean value;

    public TypedBooleanParameter() {
    }

    public TypedBooleanParameter(boolean value, String field) {
        this.setField(field);
        this.setValue(value);
    }

    public TypedBooleanParameter(byte value, String field) {
        this.setField(field);
        this.setValue((value) != 0);
    }

    public int getType() {
        return 6;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_BOOLEAN";
    }

    public boolean getValue() {
        return value;
    }

    public void setValue(final boolean value) {
        this.value = value;
    }

    public String getValueAsString() {
        return Boolean.toString(value);
    }
}
