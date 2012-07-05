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

    public SchedBooleanParameter(boolean value) {
        this.value = value;
    }

    public SchedBooleanParameter(byte value) {
        this.value = ((value) != 0) ? true : false;
    }

    public int getType() {
        return 6;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_BOOLEAN";
    }

    public String getValueAsString() {
        return Boolean.toString(value);
    }
}
