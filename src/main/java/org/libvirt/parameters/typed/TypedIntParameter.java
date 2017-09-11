package org.libvirt.parameters.typed;

/**
 * Class for representing a int typed parameter
 */
public final class TypedIntParameter extends TypedParameter {
    private int value;

    public TypedIntParameter() {
    }

    public TypedIntParameter(int value, String field) {
        this.setField(field);
        this.setValue(value);
    }

    public int getType() {
        return 1;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_INT";
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
