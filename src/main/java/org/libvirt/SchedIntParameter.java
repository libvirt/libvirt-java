package org.libvirt;

/**
 * Parameter to hold an int.
 */
public final class SchedIntParameter extends SchedParameter {
    public int value;

    public SchedIntParameter() {

    }

    public SchedIntParameter(final int value) {
        this.value = value;
    }

    public int getType() {
        return SCHED_PARAM_INT;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_INT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
