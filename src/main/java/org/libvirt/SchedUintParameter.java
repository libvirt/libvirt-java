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

    public SchedUintParameter(final int value) {
        this.value = value;
    }

    public int getType() {
        return SCHED_PARAM_UINT;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_UINT";
    }

    public String getValueAsString() {
        return Integer.toString(value);
    }
}
