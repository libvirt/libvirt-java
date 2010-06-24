package org.libvirt;

import org.libvirt.jna.virDomainInfo;

/**
 * This object is returned by Domain.getInfo()
 * 
 * @author stoty
 * 
 */
public class DomainInfo {
    /**
     * @author stoty
     * 
     */
    public static enum DomainState {
        /**
         * no state
         */
        VIR_DOMAIN_NOSTATE,
        /**
         * the domain is running
         */
        VIR_DOMAIN_RUNNING,
        /**
         * the domain is blocked on resource
         */
        VIR_DOMAIN_BLOCKED,
        /**
         * the domain is paused by user
         */
        VIR_DOMAIN_PAUSED,
        /**
         * the domain is being shut down
         */
        VIR_DOMAIN_SHUTDOWN,
        /**
         * the domain is shut off
         */
        VIR_DOMAIN_SHUTOFF,
        /**
         * the domain is crashed
         */
        VIR_DOMAIN_CRASHED
    }

    /**
     * the running state, one of virDomainFlag
     */
    public DomainState state;
    /**
     * the maximum memory in KBytes allowed
     */
    public long maxMem;
    /**
     * the memory in KBytes used by the domain
     */
    public long memory;
    /**
     * the number of virtual CPUs for the domain
     */
    public int nrVirtCpu;

    /**
     * the CPU time used in nanoseconds
     */
    public long cpuTime;

    public DomainInfo() {

    }

    public DomainInfo(virDomainInfo info) {
        cpuTime = info.cpuTime;
        maxMem = info.maxMem.longValue();
        memory = info.memory.longValue();
        nrVirtCpu = info.nrVirtCpu;
        state = DomainState.values()[info.state];
    }

    @Override
    public String toString() {
        return String.format("state:%s%nmaxMem:%d%nmemory:%d%nnrVirtCpu:%d%ncpuTime:%d%n", state, maxMem, memory, nrVirtCpu, cpuTime);
    }
}
