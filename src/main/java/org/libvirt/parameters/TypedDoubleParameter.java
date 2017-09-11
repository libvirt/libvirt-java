package org.libvirt.parameters;

/**
 * Class for representing a double typed parameter
 */
public final class TypedDoubleParameter extends TypedParameter {
    public double value;

    public TypedDoubleParameter() {
    }

    public TypedDoubleParameter(double value) {
        this.value = value;
    }

    public int getType() {
        return 5;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_DOUBLE";
    }

    public String getValueAsString() {
        return Double.toString(value);
    }
}
