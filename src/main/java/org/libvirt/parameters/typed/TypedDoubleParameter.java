package org.libvirt.parameters.typed;

/**
 * Class for representing a double typed parameter
 */
public final class TypedDoubleParameter extends TypedParameter {
    private double value;

    public TypedDoubleParameter() {
    }

    public TypedDoubleParameter(double value, String field) {
        this.setField(field);
        this.setValue(value);
    }

    public int getType() {
        return 5;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_DOUBLE";
    }

    public double getValue() {
        return value;
    }

    public void setValue(final double value) {
        this.value = value;
    }

    public String getValueAsString() {
        return Double.toString(value);
    }
}
