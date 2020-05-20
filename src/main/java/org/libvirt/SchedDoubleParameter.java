package org.libvirt;

/**
 * Class for representing a double scheduler parameter
 *
 * @author stoty
 *
 */
public final class SchedDoubleParameter extends SchedParameter {
    /**
     * The parameter value
     */
    public double value;

    public SchedDoubleParameter() {

    }

    public SchedDoubleParameter(final double value) {
        this.value = value;
    }

    public int getType() {
        return SCHED_PARAM_DOUBLE;
    }

    public String getTypeAsString() {
        return "VIR_DOMAIN_SCHED_FIELD_DOUBLE";
    }

    public String getValueAsString() {
        return Double.toString(value);
    }
}
