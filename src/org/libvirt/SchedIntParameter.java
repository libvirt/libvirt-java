package org.libvirt;

public final class SchedIntParameter extends SchedParameter {
	public int value;

	public String getValueAsString(){
		return Integer.toString(value);
	}

	public String getTypeAsString(){
		return "VIR_DOMAIN_SCHED_FIELD_INT";
	}
}
