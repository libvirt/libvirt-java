package org.libvirt;

/**
 * Class for representing a long int scheduler parameter
 * 
 * @author stoty
 *
 */
public final class VirSchedLongParameter extends VirSchedParameter{
	/**
	 * The parameter value
	 */
	public long value;
	
	public String getValueAsString(){
		return Long.toString(value);
	}
	
	public String getTypeAsString(){
		return "VIR_DOMAIN_SCHED_FIELD_LLONG";
	}
}
