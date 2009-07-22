package org.libvirt;

/**
 * Parameter to hold an int.
 */
public final class SchedIntParameter extends SchedParameter {
    public int value;

    public SchedIntParameter() {

    }

    public SchedIntParameter(int value) {
        this.value = value;
    }

    public int getType() {
        return 1;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_INT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
