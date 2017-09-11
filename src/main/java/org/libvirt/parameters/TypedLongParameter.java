package org.libvirt.parameters;

/**
 * Class for representing a long typed parameter
 */
public final class TypedLongParameter extends TypedParameter {
    public long value;

    public TypedLongParameter() {
    }

    public TypedLongParameter(long value) {
        this.value = value;
    }

    public int getType() {
        return 3;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_LLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }

}
