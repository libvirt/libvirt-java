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

	public String getValueAsString(){
		return Integer.toString(value);
	}

	public String getTypeAsString(){
		return "VIR_DOMAIN_SCHED_FIELD_UINT";
	}
}
