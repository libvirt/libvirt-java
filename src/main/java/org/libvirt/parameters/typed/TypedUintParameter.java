package org.libvirt.parameters.typed;

/**
 * Class for representing an unsigned int typed parameter
 */
public final class TypedUintParameter extends TypedParameter {
    private int value;

    public TypedUintParameter() {
    }

    public TypedUintParameter(int value, String field) {
        this.setField(field);
        this.setValue(value);
    }

    public int getType() {
        return 2;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_UINT";
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
