package org.libvirt;

public class VcpuInfo {
	public int number;
	public VcpuState state;
	public long cpuTime;
	public int cpu;

	public static enum VcpuState {
		VIR_VCPU_OFFLINE,
		VIR_VCPU_RUNNING,
		VIR_VCPU_BLOCKED};
}
