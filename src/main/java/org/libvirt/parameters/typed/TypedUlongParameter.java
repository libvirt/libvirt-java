package org.libvirt.parameters.typed;

/**
 * Class for representing an unsigned long int typed parameter
 */
public final class TypedUlongParameter extends TypedParameter {
    private long value;

    public TypedUlongParameter() {
    }

    public TypedUlongParameter(long value, String field) {
        this.setField(field);
        this.setValue(value);
    }

    public int getType() {
        return 4;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_ULLONG";
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
