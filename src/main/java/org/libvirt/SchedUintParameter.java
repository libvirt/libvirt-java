package org.libvirt;

/**
 * Class for representing an unsigned int scheduler parameter
 *
 *
 * @author stoty
 *
 */
public final class SchedUintParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public int value;

    public SchedUintParameter() {

    }

    public SchedUintParameter(int value) {
        this.value = value;
    }

    public int getType() {
        return 2;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_UINT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
