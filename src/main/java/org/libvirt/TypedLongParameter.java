package org.libvirt;

/**
 * Class for representing a long typed parameter
 */
public class TypedLongParameter extends TypedParameter {
    public long value;

    public TypedLongParameter() {

    }

    public TypedLongParameter(final long value) {
        this.value = value;
    }

    public TypedLongParameter(final String field, final long value) {
        this.field = field;
        this.value = value;
    }

    public int getType() {
        return TYPED_PARAM_LONG;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_LLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }
}
