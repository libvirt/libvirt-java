package org.libvirt;

/**
 * This object is returned by VirDomain.getInfo()
 * 
 * @author stoty
 *
 */
public class VirDomainInfo {
	/**
	 * the running state, one of virDomainFlag
	 */
	public VirDomainState state;
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
	public int	nrVirtCpu;
	/**
	 * the CPU time used in nanoseconds
	 */
	public long cpuTime;
	
	/**
	 * @author stoty
	 *
	 */
	public static enum VirDomainState {
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
	
	public String toString(){
		StringBuffer result = new StringBuffer("");
		result.append("state:" + state + "\n");
		result.append("maxMem:" + maxMem + "\n");
		result.append("memory:" + memory + "\n");
		result.append("nrVirtCpu:" + nrVirtCpu + "\n");
		result.append("cpuTime:" + cpuTime + "\n");
		return result.toString();
	}
}
