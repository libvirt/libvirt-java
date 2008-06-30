package org.libvirt;

/**
 * The abstract parent of the actual VirSchedparameter classes
 * 
 * @author stoty
 *
 */
public abstract class VirSchedParameter {
	
	/**
	 * Parameter name
	 */
	public String field;
	
	/**
	 * Utility function for displaying the value
	 * 
	 * @return the value of the parameter in String form
	 */
	public abstract String getValueAsString();
	/**
	 * Utility function for displaying the type
	 * 
	 * @return the Type of the parameter as string
	 */
	public abstract String getTypeAsString();
	
}
