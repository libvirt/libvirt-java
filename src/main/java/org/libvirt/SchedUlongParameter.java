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

    public SchedUlongParameter(long value) {
        this.value = value;
    }

    public int getType() {
        return 4;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_ULLONG";
    }

    public String getValueAsString() {
        return Long.toString(value);
    }
}
