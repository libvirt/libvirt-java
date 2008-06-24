package org.libvirt;

/**
 * Class for representing a boolean scheduler parameter
 * 
 * @author stoty
 *
 */
public final class VirSchedBooleanParameter  extends VirSchedParameter{
	/**
	 * The parameter value
	 */
	public boolean value;
	
	public String getValueAsString(){
		return Boolean.toString(value);
	}
	
	public String getTypeAsString(){
		return "VIR_DOMAIN_SCHED_FIELD_BOOLEAN";
	}
}
