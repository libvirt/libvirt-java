package org.libvirt;

import org.libvirt.jna.virVcpuInfo;

/**
 * Stats about a cpu.
 */
public class VcpuInfo {
    public enum VcpuState {
        VIR_VCPU_OFFLINE, VIR_VCPU_RUNNING, VIR_VCPU_BLOCKED
    }

    public int number;
    public VcpuState state;
    public long cpuTime;

    public int cpu;;

    public VcpuInfo() {

    }

    public VcpuInfo(final virVcpuInfo vVcpu) {
        number = vVcpu.number;
        cpuTime = vVcpu.cpuTime;
        cpu = vVcpu.cpu;
        state = VcpuState.values()[vVcpu.state];
    }

    @Override
    public String toString() {
        return String.format("number:%d%ncpuTime:%d%ncpu:%d%nstate:%s%n", number, cpuTime, cpu, state);
    }
}
