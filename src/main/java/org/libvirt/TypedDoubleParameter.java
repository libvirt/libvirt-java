package org.libvirt;

/**
 * Class for representing a double typed parameter
 */
public class TypedDoubleParameter extends TypedParameter {
    public double value;

    public TypedDoubleParameter() {

    }

    public TypedDoubleParameter(final double value) {
        this.value = value;
    }

    public TypedDoubleParameter(final String field, final double value) {
        this.field = field;
        this.value = value;
    }

    public int getType() {
        return TYPED_PARAM_DOUBLE;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_DOUBLE";
    }

    public String getValueAsString() {
        return Double.toString(value);
    }
}
