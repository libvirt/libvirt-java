package org.libvirt.parameters;

/**
 * Class for representing an unsigned long int typed parameter
 */
public final class TypedUlongParameter extends TypedParameter {
    public long value;

    public TypedUlongParameter() {
    }

    public TypedUlongParameter(long value) {
        this.value = value;
    }

    public int getType() {
        return 4;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_ULLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }
}
