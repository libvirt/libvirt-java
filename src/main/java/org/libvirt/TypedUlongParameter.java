package org.libvirt;

/**
 * Class for representing an ulong typed parameter
 */
public class TypedUlongParameter extends TypedParameter {
    public long value;

    public TypedUlongParameter() {

    }

    public TypedUlongParameter(final long value) {
        this.value = value;
    }

    public TypedUlongParameter(final String field, final long value) {
        this.field = field;
        this.value = value;
    }

    public int getType() {
        return TYPED_PARAM_ULONG;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_ULLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }
}
