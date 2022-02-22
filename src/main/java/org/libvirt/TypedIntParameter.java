package org.libvirt;

/**
 * Class for representing an int typed parameter
 */
public class TypedIntParameter extends TypedParameter {
    public int value;

    public TypedIntParameter() {

    }

    public TypedIntParameter(final int value) {
        this.value = value;
    }

    public TypedIntParameter(final String field, final int value) {
        this.field = field;
        this.value = value;
    }

    public int getType() { return TYPED_PARAM_INT; }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_INT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
