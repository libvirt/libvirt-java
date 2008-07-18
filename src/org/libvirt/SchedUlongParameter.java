package org.libvirt;

/**
 * Class for representing an unsigned long int scheduler parameter
 *
 * @author stoty
 *
 */
public final class SchedUlongParameter extends SchedParameter{
	/**
	 * The parameter value
	 */
	public long value;

	public String getValueAsString(){
		return Long.toString(value);
	}

	public String getTypeAsString(){
		return "VIR_DOMAIN_SCHED_FIELD_ULLONG";
	}
}
