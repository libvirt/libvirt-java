package org.libvirt;

/**
 * Class for representing an unsigned long int scheduler parameter
 *
 * @author stoty
 *
 */
public final class SchedUlongParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public long value;

    public SchedUlongParameter() {

    }

    public SchedUlongParameter(final long value) {
        this.value = value;
    }

    public int getType() {
        return SCHED_PARAM_ULONG;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_ULLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }
}
