package org.libvirt;

/**
 * Class for representing a string (char pointer) typed parameter
 */
public final class TypedStringParameter extends TypedParameter {
    public String value;

    public TypedStringParameter() {

    }

    public TypedStringParameter(String value) {
        this.value = value;
    }

    public TypedStringParameter(final String field, final String value) {
        this.field = field;
        this.value = value;
    }

    public int getType() {
        return TYPED_PARAM_STRING;
    }

    public String getTypeAsString() {
        return "VIR_TYPED_PARAM_STRING";
    }

    public String getValueAsString() {
        return String.valueOf(value);
    }
}
