package org.libvirt.parameters.typed;

/**
 * Class for representing a long typed parameter
 */
public final class TypedLongParameter extends TypedParameter {
    private long value;

    public TypedLongParameter() {
    }

    public TypedLongParameter(long value, String field) {
        this.setField(field);
        this.setValue(value);
    }

    public int getType() {
        return 3;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_LLONG";
    }

    public long getValue() {
        return value;
    }

    public void setValue(final long value) {
        this.value = value;
    }

    public String getValueAsString() {
        return Long.toString(value);
    }
}
