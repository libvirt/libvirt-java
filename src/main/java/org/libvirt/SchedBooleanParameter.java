package org.libvirt;

/**
 * Class for representing a boolean scheduler parameter
 *
 * @author stoty
 *
 */
public final class SchedBooleanParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public boolean value;

    public SchedBooleanParameter() {

    }

    public SchedBooleanParameter(final boolean value) {
        this.value = value;
    }

    public SchedBooleanParameter(final byte value) {
        this.value = ((value) != 0);
    }

    public int getType() {
        return SCHED_PARAM_BOOLEAN;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_BOOLEAN";
    }

    public String getValueAsString() {
        return Boolean.toString(value);
    }
}
