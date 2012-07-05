package org.libvirt;

/**
 * Class for representing a long int scheduler parameter
 *
 * @author stoty
 *
 */
public final class SchedLongParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public long value;

    public SchedLongParameter() {

    }

    public SchedLongParameter(long value) {
        this.value = value;
    }

    public int getType() {
        return 3;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_LLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }

}
