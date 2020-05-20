package org.libvirt;

import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.libvirt.event.IOErrorListener;
import org.libvirt.jna.CString;
import org.libvirt.jna.CStringByReference;
import org.libvirt.jna.DomainPointer;
import org.libvirt.jna.DomainSnapshotPointer;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.SizeT;
import org.libvirt.jna.virDomainBlockInfo;
import org.libvirt.jna.virDomainBlockStats;
import org.libvirt.jna.virDomainInfo;
import org.libvirt.jna.virDomainInterfaceStats;
import org.libvirt.jna.virDomainJobInfo;
import org.libvirt.jna.virDomainMemoryStats;
import org.libvirt.jna.virSchedParameter;
import org.libvirt.jna.virVcpuInfo;
import org.libvirt.event.RebootListener;
import org.libvirt.event.LifecycleListener;
import org.libvirt.event.PMWakeupListener;
import org.libvirt.event.PMSuspendListener;
import static org.libvirt.Library.libvirt;
import static org.libvirt.Library.libvirtQemu;
import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.ErrorHandler.processErrorIfZero;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;

/**
 * A virtual machine defined within libvirt.
 */
public class Domain {

    private static int bit(final int i) {
        return 1 << i;
    }

    /**
     * TODO: get generated constants from libvirt
     */
    public static final class BlockResizeFlags {
        /**
         * size is in bytes instead of KiB
         */
        public static final int BYTES = 1;
    }

    static final class CreateFlags {
        static final int VIR_DOMAIN_NONE = 0;
        /** Restore or alter metadata */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_REDEFINE    = bit(0);

        /** With redefine, make snapshot current */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_CURRENT     = bit(1);

        /** Make snapshot without remembering it */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_NO_METADATA = bit(2);

        /** Stop running guest after snapshot */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_HALT        = bit(3);

        /** disk snapshot, not system checkpoint */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_DISK_ONLY   = bit(4);

        /** reuse any existing external files */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_REUSE_EXT   = bit(5);

        /** use guest agent to quiesce all mounted file systems within the domain */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_QUIESCE     = bit(6);

        /* atomically avoid partial changes */
        static final int VIR_DOMAIN_SNAPSHOT_CREATE_ATOMIC      = bit(7);
    }

    static final class MigrateFlags {
        /** live migration */
        static final int VIR_MIGRATE_LIVE              = bit(0);

        /** direct source -> dest host control channel */
        static final int VIR_MIGRATE_PEER2PEER         = bit(1);

        /** tunnel migration data over libvirtd connection
         * @apiNote Note the less-common spelling that we're stuck with:
         *  VIR_MIGRATE_TUNNELLED should be VIR_MIGRATE_TUNNELED
         */
        static final int VIR_MIGRATE_TUNNELLED         = bit(2);

        /** persist the VM on the destination */
        static final int VIR_MIGRATE_PERSIST_DEST      = bit(3);

        /** undefine the VM on the source */
        static final int VIR_MIGRATE_UNDEFINE_SOURCE   = bit(4);

        /** pause on remote side */
        static final int VIR_MIGRATE_PAUSED            = bit(5);

        /** migration with non-shared storage with full disk copy */
        static final int VIR_MIGRATE_NON_SHARED_DISK   = bit(6);

        /** migration with non-shared storage with incremental copy
         * (same base image shared between source and destination)
         */
        static final int VIR_MIGRATE_NON_SHARED_INC    = bit(7);

        /** protect for changing domain configuration through the
         * whole migration process; this will be used automatically
         * when supported
         */
        static final int VIR_MIGRATE_CHANGE_PROTECTION = bit(8);

        /** force migration even if it is considered unsafe */
        static final int VIR_MIGRATE_UNSAFE            = bit(9);
    }

    static final class XMLFlags {
        /** dump security sensitive information too */
        static final int VIR_DOMAIN_XML_SECURE = 1;

        /** dump inactive domain information*/
        static final int VIR_DOMAIN_XML_INACTIVE = 2;

        /** update guest CPU requirements according to host CPU */
        static final int VIR_DOMAIN_XML_UPDATE_CPU   = bit(2);
    }

    public static final class UndefineFlags {
        /** Also remove any managed save */
        public static final int MANAGED_SAVE = bit(0);

        /** If last use of domain, then also remove any snapshot metadata */
        public static final int SNAPSHOTS_METADATA = bit(1);
    }

    public static final class SnapshotListFlags {
        /** Filter by snapshots with no parents, when listing a domain */
        public static final int ROOTS       = bit(0);

        /** List all descendants, not just children, when listing a snapshot */

        public static final int DESCENDANTS = bit(0);

        /** @apiNote For historical reasons, groups do not use contiguous bits. */

        /** Filter by snapshots with no children */
        public static final int LEAVES      = bit(2);

        /** Filter by snapshots that have children */
        public static final int NO_LEAVES   = bit(3);

        /** Filter by snapshots which have metadata */
        public static final int METADATA    = bit(1);

        /** Filter by snapshots with no metadata */
        public static final int NO_METADATA = bit(4);
    }

    /** the native virDomainPtr. */
    DomainPointer vdp;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
            + ((virConnect == null) ? 0 : virConnect.hashCode());
        try {
            result = prime * result + ((vdp == null) ? 0 : Arrays.hashCode(this.getUUID()));
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error testing domain equality", e);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Domain)) {
            return false;
        }

        Domain other = (Domain) obj;

        // return false when this domain belongs to
        // a different hypervisor than the other
        if (!this.virConnect.equals(other.virConnect)) {
            return false;
        }

        if (vdp == null) {
            return other.vdp == null;
        }

        if (vdp.equals(other.vdp)) {
            return true;
        }

