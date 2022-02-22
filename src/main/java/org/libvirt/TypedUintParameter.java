package org.libvirt;

/**
 * Class for representing a uint typed parameter
 */
public class TypedUintParameter extends TypedParameter {
    public int value;

    public TypedUintParameter() {

    }

    public TypedUintParameter(final int value) {
        this.value = value;
    }

    public TypedUintParameter(final String field, final int value) {
        this.field = field;
        this.value = value;
    }

    public int getType() {
        return TYPED_PARAM_UINT;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_UINT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
