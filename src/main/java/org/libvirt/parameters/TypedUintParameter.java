package org.libvirt.parameters;

/**
 * Class for representing an unsigned int typed parameter
 */
public final class TypedUintParameter extends TypedParameter {
    public int value;

    public TypedUintParameter() {
    }

    public TypedUintParameter(int value) {
        this.value = value;
    }

    public int getType() {
        return 2;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_UINT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
