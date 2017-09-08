package org.libvirt;

import com.sun.jna.Native;
import org.libvirt.jna.virNodeInfo;

/**
 * Node data from libvirt.
 */
public class NodeInfo {
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

    public NodeInfo() {
    }

    public NodeInfo(virNodeInfo vInfo) {
        model = Native.toString(vInfo.model);
        memory = vInfo.memory.longValue();
        cpus = vInfo.cpus;
        mhz = vInfo.mhz;
        nodes = vInfo.nodes;
        sockets = vInfo.sockets;
        cores = vInfo.cores;
        threads = vInfo.threads;
    }

    /**
     * @return the total number of CPUs supported but not necessarily active in
     *         the host.
     */
    public int maxCpus() {
        return nodes * sockets * cores * threads;
    }

    @Override
    public String toString() {
        return String.format("model:%s%nmemory:%d%ncpus:%d%nmhz:%d%nnodes:%d%nsockets:%d%ncores:%d%nthreads:%d%n", model, memory, cpus, mhz, nodes, sockets, cores, threads);
    }
}
