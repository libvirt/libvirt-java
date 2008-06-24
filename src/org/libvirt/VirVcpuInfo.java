package org.libvirt;

public class VirVcpuInfo {
	public int number;
	public VirVcpuState state;
	public long cpuTime;
	public int cpu;
	
	public static enum VirVcpuState {
		VIR_VCPU_OFFLINE,
		VIR_VCPU_RUNNING, 
		VIR_VCPU_BLOCKED};
}