        try {
            return Arrays.equals(getUUID(), other.getUUID());
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error testing domain equality", e);
        }
    }

    /** The Connect Object that represents the Hypervisor of this Domain */
    private final Connect virConnect;

    /**
     * Constructs a Domain object from a known native DomainPointer, and a
     * Connect object.
     *
     * @param virConnect
     *            the Domain's hypervisor
     * @param vdp
     *            the native virDomainPtr
     */
    Domain(final Connect virConnect, final DomainPointer vdp) {
        assert virConnect != null;

        this.virConnect = virConnect;
        this.vdp = vdp;
    }

    /**
     * Constructs a new Domain object increasing the reference count
     * on the DomainPointer.
     * <p>
     * This factory method is mostly useful with callback functions,
     * since the virDomainPtr passed is only valid for the duration of
     * execution of the callback.
     */
    static Domain constructIncRef(final Connect virConnect, final DomainPointer vdp)
            throws LibvirtException {
        processError(libvirt.virDomainRef(vdp));

        return new Domain(virConnect, vdp);
    }

    /**
     * Requests that the current background job be aborted at the soonest
     * opportunity. This will block until the job has either completed, or
     * aborted.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainAbortJob">Libvirt
     *      Documentation</a>
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int abortJob() throws LibvirtException {
        return processError(libvirt.virDomainAbortJob(vdp));
    }

    /**
     * Creates a virtual device attachment to backend.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainAttachDevice">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     *            XML description of one device
     * @throws LibvirtException
     */
    public void attachDevice(final String xmlDesc) throws LibvirtException {
        processError(libvirt.virDomainAttachDevice(vdp, xmlDesc));
    }

    /**
     * Creates a virtual device attachment to backend.
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainAttachDeviceFlags">
            Libvirt Documentation</a>
     * @param xmlDesc
     *            XML description of one device
     * @param flags
     *            the an OR'ed set of virDomainDeviceModifyFlags
     * @throws LibvirtException
     */
    public void attachDeviceFlags(final String xmlDesc, final int flags)
            throws LibvirtException {
        processError(libvirt.virDomainAttachDeviceFlags(vdp, xmlDesc, flags));
    }

    /**
     * This function returns block device (disk) stats for block devices
     * attached to the domain.
     *
     * @param path
     *            the path to the block device
     * @return the info
     * @throws LibvirtException
     */
    public DomainBlockInfo blockInfo(final String path) throws LibvirtException {
        virDomainBlockInfo info = new virDomainBlockInfo();
        processError(libvirt.virDomainGetBlockInfo(vdp, path, info, 0));
        return new DomainBlockInfo(info);
    }

    /**
     * Read the contents of a domain's disk device.
     * <p>
     * Typical uses for this are to determine if the domain has
     * written a Master Boot Record (indicating that the domain has
     * completed installation), or to try to work out the state of the
     * domain's filesystems.
     * <p>
     * (Note that in the local case you might try to open the block
     * device or file directly, but that won't work in the remote
     * case, nor if you don't have sufficient permission. Hence the
     * need for this call).
     * <p>
     * The disk parameter can either be an unambiguous source name of
     * the block device (the {@code <source file='...'/>} sub-element,
     * such as "/path/to/image"), or <em>(since 0.9.5)</em> the device
     * target shorthand (the {@code <target dev='...'/>} sub-element,
     * such as "xvda").
     * <p>
     * Valid names can be found by calling {@link #getXMLDesc} and
     * inspecting elements within {@code //domain/devices/disk}.
     * <p>
     * This method always reads the number of bytes remaining in the
     * buffer, that is, {@code buffer.remaining()} at the moment this
     * method is invoked. Upon return the buffer's position will be
     * equal to the limit, the limit itself will not have changed.
     *
     * @param  disk    the path to the block device, or device shorthand
     * @param  offset  the offset within block device
     * @param  buffer  the buffer receiving the data
     */
    public void blockPeek(final String disk, final long offset,
                          final ByteBuffer buffer) throws LibvirtException {
        SizeT size = new SizeT();

        // older libvirt has a limitation on the size of data
        // transferred per request in the remote driver. So, split
        // larger requests into 64K blocks.

        do {
            final int req = Math.min(65536, buffer.remaining());

            size.setValue(req);

            processError(libvirt.virDomainBlockPeek(this.vdp, disk, offset, size, buffer, 0));

            buffer.position(buffer.position() + req);
        } while (buffer.hasRemaining());

        assert buffer.position() == buffer.limit();
    }

    /**
     * Returns block device (disk) stats for block devices attached to this
     * domain. The path parameter is the name of the block device. Get this by
     * calling virDomainGetXMLDesc and finding the {@code <target dev='...'>}
     * attribute within //domain/devices/disk. (For example, "xvda"). Domains
     * may have more than one block device. To get stats for each you should
     * make multiple calls to this function. Individual fields within the
     * DomainBlockStats object may be returned as -1, which indicates that the
     * hypervisor does not support that particular statistic.
     *
     * @param path
     *            path to the block device
     * @return the statistics in a DomainBlockStats object
     * @throws LibvirtException
     */
    public DomainBlockStats blockStats(final String path) throws LibvirtException {
        virDomainBlockStats stats = new virDomainBlockStats();
        processError(libvirt.virDomainBlockStats(vdp, path, stats, new SizeT(stats.size())));
        return new DomainBlockStats(stats);
    }

    /**
     * Resize a block device of domain while the domain is running.
     *
     * @param disk
     *           path to the block image, or shorthand (like vda)
     * @param size
     *           the new size of the block devices
     * @param flags
     *           bitwise OR'ed values of {@link BlockResizeFlags}
     * @throws LibvirtException
     */
    public void blockResize(final String disk, final long size, final int flags)
            throws LibvirtException {
        processError(libvirt.virDomainBlockResize(vdp, disk, size, flags));
    }


    /**
     * Dumps the core of this domain on a given file for analysis. Note that for
     * remote Xen Daemon the file path will be interpreted in the remote host.
     *
     * @param to
     *            path for the core file
     * @param flags
     *            extra flags, currently unused
     * @throws LibvirtException
     */
    public void coreDump(final String to, final int flags) throws LibvirtException {
        processError(libvirt.virDomainCoreDump(vdp, to, flags));
    }

    /**
     * It returns the length (in bytes) required to store the complete CPU map
     * between a single virtual & all physical CPUs of a domain.
     */
    public int cpuMapLength(final int maxCpus) {
        return (maxCpus + 7) / 8;
    }

    /**
     * Launches this defined domain. If the call succeed the domain moves from
     * the defined to the running domains pools.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int create() throws LibvirtException {
        return processError(libvirt.virDomainCreate(vdp));
    }

    /**
     * Launches this defined domain with the provide flags.
     * If the call succeed the domain moves from
     * the defined to the running domains pools.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int create(final int flags) throws LibvirtException {
        return processError(libvirt.virDomainCreateWithFlags(vdp, flags));
    }

    /**
     * Destroys this domain object. The running instance is shutdown if not down
     * already and all resources used by it are given back to the hypervisor.
     * The data structure is freed and should not be used thereafter if the call
     * does not return an error. This function may requires priviledged access
     *
     * @throws LibvirtException
     */
    public void destroy() throws LibvirtException {
        processError(libvirt.virDomainDestroy(vdp));
    }

    /**
     * Destroys a virtual device attachment to backend.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainDetachDevice">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     *            XML description of one device
     * @throws LibvirtException
     */
    public void detachDevice(final String xmlDesc) throws LibvirtException {
        processError(libvirt.virDomainDetachDevice(vdp, xmlDesc));
    }

    /**
     * Destroys a virtual device attachment to backend.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainDetachDeviceFlags">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     *            XML description of one device
     * @throws LibvirtException
     */
    public void detachDeviceFlags(final String xmlDesc, final int flags)
            throws LibvirtException {
        processError(libvirt.virDomainDetachDeviceFlags(vdp, xmlDesc, flags));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this domain object. The running instance is kept alive. The data
     * structure is freed and should not be used thereafter.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vdp != null) {
            success = processError(libvirt.virDomainFree(vdp));
            vdp = null;
        }

        return success;
    }

    /**
     * Provides a boolean value indicating whether the domain is configured to
     * be automatically started when the host machine boots.
     *
     * @return the result
     * @throws LibvirtException
     */
    public boolean getAutostart() throws LibvirtException {
        IntByReference autoStart = new IntByReference();
        processError(libvirt.virDomainGetAutostart(vdp, autoStart));
        return autoStart.getValue() != 0 ? true : false;
    }

    /**
     * Provides the connection object associated with a domain.
     *
     * @return the Connect object
     */
    public Connect getConnect() {
        return virConnect;
    }

    /**
     * Gets the hypervisor ID number for the domain
     *
     * @return the hypervisor ID
     * @throws LibvirtException
     */
    public int getID() throws LibvirtException {
        return processError(libvirt.virDomainGetID(vdp));
    }

    /**
     * Extract information about a domain. Note that if the connection used to
     * get the domain is limited only a partial set of the information can be
     * extracted.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainGetInfo">Libvirt
     *      Documentation</a>
     *
     * @return a DomainInfo object describing this domain
     * @throws LibvirtException
     */
    public DomainInfo getInfo() throws LibvirtException {
        virDomainInfo vInfo = new virDomainInfo();
        processError(libvirt.virDomainGetInfo(vdp, vInfo));
        return new DomainInfo(vInfo);
    }

    /**
     * Extract information about progress of a background job on a domain. Will
     * return an error if the domain is not active.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainGetJobInfo">Libvirt
     *      Documentation</a>
     * @return a DomainJobInfo object describing this domain
     * @throws LibvirtException
     */
    public DomainJobInfo getJobInfo() throws LibvirtException {
        virDomainJobInfo vInfo = new virDomainJobInfo();
        processError(libvirt.virDomainGetJobInfo(vdp, vInfo));
        return new DomainJobInfo(vInfo);
    }

    /**
     * Retrieve the maximum amount of physical memory allocated to a domain.
     *
     * @return the memory in kilobytes
     * @throws LibvirtException
     */
    public long getMaxMemory() throws LibvirtException {
        // the memory size in kibibytes (blocks of 1024 bytes), or 0 in case of error.
        NativeLong returnValue = libvirt.virDomainGetMaxMemory(vdp);
        return processErrorIfZero(returnValue.longValue());
    }

    /**
     * Provides the maximum number of virtual CPUs supported for the guest VM.
     * If the guest is inactive, this is basically the same as
     * virConnectGetMaxVcpus. If the guest is running this will reflect the
     * maximum number of virtual CPUs the guest was booted with.
     *
     * @return the number of VCPUs
     * @throws LibvirtException
     */
    public int getMaxVcpus() throws LibvirtException {
        return processError(libvirt.virDomainGetMaxVcpus(vdp));
    }

    /**
     * Gets the public name for this domain
     *
     * @return the name, null if there is no name
     * @throws LibvirtException <em>never</em>
     */
    public String getName() throws LibvirtException {
        return libvirt.virDomainGetName(vdp);
    }

    /**
     * Gets the type of domain operation system.
     *
     * @return the type
     * @throws LibvirtException
     */
    public String getOSType() throws LibvirtException {
        return processError(libvirt.virDomainGetOSType(vdp)).toString();
    }

    /**
     * Gets the scheduler parameters.
     *
     * @return an array of SchedParameter objects
     * @throws LibvirtException
     */
    public SchedParameter[] getSchedulerParameters() throws LibvirtException {
        IntByReference nParams = new IntByReference();
        processError(libvirt.virDomainGetSchedulerType(vdp, nParams));

        int n = nParams.getValue();

        if (n > 0) {
            virSchedParameter[] nativeParams = new virSchedParameter[n];

            processError(libvirt.virDomainGetSchedulerParameters(vdp, nativeParams, nParams));
            n = nParams.getValue();

            SchedParameter[] returnValue = new SchedParameter[n];

            for (int x = 0; x < n; x++) {
                returnValue[x] = SchedParameter.create(nativeParams[x]);
            }
            return returnValue;
        } else {
            return new SchedParameter[] {};
        }
    }

    // getSchedulerType
    // We don't expose the nparams return value, it's only needed for the
    // SchedulerParameters allocations,
    // but we handle that in getSchedulerParameters internally.
    /**
     * Gets the scheduler type.
     *
     * @return the type of the scheduler
     * @throws LibvirtException
     */
    public String getSchedulerType() throws LibvirtException {
        return processError(libvirt.virDomainGetSchedulerType(vdp, null)).toString();
    }

    /**
     * Get the security label of an active domain.
     *
     * @return the SecurityLabel or {@code null} if the domain is not
     *         running under a security model
     * @throws LibvirtException
     */
    public SecurityLabel getSecurityLabel() throws LibvirtException {
        Libvirt.SecurityLabel seclabel = new Libvirt.SecurityLabel();

        processError(libvirt.virDomainGetSecurityLabel(this.vdp, seclabel));

        if (seclabel.label[0] == 0) {
            return null;
        } else {
            return new SecurityLabel(seclabel);
        }
    }

    /**
     * Get the UUID for this domain.
     *
     * @return the UUID as an unpacked int array
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public int[] getUUID() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        processError(libvirt.virDomainGetUUID(vdp, bytes));
        return Connect.convertUUIDBytes(bytes);
    }

    /**
     * Gets the UUID for this domain as string.
     *
     * @return the UUID in canonical String format
     * @throws LibvirtException
     * @see <a href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>
     */
    public String getUUIDString() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_STRING_BUFLEN];
        processError(libvirt.virDomainGetUUIDString(vdp, bytes));
        return Native.toString(bytes);
    }

    /**
     * Returns the cpumaps for this domain Only the lower 8 bits of each int in
     * the array contain information.
     *
     * @return a bitmap of real CPUs for all vcpus of this domain
     * @throws LibvirtException
     */
    public int[] getVcpusCpuMaps() throws LibvirtException {
        int[] returnValue = new int[0];
        int cpuCount = getMaxVcpus();

        if (cpuCount > 0) {
            NodeInfo nodeInfo = virConnect.nodeInfo();
            int maplength = cpuMapLength(nodeInfo.maxCpus());
            virVcpuInfo[] infos = new virVcpuInfo[cpuCount];
            returnValue = new int[cpuCount * maplength];
            byte[] cpumaps = new byte[cpuCount * maplength];
            processError(libvirt.virDomainGetVcpus(vdp, infos, cpuCount, cpumaps, maplength));
            for (int x = 0; x < cpuCount * maplength; x++) {
                returnValue[x] = cpumaps[x];
            }
        }
        return returnValue;
    }

    /**
     * Extracts information about virtual CPUs of this domain
     *
     * @return an array of VcpuInfo object describing the VCPUs
     * @throws LibvirtException
     */
    public VcpuInfo[] getVcpusInfo() throws LibvirtException {
        int cpuCount = getMaxVcpus();
        VcpuInfo[] returnValue = new VcpuInfo[cpuCount];
        virVcpuInfo[] infos = new virVcpuInfo[cpuCount];
        processError(libvirt.virDomainGetVcpus(vdp, infos, cpuCount, null, 0));
        for (int x = 0; x < cpuCount; x++) {
            returnValue[x] = new VcpuInfo(infos[x]);
        }
        return returnValue;
    }

    /**
     * Provides an XML description of the domain. The description may be reused
     * later to relaunch the domain with createLinux().
     *
     * @param flags
     *            not used
     * @return the XML description String
     * @throws LibvirtException
     * @see <a href="http://libvirt.org/format.html#Normal1" >The XML
     *      Description format </a>
     */
    public String getXMLDesc(final int flags) throws LibvirtException {
        return processError(libvirt.virDomainGetXMLDesc(vdp, flags)).toString();
    }

    /**
     * Determine if the domain has a snapshot
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainHasCurrentSnapshot>Libvi
     *      r t Documentation</a>
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int hasCurrentSnapshot() throws LibvirtException {
        return processError(libvirt.virDomainHasCurrentSnapshot(vdp, 0));
    }

    /**
     * Determine if the domain has a managed save image
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainHasManagedSaveImage>Libvi
     *      r t Documentation</a>
     * @return 0 if no image is present, 1 if an image is present, and -1 in
     *         case of error
     * @throws LibvirtException
     */
    public int hasManagedSaveImage() throws LibvirtException {
        return processError(libvirt.virDomainHasManagedSaveImage(vdp, 0));
    }

    /**
     * Returns network interface stats for interfaces attached to this domain.
     * The path parameter is the name of the network interface. Domains may have
     * more than network interface. To get stats for each you should make
     * multiple calls to this function. Individual fields within the
     * DomainInterfaceStats object may be returned as -1, which indicates that
     * the hypervisor does not support that particular statistic.
     *
     * @param path
     *            path to the interface
     * @return the statistics in a DomainInterfaceStats object
     * @throws LibvirtException
     */
    public DomainInterfaceStats interfaceStats(final String path)
            throws LibvirtException {
        virDomainInterfaceStats stats = new virDomainInterfaceStats();
        processError(libvirt.virDomainInterfaceStats(vdp, path, stats, new SizeT(stats.size())));
        return new DomainInterfaceStats(stats);
    }

    /**
     * Determine if the domain is currently running
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainIsActive">Libvirt
     *      Documentation</a>
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        return processError(libvirt.virDomainIsActive(vdp));
    }

    /**
     * Determine if the domain has a persistent configuration which means it
     * will still exist after shutting down
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainIsPersistent">Libvirt
     *      Documentation</a>
     * @return 1 if persistent, 0 if transient
     * @throws LibvirtException
     */
    public int isPersistent() throws LibvirtException {
        return processError(libvirt.virDomainIsPersistent(vdp));
    }


    /**
     * Returns {@code true} if, and only if, this domain has been updated.
     */
    public boolean isUpdated() throws LibvirtException {
        return processError(libvirt.virDomainIsUpdated(this.vdp)) == 1;
    }

    /**
     * suspend a domain and save its memory contents to a file on disk.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainManagedSave">Libvirt
     *      Documentation</a>
     * @return always 0
     * @throws LibvirtException
     */
    public int managedSave() throws LibvirtException {
        return processError(libvirt.virDomainManagedSave(vdp, 0));
    }

    /**
     * Remove any managed save images from the domain
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainManagedSaveRemove">Libvirt
     *      Documentation</a>
     * @return always 0
     * @throws LibvirtException
     */
    public int managedSaveRemove() throws LibvirtException {
        return processError(libvirt.virDomainManagedSaveRemove(vdp, 0));
    }

    /**
     * Read the contents of a domain's memory.
     * <p>
     * If mode is MemoryAddressMode.VIRTUAL the 'start' parameter is
     * interpreted as virtual memory address for whichever task
     * happens to be running on the domain at the moment. Although
     * this sounds haphazard it is in fact what you want in order to
     * read Linux kernel state, because it ensures that pointers in
     * the kernel image can be interpreted coherently.
     * <p>
     * This method always reads the number of bytes remaining in the
     * buffer, that is, {@code buffer.remaining()} at the moment this
     * method is invoked. Upon return the buffer's position will be
     * equal to the limit, the limit itself will not have changed.
     *
     * @param start  the start address of the memory to peek
     * @param mode   the mode which determines whether the given addresses
     *               are interpreted as virtual or physical addresses
     */
    public void memoryPeek(final long start, final ByteBuffer buffer,
                           final MemoryAddressMode mode)
            throws LibvirtException {
        SizeT size = new SizeT();

        // older libvirt has a limitation on the size of data
        // transferred per request in the remote driver. So, split
        // larger requests into 64K blocks.

        do {
            final int req = Math.min(65536, buffer.remaining());

            size.setValue(req);

            processError(libvirt.virDomainMemoryPeek(this.vdp, start, size, buffer, mode.getValue()));

            buffer.position(buffer.position() + req);
        } while (buffer.hasRemaining());

        assert buffer.position() == buffer.limit();
    }

    /**
     * This function provides memory statistics for the domain.
     *
     * @param number
     *            the number of stats to retrieve
     * @return the collection of stats
     * @throws LibvirtException
     */
    public MemoryStatistic[] memoryStats(final int number)
            throws LibvirtException {
        virDomainMemoryStats[] stats = new virDomainMemoryStats[number];
        MemoryStatistic[] returnStats = null;
        int result = processError(libvirt.virDomainMemoryStats(vdp, stats, number, 0));
        returnStats = new MemoryStatistic[result];
        for (int x = 0; x < result; x++) {
            returnStats[x] = new MemoryStatistic(stats[x]);
        }
        return returnStats;
    }

    /**
     * Migrate this domain object from its current host to the destination host
     * given by dconn (a connection to the destination host).
     * <p>
     * Flags may be bitwise OR'ed values of
     * {@link org.libvirt.Domain.MigrateFlags MigrateFlags}.
     * <p>
     * If a hypervisor supports renaming domains during migration, then you may
     * set the dname parameter to the new name (otherwise it keeps the same name).
     * <p>
     * If this is not supported by the hypervisor, dname must be {@code null} or
     * else you will get an exception.
     * <p>
     * Since typically the two hypervisors connect directly to each other in order
     * to perform the migration, you may need to specify a path from the source
     * to the destination. This is the purpose of the uri parameter.
     * <p>
     * If uri is {@code null}, then libvirt will try to find the best method.
     * <p>
     * Uri may specify the hostname or IP address of the destination host as seen
     * from the source, or uri may be a URI giving transport, hostname, user,
     * port, etc. in the usual form.
     * <p>
     * Uri should only be specified if you want to migrate over a specific interface
     * on the remote host.
     * <p>
     * For Qemu/KVM, the URI should be of the form {@code "tcp://hostname[:port]"}.
     * <p>
     * This does not require TCP auth to be setup between the connections, since
     * migrate uses a straight TCP connection (unless using the PEER2PEER flag,
     * in which case URI should be a full fledged libvirt URI).
     * <p>
     * Refer also to driver documentation for the particular URIs supported.
     * <p>
     * The maximum bandwidth (in Mbps) that will be used to do
     * migration can be specified with the bandwidth parameter. If
     * set to 0, libvirt will choose a suitable default.
     * <p>
     * Some hypervisors do not support this feature and will return an
     * error if bandwidth is not 0. To see which features are
     * supported by the current hypervisor, see
     * Connect.getCapabilities, /capabilities/host/migration_features.
     * <p>
     * There are many limitations on migration imposed by the underlying technology
     * for example it may not be possible to migrate between different processors
     * even with the same architecture, or between different types of hypervisor.
     * <p>
     * If the hypervisor supports it, dxml can be used to alter
     * host-specific portions of the domain XML that will be used on
     * the destination.
     *
     * @param dconn
     *            destination host (a Connect object)
     * @param dxml
     *            (optional) XML config for launching guest on target
     * @param flags
     *            flags
     * @param dname
     *            (optional) rename domain to this at destination
     * @param uri
     *            (optional) dest hostname/URI as seen from the source host
     * @param bandwidth
     *            (optional) specify migration bandwidth limit in Mbps
     * @return the new domain object if the migration was
     *         successful. Note that the new domain object exists in
     *         the scope of the destination connection (dconn).
     * @throws LibvirtException if the migration fails
     */
    public Domain migrate(final Connect dconn, final long flags,
                          final String dxml, final String dname,
                          final String uri, final long bandwidth)
            throws LibvirtException {
        DomainPointer newPtr =
            processError(libvirt.virDomainMigrate2(vdp, dconn.vcp, dxml,
                    new NativeLong(flags), dname, uri, new NativeLong(bandwidth)));
        return new Domain(dconn, newPtr);
    }

    /**
     * Migrate this domain object from its current host to the destination host
     * given by dconn (a connection to the destination host). Flags may be one
     * of more of the following: Domain.VIR_MIGRATE_LIVE Attempt a live
     * migration. If a hypervisor supports renaming domains during migration,
     * then you may set the dname parameter to the new name (otherwise it keeps
     * the same name). If this is not supported by the hypervisor, dname must be
     * NULL or else you will get an error. Since typically the two hypervisors
     * connect directly to each other in order to perform the migration, you may
     * need to specify a path from the source to the destination. This is the
     * purpose of the uri parameter.If uri is NULL, then libvirt will try to
     * find the best method. Uri may specify the hostname or IP address of the
     * destination host as seen from the source, or uri may be a URI giving
     * transport, hostname, user, port, etc. in the usual form. Uri should only
     * be specified if you want to migrate over a specific interface on the
     * remote host. For Qemu/KVM, the uri should be of the form
     * "tcp://hostname[:port]". This does not require TCP auth to be setup
     * between the connections, since migrate uses a straight TCP connection
     * (unless using the PEER2PEER flag, in which case URI should be a full
     * fledged libvirt URI). Refer also to driver documentation for the
     * particular URIs supported. If set to 0, libvirt will choose a suitable
     * default. Some hypervisors do not support this feature and will return an
     * error if bandwidth is not 0. To see which features are supported by the
     * current hypervisor, see Connect.getCapabilities,
     * /capabilities/host/migration_features. There are many limitations on
     * migration imposed by the underlying technology - for example it may not
     * be possible to migrate between different processors even with the same
     * architecture, or between different types of hypervisor.
     *
     * @param dconn
     *            destination host (a Connect object)
     * @param flags
     *            flags
     * @param dname
     *            (optional) rename domain to this at destination
     * @param uri
     *            (optional) dest hostname/URI as seen from the source host
     * @param bandwidth
     *            optional) specify migration bandwidth limit in Mbps
     * @return the new domain object if the migration was successful. Note that
     *         the new domain object exists in the scope of the destination
     *         connection (dconn).
     * @throws LibvirtException
     */
    public Domain migrate(final Connect dconn, final long flags,
                          final String dname, final String uri,
                          final long bandwidth)
            throws LibvirtException {
        DomainPointer newPtr = processError(libvirt.virDomainMigrate(vdp, dconn.vcp,
                new NativeLong(flags), dname, uri, new NativeLong(bandwidth)));
        return new Domain(dconn, newPtr);
    }

    /**
     * Sets maximum tolerable time for which the domain is allowed to be paused
     * at the end of live migration.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainMigrateSetMaxDowntime">LIbvirt
     *      Documentation</a>
     * @param downtime
     *            the time to be down
     * @return always 0
     * @throws LibvirtException
     */
    public int migrateSetMaxDowntime(final long downtime)
            throws LibvirtException {
        return processError(libvirt.virDomainMigrateSetMaxDowntime(vdp, downtime, 0));
    }

    /**
     * Migrate the domain object from its current host to the destination
     * denoted by a given URI.
     * <p>
     * The destination is given either in dconnuri (if the
     * {@link MigrateFlags#VIR_MIGRATE_PEER2PEER PEER2PEER}
     * is flag set), or in miguri (if neither the
     * {@link MigrateFlags#VIR_MIGRATE_PEER2PEER PEER2PEER} nor the
     * {@link MigrateFlags#VIR_MIGRATE_TUNNELLED TUNNELLED} migration
     * flag is set in flags).
     *
     * @see <a
     * href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainMigrateToURI">
     * virDomainMigrateToURI</a>
     *
     * @param dconnuri
     *            (optional) URI for target libvirtd if @flags includes VIR_MIGRATE_PEER2PEER
     * @param miguri
     *            (optional) URI for invoking the migration, not if @flags includs VIR_MIGRATE_TUNNELLED
     * @param dxml
     *            (optional) XML config for launching guest on target
     * @param flags
     *            Controls the migrate
     * @param dname
     *            The name at the destnation
     * @param bandwidth
     *            Specify the migration bandwidth
     * @return 0 if successful
     * @throws LibvirtException
     */
    public int migrateToURI(final String dconnuri, final String miguri,
                            final String dxml, final long flags,
                            final String dname, final long bandwidth)
            throws LibvirtException {
        return processError(libvirt.virDomainMigrateToURI2(vdp, dconnuri, miguri,
                                                           dxml, new NativeLong(flags),
                                                           dname, new NativeLong(bandwidth)));
    }

    /**
     * Migrate the domain object from its current host to the destination host
     * given by duri.
     *
     * @see <a
     *       href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainMigrateToURI">
     *       virDomainMigrateToURI</a>
     *
     * @param uri
     *            The destination URI
     * @param flags
     *            Controls the migrate
     * @param dname
     *            The name at the destnation
     * @param bandwidth
     *            Specify the migration bandwidth
     * @return 0 if successful, -1 if not
     * @throws LibvirtException
     */
    public int migrateToURI(final String uri, final long flags,
                            final String dname, final long bandwidth)
            throws LibvirtException {
        return processError(libvirt.virDomainMigrateToURI(vdp, uri,
                new NativeLong(flags), dname, new NativeLong(bandwidth)));
    }

    /**
     * Enter the given power management suspension target level.
     */
    public void PMsuspend(final SuspendTarget target) throws LibvirtException {
        PMsuspendFor(target, 0, TimeUnit.SECONDS);
    }

    /**
     * Enter the given power management suspension target level for the given duration.
     */
    public void PMsuspendFor(final SuspendTarget target, final long duration,
            final TimeUnit unit)
            throws LibvirtException {
        processError(libvirt.virDomainPMSuspendForDuration(this.vdp,
                target.ordinal(), unit.toSeconds(duration), 0));
    }

    /**
     * Immediately wake up a guest using power management.
     * <p>
     * Injects a <em>wakeup<em> into the guest that previously used
     * {@link #PMsuspend} or {@link #PMsuspendFor}, rather than
     * waiting for the previously requested duration (if any) to
     * elapse.
     */
    public void PMwakeup() throws LibvirtException {
        processError(libvirt.virDomainPMWakeup(this.vdp, 0));
    }

    /**
     * Dynamically changes the real CPUs which can be allocated to a virtual
     * CPU. This function requires priviledged access to the hypervisor.
     *
     * @param vcpu
     *            virtual cpu number
     * @param cpumap
     *            bit map of real CPUs represented by the the lower 8 bits of
     *            each int in the array. Each bit set to 1 means that
     *            corresponding CPU is usable. Bytes are stored in little-endian
     *            order: CPU0-7, 8-15... In each byte, lowest CPU number is
     *            least significant bit.
     * @throws LibvirtException
     */
    public void pinVcpu(final int vcpu, final int[] cpumap)
            throws LibvirtException {
        byte[] packedMap = new byte[cpumap.length];
        for (int x = 0; x < cpumap.length; x++) {
            packedMap[x] = (byte) cpumap[x];
        }
        processError(libvirt.virDomainPinVcpu(vdp, vcpu, packedMap, cpumap.length));
    }

    /**
     * Reboot this domain, the domain object is still usable there after but the
     * domain OS is being stopped for a restart. Note that the guest OS may
     * ignore the request.
     *
     * @param flags
     *            extra flags for the reboot operation, not used yet
     * @throws LibvirtException
     */
    public void reboot(final int flags) throws LibvirtException {
        processError(libvirt.virDomainReboot(vdp, flags));
    }

    /**
     * Resume this suspended domain, the process is restarted from the state
     * where it was frozen by calling virSuspendDomain(). This function may
     * requires privileged access
     *
     * @throws LibvirtException
     */
    public void resume() throws LibvirtException {
        processError(libvirt.virDomainResume(vdp));
    }

    /**
     * Adds a callback to receive notifications of IOError domain events
     * occurring on this domain.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny">Libvirt
     *      Documentation</a>
     * @param cb
     *            the IOErrorCallback instance
     * @throws LibvirtException on failure
     */
    public void addIOErrorListener(final IOErrorListener cb) throws LibvirtException {
        virConnect.domainEventRegister(this, cb);
    }

    /**
     * Adds the specified listener to receive reboot events for this domain.
     *
     * @param  l   the reboot listener
     * @throws     LibvirtException on failure
     *
     * @see Connect#addRebootListener
     * @see <a
     *       href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
     *      >virConnectDomainEventRegisterAny</a>
     * @since 1.5.2
     */
    public void addRebootListener(final RebootListener l)
            throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive lifecycle events for this domain.
     *
     * @param  l   the lifecycle listener
     * @throws LibvirtException on failure
     *
     * @see Connect#addLifecycleListener
     * @see Connect#removeLifecycleListener
     * @see <a
     *       href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
     *      >virConnectDomainEventRegisterAny</a>
     */
    public void addLifecycleListener(final LifecycleListener l)
            throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive PMWakeup events for this domain.
     *
     * @param  l  the PMWakeup listener
     * @throws    LibvirtException on failure
     *
     * @see Connect#removePMWakeupListener
     * @see Connect#addPMWakeupListener
     * @see <a
     *       href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
     *      >virConnectDomainEventRegisterAny</a>
     *
     * @since 1.5.2
     */
    public void addPMWakeupListener(final PMWakeupListener l)
            throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive PMSuspend events for this domain.
     *
     * @param  l  the PMSuspend listener
     * @throws    LibvirtException on failure
     *
     * @see Connect#removePMSuspendListener
     * @see Connect#addPMSuspendListener
     * @see <a
     *       href="http://www.libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
     *      >virConnectDomainEventRegisterAny</a>
     *
     * @since 1.5.2
     */
    public void addPMSuspendListener(final PMSuspendListener l)
            throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Reset a domain immediately without any guest OS shutdown.
     */
    public void reset() throws LibvirtException {
        processError(libvirt.virDomainReset(this.vdp, 0));
    }

    /**
     * Revert the domain to a given snapshot.
     *
     * @see <a href=
     *      "http://www.libvirt.org/html/libvirt-libvirt.html#virDomainRevertToSnapshot"
     *      >Libvirt Documentation</>
     * @param snapshot
     *            the snapshot to revert to
     * @return 0 if the creation is successful
     * @throws LibvirtException
     */
    public int revertToSnapshot(final DomainSnapshot snapshot)
            throws LibvirtException {
        return processError(libvirt.virDomainRevertToSnapshot(snapshot.vdsp, 0));
    }

    /**
     * Suspends this domain and saves its memory contents to a file on disk.
     * After the call, if successful, the domain is not listed as running
     * anymore (this may be a problem). Use Connect.virDomainRestore() to
     * restore a domain after saving.
     *
     * @param to
     *            path for the output file
     * @throws LibvirtException
     */
    public void save(final String to) throws LibvirtException {
        processError(libvirt.virDomainSave(vdp, to));
    }

    public String screenshot(final Stream stream, final int screen)
            throws LibvirtException {
        CString mimeType = libvirt.virDomainScreenshot(this.vdp, stream.getVsp(), screen, 0);
        processError(mimeType);
        stream.markReadable();
        return mimeType.toString();
    }

    /**
     * Configures the network to be automatically started when the host machine
     * boots.
     *
     * @param autostart
     * @throws LibvirtException
     */
    public void setAutostart(final boolean autostart) throws LibvirtException {
        int autoValue = autostart ? 1 : 0;
        processError(libvirt.virDomainSetAutostart(vdp, autoValue));
    }

    /**
     * * Dynamically change the maximum amount of physical memory allocated to a
     * domain. This function requires priviledged access to the hypervisor.
     *
     * @param memory
     *            the amount memory in kilobytes
     * @throws LibvirtException
     */
    public void setMaxMemory(final long memory) throws LibvirtException {
        processError(libvirt.virDomainSetMaxMemory(vdp, new NativeLong(memory)));
    }

    /**
     * Dynamically changes the target amount of physical memory allocated to
     * this domain. This function may requires priviledged access to the
     * hypervisor.
     *
     * @param memory
     *            in kilobytes
     * @throws LibvirtException
     */
    public void setMemory(final long memory) throws LibvirtException {
        processError(libvirt.virDomainSetMemory(vdp, new NativeLong(memory)));
    }

    /**
     * Changes the scheduler parameters
     *
     * @param params
     *            an array of SchedParameter objects to be changed
     * @throws LibvirtException
     */
    public void setSchedulerParameters(final SchedParameter[] params)
            throws LibvirtException {
        virSchedParameter[] input = new virSchedParameter[params.length];
        for (int x = 0; x < params.length; x++) {
            input[x] = SchedParameter.toNative(params[x]);
        }
        processError(libvirt.virDomainSetSchedulerParameters(vdp, input, params.length));
    }

    /**
     * Dynamically changes the number of virtual CPUs used by this domain. Note
     * that this call may fail if the underlying virtualization hypervisor does
     * not support it or if growing the number is arbitrary limited. This
     * function requires priviledged access to the hypervisor.
     *
     * @param nvcpus
     *            the new number of virtual CPUs for this domain
     * @throws LibvirtException
     */
    public void setVcpus(final int nvcpus) throws LibvirtException {
        processError(libvirt.virDomainSetVcpus(vdp, nvcpus));
    }

    /**
     * Send key(s) to the guest.
     *
     * @param  codeset  the set of keycodes
     * @param  holdtime the duration that the keys will be held (in milliseconds)
     * @param  keys     the key codes to be send
     */
    public void sendKey(final KeycodeSet codeset, final int holdtime,
                        final int... keys)
            throws LibvirtException {
        processError(libvirt.virDomainSendKey(this.vdp, codeset.ordinal(),
                                              holdtime, keys, keys.length, 0));
    }

    /**
     * Shuts down this domain, the domain object is still usable there after but
     * the domain OS is being stopped. Note that the guest OS may ignore the
     * request. TODO: should we add an option for reboot, knowing it may not be
     * doable in the general case ?
     *
     * @throws LibvirtException
     */
    public void shutdown() throws LibvirtException {
        processError(libvirt.virDomainShutdown(vdp));
    }

    /**
     * Creates a new snapshot of a domain based on the snapshot xml contained in
     * xmlDesc.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotCreateXML">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     *            string containing an XML description of the domain
     * @param flags
     *            flags for creating the snapshot, see the virDomainSnapshotCreateFlags for the flag options
     * @return the snapshot
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotCreateXML(final String xmlDesc, final int flags)
            throws LibvirtException {
        DomainSnapshotPointer ptr = processError(libvirt.virDomainSnapshotCreateXML(vdp, xmlDesc, flags));
        return new DomainSnapshot(virConnect, ptr);
    }

    /**
     * Creates a new snapshot of a domain based on the snapshot xml contained in
     * xmlDesc.
     * <p>
     * This is just a convenience method, it has the same effect
     * as calling {@code snapshotCreateXML(xmlDesc, 0);}.
     *
     * @see #snapshotCreateXML(String, int)
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotCreateXML">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     *            string containing an XML description of the domain
     * @return the snapshot, or null on Error
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotCreateXML(final String xmlDesc)
            throws LibvirtException {
        return snapshotCreateXML(xmlDesc, 0);
    }

    /**
     * Get the current snapshot for a domain, if any.
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotCurrent">Libvirt
     *      Documentation</a>
     * @return the snapshot
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotCurrent() throws LibvirtException {
        DomainSnapshotPointer ptr = processError(libvirt.virDomainSnapshotCurrent(vdp, 0));
        return new DomainSnapshot(virConnect, ptr);
    }

    /**
     * Collect the list of domain snapshots for the given domain. With the option to pass flags
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotListNames">Libvirt
     *      Documentation</a>
     * @return The list of names, or null if an error
     * @throws LibvirtException
     */
    public String[] snapshotListNames(final int flags) throws LibvirtException {
        int num = snapshotNum();
        if (num > 0) {
            CString[] names = new CString[num];
            int got = processError(libvirt.virDomainSnapshotListNames(vdp, names, num, flags));

            return Library.toStringArray(names, got);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Collect the list of domain snapshots for the given domain.
     * <p>
     * This is just a convenience method, it has the same effect
     * as calling {@code snapshotListNames(0);}.
     *
     * @see #snapshotListNames(int)
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotListNames">
     *        virDomainSnapshotListNames</a>
     * @return The list of names, or null if an error
     * @throws LibvirtException
     */
    public String[] snapshotListNames() throws LibvirtException {
        return snapshotListNames(0);
    }

    /**
     * Retrieve a snapshot by name
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotLookupByName">Libvirt
     *      Documentation</a>
     * @param name
     *            the name
     * @return The located snapshot
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotLookupByName(final String name)
            throws LibvirtException {
        DomainSnapshotPointer ptr = processError(libvirt.virDomainSnapshotLookupByName(vdp, name, 0));
        return new DomainSnapshot(virConnect, ptr);
    }

    /**
     * Provides the number of domain snapshots for this domain..
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotNum">Libvirt
     *      Documentation</a>
     */
    public int snapshotNum() throws LibvirtException {
        return processError(libvirt.virDomainSnapshotNum(vdp, 0));
    }

    /**
     * Suspends this active domain, the process is frozen without further access
     * to CPU resources and I/O but the memory used by the domain at the
     * hypervisor level will stay allocated. Use Domain.resume() to reactivate
     * the domain. This function requires priviledged access.
     *
     * @throws LibvirtException
     */
    public void suspend() throws LibvirtException {
        processError(libvirt.virDomainSuspend(vdp));
    }

    /**
     * undefines this domain but does not stop it if it is running
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        processError(libvirt.virDomainUndefine(vdp));
    }

    /**
     * Undefines this domain but does not stop if it it is running.
     * With option for passing flags
     *
     * @see <a href="http://libvirt.org/html/libvirt-libvirt.html#virDomainUndefineFlags">
        Libvirt Documentation</a>
     * @param flags
     *            flags for undefining the domain.
     *            See virDomainUndefineFlagsValues for more information
     * @throws LibvirtException
    */
    public void undefine(final int flags) throws LibvirtException {
        processError(libvirt.virDomainUndefineFlags(vdp, flags));
    }

    /**
     * Change a virtual device on a domain
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainUpdateDeviceFlags">
        Libvirt Documentation</a>
     * @param xml
     *            the xml to update with
     * @param flags
     *            controls the update
     * @return always 0
     * @throws LibvirtException
     */
    public int updateDeviceFlags(final String xml, final int flags)
            throws LibvirtException {
        return processError(libvirt.virDomainUpdateDeviceFlags(vdp, xml, flags));
    }

    /**
     * Commands for  Qemu Guest Agent helper daemon
     *
     *@see <a
     *      href="https://www.libvirt.org/html/libvirt-libvirt-qemu.html#virDomainQemuAgentCommand">Libvirt
     *      Documentation</a>
     * @param cmd
     *        the guest agent command string
     * @param timeout
     *        timeout seconds
     * @param flags
     *        execution flags
     * @return result
     *         strings if success, NULL in failure.
     * @throws LibvirtException
     */
    public String qemuAgentCommand(String cmd, int timeout, int flags) throws LibvirtException {
        CString result = libvirtQemu != null ? libvirtQemu.virDomainQemuAgentCommand(vdp, cmd, timeout, flags) : null;
        processError(result);
        return result.toString();
    }

    /**
     * Qemu Monitor Command - it will only work with hypervisor connections to the QEMU driver.
     *
     *@see <a
     *      href="https://www.libvirt.org/html/libvirt-libvirt-qemu.html#virDomainQemuMonitorCommand">Libvirt
     *      Documentation</a>
     * @param cmd
     *        the qemu monitor command string
     * @param flags
     *        bitwise-or of supported virDomainQemuMonitorCommandFlags
     * @return result
     *         a string returned by @cmd
     * @throws LibvirtException
     */
    public String qemuMonitorCommand(String cmd, int flags) throws LibvirtException {
        CStringByReference result = new CStringByReference();
        int cmdResult = libvirtQemu != null ? libvirtQemu.virDomainQemuMonitorCommand(vdp, cmd, result, flags) : -1;
        processError(cmdResult);
        return result.getValue().toString();
    }
}
