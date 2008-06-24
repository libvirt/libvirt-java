package org.libvirt;

public class VirNodeInfo {
	/**
	 * string indicating the CPU model
	 */
	public String model;
	/**
	 * memory size in kilobytes
	 */
	public long memory;
	/**
	 * the number of active CPUs
	 */
	public int cpus;
	/**
	 * expected CPU frequency
	 */
	public int mhz;
	/**
	 * the number of NUMA cell, 1 for uniform
	 */
	public int nodes;
	/**
	 * number of CPU socket per node
	 */
	public int sockets;
	/**
	 * number of core per socket
	 */
	public int cores;
	/**
	 * number of threads per core
	 */
	public int threads;
	
	/**
	 * @return the total number of CPUs supported but not neccessarily active in the host.
	 */
	public int maxCpus(){
		return nodes*sockets*cores*threads;
	}
}
