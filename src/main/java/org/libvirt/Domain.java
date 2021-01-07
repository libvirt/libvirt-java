package org.libvirt;

import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.ErrorHandler.processErrorIfZero;
import static org.libvirt.Library.libvirt;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;
import org.libvirt.event.BlockJobListener;
import org.libvirt.event.IOErrorListener;
import org.libvirt.event.LifecycleListener;
import org.libvirt.event.PMSuspendListener;
import org.libvirt.event.PMWakeupListener;
import org.libvirt.event.RebootListener;
import org.libvirt.flags.DomainBlockResizeFlags;
import org.libvirt.flags.DomainDeviceModifyFlags;
import org.libvirt.flags.DomainMetadataFlags;
import org.libvirt.flags.DomainMigrateFlags;
import org.libvirt.flags.DomainSnapshotListFlags;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.pointers.DomainPointer;
import org.libvirt.jna.pointers.DomainSnapshotPointer;
import org.libvirt.jna.structures.virDomainBlockInfo;
import org.libvirt.jna.structures.virDomainBlockJobInfo;
import org.libvirt.jna.structures.virDomainBlockStats;
import org.libvirt.jna.structures.virDomainInfo;
import org.libvirt.jna.structures.virDomainInterfaceStats;
import org.libvirt.jna.structures.virDomainJobInfo;
import org.libvirt.jna.structures.virDomainMemoryStats;
import org.libvirt.jna.structures.virSecurityLabel;
import org.libvirt.jna.structures.virTypedParameter;
import org.libvirt.jna.structures.virVcpuInfo;
import org.libvirt.jna.types.CString;
import org.libvirt.jna.types.SizeT;
import org.libvirt.parameters.DomainBlockCopyParameters;
import org.libvirt.parameters.typed.TypedParameter;

/**
 * A virtual machine defined within libvirt.
 */
public class Domain {

    /**
     * the native virDomainPtr.
     */
    DomainPointer VDP;

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
            result = prime * result + ((VDP == null) ? 0 : Arrays.hashCode(this.getUUID()));
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error testing domain equality", e);
        }
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
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

        if (VDP == null) {
            return (other.VDP == null);
        }

        if (VDP.equals(other.VDP)) {
            return true;
        }

        try {
            return Arrays.equals(getUUID(), other.getUUID());
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error testing domain equality", e);
        }
    }

    /**
     * The Connect Object that represents the Hypervisor of this Domain
     */
    private final Connect virConnect;

    /**
     * Constructs a Domain object from a known native DomainPointer, and a
     * Connect object.
     *
     * @param virConnect the Domain's hypervisor
     * @param VDP        the native virDomainPtr
     */
    Domain(Connect virConnect, DomainPointer VDP) {
        assert virConnect != null;

        this.virConnect = virConnect;
        this.VDP = VDP;
    }

    /**
     * Constructs a new Domain object increasing the reference count
     * on the DomainPointer.
     * <p>
     * This factory method is mostly useful with callback functions,
     * since the virDomainPtr passed is only valid for the duration of
     * execution of the callback.
     */
    static Domain constructIncRef(Connect virConnect, DomainPointer VDP) throws LibvirtException {
        processError(libvirt.virDomainRef(VDP));

        return new Domain(virConnect, VDP);
    }

    /**
     * Requests that the current background job be aborted at the soonest
     * opportunity. This will block until the job has either completed, or
     * aborted.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int abortJob() throws LibvirtException {
        return processError(libvirt.virDomainAbortJob(VDP));
    }

    /**
     * Creates a virtual device attachment to backend.
     *
     * @param xmlDesc XML description of one device
     * @throws LibvirtException
     */
    public void attachDevice(String xmlDesc) throws LibvirtException {
        processError(libvirt.virDomainAttachDevice(VDP, xmlDesc));
    }

    /**
     * Creates a virtual device attachment to backend.
     *
     * @param xmlDesc XML description of one device
     * @param flags   the an OR'ed set of virDomainDeviceModifyFlags
     * @throws LibvirtException
     */
    public void attachDeviceFlags(String xmlDesc, int flags) throws LibvirtException {
        processError(libvirt.virDomainAttachDeviceFlags(VDP, xmlDesc, flags));
    }

    /**
     * Copy the guest-visible contents of a disk image to a new file described by @destxml.
     *
     * @param disk    the path to the block device
     * @param destxml the new disk description
     * @param params  {@link virTypedParameter}
     * @param flags   {@link org.libvirt.flags.DomainBlockCopyFlags}
     * @throws LibvirtException
     */
    public void blockCopy(String disk, String destxml, TypedParameter[] params, int flags) throws LibvirtException {
        virTypedParameter[] input = new virTypedParameter[params.length];
        for (int x = 0; x < params.length; x++) {
            input[x] = TypedParameter.toNative(params[x]);
        }
        processError(libvirt.virDomainBlockCopy(VDP, disk, destxml, input, params.length, flags));
    }

    /**
     * This function returns block device (disk) stats for block devices
     * attached to the domain.
     *
     * @param path the path to the block device
     * @return the info
     * @throws LibvirtException
     */
    public DomainBlockInfo blockInfo(String path) throws LibvirtException {
        virDomainBlockInfo info = new virDomainBlockInfo();
        processError(libvirt.virDomainGetBlockInfo(VDP, path, info, 0));
        return new DomainBlockInfo(info);
    }

    /**
     * This function returns a block job for block devices attached to the domain.
     *
     * @param path the path to the block device
     * @return the info
     * @throws LibvirtException
     */
    public DomainBlockJobInfo blockJobInfo(String path, int flags) throws LibvirtException {
        virDomainBlockJobInfo info = new virDomainBlockJobInfo();
        processError(libvirt.virDomainGetBlockJobInfo(VDP, path, info, flags));
        return new DomainBlockJobInfo(info);
    }

    /**
     * Cancel the active block job on the given disk.
     *
     * @param disk  the path to the block device
     * @param flags {@link org.libvirt.flags.DomainBlockJobAbortFlags}
     * @throws LibvirtException
     */
    public void blockJobAbort(String disk, int flags) throws LibvirtException {
        processError(libvirt.virDomainBlockJobAbort(VDP, disk, flags));
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
     * @param disk   the path to the block device, or device shorthand
     * @param offset the offset within block device
     * @param buffer the buffer receiving the data
     */
    public void blockPeek(String disk, long offset, ByteBuffer buffer) throws LibvirtException {
        SizeT size = new SizeT();

        // older libvirt has a limitation on the size of data
        // transferred per request in the remote driver. So, split
        // larger requests into 64K blocks.

        do {
            final int req = Math.min(65536, buffer.remaining());

            size.setValue(req);

            processError(libvirt.virDomainBlockPeek(this.VDP, disk, offset, size, buffer, 0));

            buffer.position(buffer.position() + req);
        } while (buffer.hasRemaining());

        assert buffer.position() == buffer.limit();
    }

    /**
     * Returns block device (disk) stats for block devices attached to this
     * domain. The path parameter is the name of the block device. Get this by
     * calling virDomainGetXMLDesc and finding the <target dev='...'> attribute
     * within //domain/devices/disk. (For example, "xvda"). Domains may have
     * more than one block device. To get stats for each you should make
     * multiple calls to this function. Individual fields within the
     * DomainBlockStats object may be returned as -1, which indicates that the
     * hypervisor does not support that particular statistic.
     *
     * @param path path to the block device
     * @return the statistics in a DomainBlockStats object
     * @throws LibvirtException
     */
    public DomainBlockStats blockStats(String path) throws LibvirtException {
        virDomainBlockStats stats = new virDomainBlockStats();
        processError(libvirt.virDomainBlockStats(VDP, path, stats, new SizeT(stats.size())));
        return new DomainBlockStats(stats);
    }

    /**
     * Resize a block device of domain while the domain is running.
     *
     * @param disk  path to the block image, or shorthand (like vda)
     * @param size  the new size of the block devices
     * @param flags bitwise OR'ed values of {@link DomainBlockResizeFlags}
     * @throws LibvirtException
     */
    public void blockResize(String disk, long size, int flags) throws LibvirtException {
        processError(libvirt.virDomainBlockResize(VDP, disk, size, flags));
    }

    /**
     * Dumps the core of this domain on a given file for analysis. Note that for
     * remote Xen Daemon the file path will be interpreted in the remote host.
     *
     * @param to    path for the core file
     * @param flags extra flags, currently unused
     * @throws LibvirtException
     */
    public void coreDump(String to, int flags) throws LibvirtException {
        processError(libvirt.virDomainCoreDump(VDP, to, flags));
    }

    /**
     * It returns the length (in bytes) required to store the complete CPU map
     * between a single virtual & all physical CPUs of a domain.
     */
    public int cpuMapLength(int maxCpus) {
        return (((maxCpus) + 7) / 8);
    }

    /**
     * Launches this defined domain. If the call succeed the domain moves from
     * the defined to the running domains pools.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int create() throws LibvirtException {
        return processError(libvirt.virDomainCreate(VDP));
    }

    /**
     * Launches this defined domain with the provide flags.
     * If the call succeed the domain moves from
     * the defined to the running domains pools.
     *
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int create(int flags) throws LibvirtException {
        return processError(libvirt.virDomainCreateWithFlags(VDP, flags));
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
        processError(libvirt.virDomainDestroy(VDP));
    }

    /**
     * Destroys a virtual device attachment to backend.
     *
     * @param xmlDesc XML description of one device
     * @throws LibvirtException
     */
    public void detachDevice(String xmlDesc) throws LibvirtException {
        processError(libvirt.virDomainDetachDevice(VDP, xmlDesc));
    }

    /**
     * Destroys a virtual device attachment to backend.
     *
     * @param xmlDesc XML description of one device
     * @param flags   the an OR'ed set of virDomainDeviceModifyFlags
     * @throws LibvirtException
     */
    public void detachDeviceFlags(String xmlDesc, int flags) throws LibvirtException {
        processError(libvirt.virDomainDetachDeviceFlags(VDP, xmlDesc, flags));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this domain object. The running instance is kept alive. The data
     * structure is freed and should not be used thereafter.
     *
     * @return number of references left (>= 0)
     * @throws LibvirtException
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VDP != null) {
            success = processError(libvirt.virDomainFree(VDP));
            VDP = null;
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
        processError(libvirt.virDomainGetAutostart(VDP, autoStart));
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
        return processError(libvirt.virDomainGetID(VDP));
    }

    /**
     * Extract information about a domain. Note that if the connection used to
     * get the domain is limited only a partial set of the information can be
     * extracted.
     *
     * @return a DomainInfo object describing this domain
     * @throws LibvirtException
     */
    public DomainInfo getInfo() throws LibvirtException {
        virDomainInfo vInfo = new virDomainInfo();
        processError(libvirt.virDomainGetInfo(VDP, vInfo));
        return new DomainInfo(vInfo);
    }

    /**
     * Extract information about progress of a background job on a domain. Will
     * return an error if the domain is not active.
     *
     * @return a DomainJobInfo object describing this domain
     * @throws LibvirtException
     */
    public DomainJobInfo getJobInfo() throws LibvirtException {
        virDomainJobInfo vInfo = new virDomainJobInfo();
        processError(libvirt.virDomainGetJobInfo(VDP, vInfo));
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
        NativeLong returnValue = libvirt.virDomainGetMaxMemory(VDP);
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
        return processError(libvirt.virDomainGetMaxVcpus(VDP));
    }

    /**
     * @type: type of metadata, from virDomainMetadataType
     * @uri: XML namespace identifier
     * @flags: bitwise-OR of virDomainModificationImpact
     *
     * Retrieves the appropriate domain element given by @type.
     * If VIR_DOMAIN_METADATA_ELEMENT is requested parameter @uri
     * must be set to the name of the namespace the requested elements
     * belong to, otherwise must be NULL.
     *
     * If an element of the domain XML is not present, the resulting
     * error will be VIR_ERR_NO_DOMAIN_METADATA.  This method forms
     * a shortcut for seeing information from virDomainSetMetadata()
     * without having to go through virDomainGetXMLDesc().
     *
     * @flags controls whether the live domain or persistent
     * configuration will be queried.
     *
     * @return the metadata string on success (caller must free),
     * or NULL in case of failure.
     * @throws LibvirtException
     */
    public String getMetadata(DomainMetadataFlags type, String uri, int flags) throws LibvirtException {
        return processError(libvirt.virDomainGetMetadata(VDP, type.getValue(), uri, flags));
    }

    /**
     * Gets the public name for this domain
     *
     * @return the name, null if there is no name
     * @throws LibvirtException <em>never</em>
     */
    public String getName() throws LibvirtException {
        return libvirt.virDomainGetName(VDP);
    }

    /**
     * Gets the type of domain operation system.
     *
     * @return the type
     * @throws LibvirtException
     */
    public String getOSType() throws LibvirtException {
        return processError(libvirt.virDomainGetOSType(VDP)).toString();
    }

    /**
     * Gets the scheduler parameters.
     *
     * @return an array of TypedParameter objects
     * @throws LibvirtException
     */
    public TypedParameter[] getSchedulerParameters() throws LibvirtException {
        IntByReference nParams = new IntByReference();
        processError(libvirt.virDomainGetSchedulerType(VDP, nParams));

        int n = nParams.getValue();

        if (n > 0) {
            virTypedParameter[] nativeParams = new virTypedParameter[n];

            processError(libvirt.virDomainGetSchedulerParameters(VDP, nativeParams, nParams));
            n = nParams.getValue();

            TypedParameter[] returnValue = new TypedParameter[n];

            for (int x = 0; x < n; x++) {
                returnValue[x] = TypedParameter.create(nativeParams[x]);
            }
            return returnValue;
        } else {
            return new TypedParameter[]{};
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
        return processError(libvirt.virDomainGetSchedulerType(VDP, null)).toString();
    }

    /**
     * Get the security label of an active domain.
     *
     * @return the SecurityLabel or {@code null} if the domain is not
     * running under a security model
     * @throws LibvirtException
     */
    public SecurityLabel getSecurityLabel() throws LibvirtException {
        virSecurityLabel seclabel = new virSecurityLabel();

        processError(libvirt.virDomainGetSecurityLabel(this.VDP, seclabel));

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
        processError(libvirt.virDomainGetUUID(VDP, bytes));
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
        processError(libvirt.virDomainGetUUIDString(VDP, bytes));
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
            processError(libvirt.virDomainGetVcpus(VDP, infos, cpuCount, cpumaps, maplength));
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
        processError(libvirt.virDomainGetVcpus(VDP, infos, cpuCount, null, 0));
        for (int x = 0; x < cpuCount; x++) {
            returnValue[x] = new VcpuInfo(infos[x]);
        }
        return returnValue;
    }

    /**
     * Provides an XML description of the domain. The description may be reused
     * later to relaunch the domain with createLinux().
     *
     * @param flags not used
     * @return the XML description String
     * @throws LibvirtException
     */
    public String getXMLDesc(int flags) throws LibvirtException {
        return processError(libvirt.virDomainGetXMLDesc(VDP, flags)).toString();
    }

    /**
     * Determine if the domain has a snapshot
     *
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int hasCurrentSnapshot() throws LibvirtException {
        return processError(libvirt.virDomainHasCurrentSnapshot(VDP, 0));
    }

    /**
     * Determine if the domain has a managed save image
     *
     * @return 0 if no image is present, 1 if an image is present, and -1 in
     * case of error
     * @throws LibvirtException
     */
    public int hasManagedSaveImage() throws LibvirtException {
        return processError(libvirt.virDomainHasManagedSaveImage(VDP, 0));
    }

    /**
     * Returns network interface stats for interfaces attached to this domain.
     * The path parameter is the name of the network interface. Domains may have
     * more than network interface. To get stats for each you should make
     * multiple calls to this function. Individual fields within the
     * DomainInterfaceStats object may be returned as -1, which indicates that
     * the hypervisor does not support that particular statistic.
     *
     * @param path path to the interface
     * @return the statistics in a DomainInterfaceStats object
     * @throws LibvirtException
     */
    public DomainInterfaceStats interfaceStats(String path) throws LibvirtException {
        virDomainInterfaceStats stats = new virDomainInterfaceStats();
        processError(libvirt.virDomainInterfaceStats(VDP, path, stats, new SizeT(stats.size())));
        return new DomainInterfaceStats(stats);
    }

    /**
     * Determine if the domain is currently running
     *
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        return processError(libvirt.virDomainIsActive(VDP));
    }

    /**
     * Determine if the domain has a persistent configuration which means it
     * will still exist after shutting down
     *
     * @return 1 if persistent, 0 if transient
     * @throws LibvirtException
     */
    public int isPersistent() throws LibvirtException {
        return processError(libvirt.virDomainIsPersistent(VDP));
    }

    /**
     * Returns {@code true} if, and only if, this domain has been updated.
     */
    public boolean isUpdated() throws LibvirtException {
        return processError(libvirt.virDomainIsUpdated(this.VDP)) == 1;
    }

    /**
     * suspend a domain and save its memory contents to a file on disk.
     *
     * @return always 0
     * @throws LibvirtException
     */
    public int managedSave() throws LibvirtException {
        return processError(libvirt.virDomainManagedSave(VDP, 0));
    }

    /**
     * Remove any managed save images from the domain
     *
     * @return always 0
     * @throws LibvirtException
     */
    public int managedSaveRemove() throws LibvirtException {
        return processError(libvirt.virDomainManagedSaveRemove(VDP, 0));
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
     * @param start the start address of the memory to peek
     * @param mode  the mode which determines whether the given addresses
     *              are interpreted as virtual or physical addresses
     */
    public void memoryPeek(long start, ByteBuffer buffer, MemoryAddressMode mode) throws LibvirtException {
        SizeT size = new SizeT();

        // older libvirt has a limitation on the size of data
        // transferred per request in the remote driver. So, split
        // larger requests into 64K blocks.

        do {
            final int req = Math.min(65536, buffer.remaining());

            size.setValue(req);

            processError(libvirt.virDomainMemoryPeek(this.VDP, start, size, buffer, mode.getValue()));

            buffer.position(buffer.position() + req);
        } while (buffer.hasRemaining());

        assert buffer.position() == buffer.limit();
    }

    /**
     * This function provides memory statistics for the domain.
     *
     * @param number the number of stats to retrieve
     * @return the collection of stats
     * @throws LibvirtException
     */
    public MemoryStatistic[] memoryStats(int number) throws LibvirtException {
        virDomainMemoryStats[] stats = new virDomainMemoryStats[number];
        MemoryStatistic[] returnStats = null;
        int result = processError(libvirt.virDomainMemoryStats(VDP, stats, number, 0));
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
     * {@link DomainMigrateFlags MigrateFlags}.
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
     * @param dconn     destination host (a Connect object)
     * @param dxml      (optional) XML config for launching guest on target
     * @param flags     flags
     * @param dname     (optional) rename domain to this at destination
     * @param uri       (optional) dest hostname/URI as seen from the source host
     * @param bandwidth (optional) specify migration bandwidth limit in Mbps
     * @return the new domain object if the migration was
     * successful. Note that the new domain object exists in
     * the scope of the destination connection (dconn).
     * @throws LibvirtException if the migration fails
     */
    public Domain migrate(Connect dconn, long flags, String dxml, String dname, String uri, long bandwidth) throws LibvirtException {
        DomainPointer newPtr = processError(libvirt.virDomainMigrate2(VDP, dconn.VCP, dxml, new NativeLong(flags), dname, uri, new NativeLong(bandwidth)));

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
     * @param dconn     destination host (a Connect object)
     * @param flags     flags
     * @param dname     (optional) rename domain to this at destination
     * @param uri       (optional) dest hostname/URI as seen from the source host
     * @param bandwidth optional) specify migration bandwidth limit in Mbps
     * @return the new domain object if the migration was successful. Note that
     * the new domain object exists in the scope of the destination
     * connection (dconn).
     * @throws LibvirtException
     */
    public Domain migrate(Connect dconn, long flags, String dname, String uri, long bandwidth) throws LibvirtException {
        DomainPointer newPtr = processError(libvirt.virDomainMigrate(VDP, dconn.VCP, new NativeLong(flags), dname, uri, new NativeLong(bandwidth)));
        return new Domain(dconn, newPtr);
    }

    /**
     * Sets maximum tolerable time for which the domain is allowed to be paused
     * at the end of live migration.
     *
     * @param downtime the time to be down
     * @return always 0
     * @throws LibvirtException
     */
    public int migrateSetMaxDowntime(long downtime) throws LibvirtException {
        return processError(libvirt.virDomainMigrateSetMaxDowntime(VDP, downtime, 0));
    }

    /**
     * Migrate the domain object from its current host to the destination
     * denoted by a given URI.
     * <p>
     * The destination is given either in dconnuri (if the
     * {@link DomainMigrateFlags#VIR_MIGRATE_PEER2PEER PEER2PEER}
     * is flag set), or in miguri (if neither the
     * {@link DomainMigrateFlags#VIR_MIGRATE_PEER2PEER PEER2PEER} nor the
     * {@link DomainMigrateFlags#VIR_MIGRATE_TUNNELLED TUNNELLED} migration
     * flag is set in flags).
     *
     * @param dconnuri  (optional) URI for target libvirtd if @flags includes VIR_MIGRATE_PEER2PEER
     * @param miguri    (optional) URI for invoking the migration, not if @flags includs VIR_MIGRATE_TUNNELLED
     * @param dxml      (optional) XML config for launching guest on target
     * @param flags     Controls the migrate
     * @param dname     The name at the destnation
     * @param bandwidth Specify the migration bandwidth
     * @return 0 if successful
     * @throws LibvirtException
     */
    public int migrateToURI(String dconnuri, String miguri, String dxml, long flags, String dname, long bandwidth) throws LibvirtException {
        return processError(libvirt.virDomainMigrateToURI2(VDP, dconnuri, miguri,
                dxml, new NativeLong(flags),
                dname, new NativeLong(bandwidth)));
    }

    /**
     * Migrate the domain object from its current host to the destination host
     * given by duri.
     *
     * @param uri       The destination URI
     * @param flags     Controls the migrate
     * @param dname     The name at the destnation
     * @param bandwidth Specify the migration bandwidth
     * @return 0 if successful, -1 if not
     * @throws LibvirtException
     */
    public int migrateToURI(String uri, long flags, String dname, long bandwidth) throws LibvirtException {
        return processError(libvirt.virDomainMigrateToURI(VDP, uri, new NativeLong(flags), dname, new NativeLong(bandwidth)));
    }

    /**
     * Enter the given power management suspension target level.
     */
    public void PMsuspend(SuspendTarget target) throws LibvirtException {
        PMsuspendFor(target, 0, TimeUnit.SECONDS);
    }

    /**
     * Enter the given power management suspension target level for the given duration.
     */
    public void PMsuspendFor(SuspendTarget target, long duration, TimeUnit unit) throws LibvirtException {
        processError(libvirt.virDomainPMSuspendForDuration(this.VDP, target.ordinal(), unit.toSeconds(duration), 0));
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
        processError(libvirt.virDomainPMWakeup(this.VDP, 0));
    }

    /**
     * Dynamically changes the real CPUs which can be allocated to a virtual
     * CPU. This function requires priviledged access to the hypervisor.
     *
     * @param vcpu   virtual cpu number
     * @param cpumap bit map of real CPUs represented by the the lower 8 bits of
     *               each int in the array. Each bit set to 1 means that
     *               corresponding CPU is usable. Bytes are stored in little-endian
     *               order: CPU0-7, 8-15... In each byte, lowest CPU number is
     *               least significant bit.
     * @throws LibvirtException
     */
    public void pinVcpu(int vcpu, int[] cpumap) throws LibvirtException {
        byte[] packedMap = new byte[cpumap.length];
        for (int x = 0; x < cpumap.length; x++) {
            packedMap[x] = (byte) cpumap[x];
        }
        processError(libvirt.virDomainPinVcpu(VDP, vcpu, packedMap, cpumap.length));
    }

    /**
     * Reboot this domain, the domain object is still usable there after but the
     * domain OS is being stopped for a restart. Note that the guest OS may
     * ignore the request.
     *
     * @param flags extra flags for the reboot operation, not used yet
     * @throws LibvirtException
     */
    public void reboot(int flags) throws LibvirtException {
        processError(libvirt.virDomainReboot(VDP, flags));
    }

    /**
     * Resume this suspended domain, the process is restarted from the state
     * where it was frozen by calling virSuspendDomain(). This function may
     * requires privileged access
     *
     * @throws LibvirtException
     */
    public void resume() throws LibvirtException {
        processError(libvirt.virDomainResume(VDP));
    }

    /**
     * Adds a callback to receive notifications of IOError domain events
     * occurring on this domain.
     *
     * @param cb the IOErrorCallback instance
     * @throws LibvirtException on failure
     */
    public void addIOErrorListener(final IOErrorListener cb) throws LibvirtException {
        virConnect.domainEventRegister(this, cb);
    }

    /**
     * Adds the specified listener to receive reboot events for this domain.
     *
     * @param l the reboot listener
     * @throws LibvirtException on failure
     * @see Connect#addRebootListener
     * @since 1.5.2
     */
    public void addRebootListener(final RebootListener l) throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive lifecycle events for this domain.
     *
     * @param l the lifecycle listener
     * @throws LibvirtException on failure
     * @see Connect#addLifecycleListener
     * @see Connect#removeLifecycleListener
     */
    public void addLifecycleListener(final LifecycleListener l) throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive blockjob events for this domain.
     *
     * @param l the blockjob listener
     * @throws LibvirtException on failure
     * @see Connect#addBlockJobListener
     * @see Connect#removeBlockJobListener
     */
    public void addBlockJobListener(final BlockJobListener l) throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive PMWakeup events for this domain.
     *
     * @param l the PMWakeup listener
     * @throws LibvirtException on failure
     * @see Connect#removePMWakeupListener
     * @see Connect#addPMWakeupListener
     * @since 1.5.2
     */
    public void addPMWakeupListener(final PMWakeupListener l) throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive PMSuspend events for this domain.
     *
     * @param l the PMSuspend listener
     * @throws LibvirtException on failure
     * @see Connect#removePMSuspendListener
     * @see Connect#addPMSuspendListener
     * @since 1.5.2
     */
    public void addPMSuspendListener(final PMSuspendListener l) throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Reset a domain immediately without any guest OS shutdown.
     */
    public void reset() throws LibvirtException {
        processError(libvirt.virDomainReset(this.VDP, 0));
    }

    /**
     * Revert the domain to a given snapshot.
     *
     * @param snapshot the snapshot to revert to
     * @return 0 if the creation is successful
     * @throws LibvirtException
     */
    public int revertToSnapshot(DomainSnapshot snapshot) throws LibvirtException {
        return processError(libvirt.virDomainRevertToSnapshot(snapshot.VDSP, 0));
    }

    /**
     * Revert the domain to a given snapshot.
     *
     * @param snapshot the snapshot to revert to
     * @param flags bitwise-OR of {@link org.libvirt.flags.DomainSnapshotRevertFlags}
     * @return 0 if the creation is successful
     * @throws LibvirtException
     */
    public int revertToSnapshot(DomainSnapshot snapshot, int flags) throws LibvirtException {
        return processError(libvirt.virDomainRevertToSnapshot(snapshot.VDSP, flags));
    }

    /**
     * Suspends this domain and saves its memory contents to a file on disk.
     * After the call, if successful, the domain is not listed as running
     * anymore (this may be a problem). Use Connect.virDomainRestore() to
     * restore a domain after saving.
     *
     * @param to path for the output file
     * @throws LibvirtException
     */
    public void save(String to) throws LibvirtException {
        processError(libvirt.virDomainSave(VDP, to));
    }

    public String screenshot(Stream stream, int screen) throws LibvirtException {
        CString mimeType = libvirt.virDomainScreenshot(this.VDP, stream.getVSP(), screen, 0);
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
    public void setAutostart(boolean autostart) throws LibvirtException {
        int autoValue = autostart ? 1 : 0;
        processError(libvirt.virDomainSetAutostart(VDP, autoValue));
    }

    /**
     * * Dynamically change the maximum amount of physical memory allocated to a
     * domain. This function requires priviledged access to the hypervisor.
     *
     * @param memory the amount memory in kilobytes
     * @throws LibvirtException
     */
    public void setMaxMemory(long memory) throws LibvirtException {
        processError(libvirt.virDomainSetMaxMemory(VDP, new NativeLong(memory)));
    }

    /**
     * Dynamically changes the target amount of physical memory allocated to
     * this domain. This function may requires priviledged access to the
     * hypervisor.
     *
     * @param memory in kilobytes
     * @throws LibvirtException
     */
    public void setMemory(long memory) throws LibvirtException {
        processError(libvirt.virDomainSetMemory(VDP, new NativeLong(memory)));
    }

    /**
     * @type: type of metadata, from {@link DomainMetadataFlags}
     * @metadata: new metadata text
     * @key: XML namespace key, or NULL
     * @uri: XML namespace URI, or NULL
     * @flags: bitwise-OR of {@link DomainDeviceModifyFlags}
     *
     * Sets the appropriate domain element given by @type to the
     * value of @metadata.  A @type of VIR_DOMAIN_METADATA_DESCRIPTION
     * is free-form text; VIR_DOMAIN_METADATA_TITLE is free-form, but no
     * newlines are permitted, and should be short (although the length is
     * not enforced). For these two options @key and @uri are irrelevant and
     * must be set to NULL.
     *
     * For type VIR_DOMAIN_METADATA_ELEMENT @metadata  must be well-formed
     * XML belonging to namespace defined by @uri with local name @key.
     *
     * Passing NULL for @metadata says to remove that element from the
     * domain XML (passing the empty string leaves the element present).
     *
     * The resulting metadata will be present in virDomainGetXMLDesc(),
     * as well as quick access through virDomainGetMetadata().
     *
     * @flags controls whether the live domain, persistent configuration,
     * or both will be modified.
     *
     * @return 0 on success, -1 in case of failure.
     * @throws LibvirtException
     */
    public int setMetadata(DomainMetadataFlags type, String metadata, String key, String uri, int flags) throws LibvirtException {
        return processError(libvirt.virDomainSetMetadata(VDP, type.getValue(), metadata, key, uri, flags));
    }

    /**
     * Changes the scheduler parameters
     *
     * @param params an array of TypedParameter objects to be changed
     * @throws LibvirtException
     */
    public void setSchedulerParameters(TypedParameter[] params) throws LibvirtException {
        virTypedParameter[] input = new virTypedParameter[params.length];
        for (int x = 0; x < params.length; x++) {
            input[x] = TypedParameter.toNative(params[x]);
        }
        processError(libvirt.virDomainSetSchedulerParameters(VDP, input, params.length));
    }

    /**
     * Dynamically changes the number of virtual CPUs used by this domain. Note
     * that this call may fail if the underlying virtualization hypervisor does
     * not support it or if growing the number is arbitrary limited. This
     * function requires priviledged access to the hypervisor.
     *
     * @param nvcpus the new number of virtual CPUs for this domain
     * @throws LibvirtException
     */
    public void setVcpus(int nvcpus) throws LibvirtException {
        processError(libvirt.virDomainSetVcpus(VDP, nvcpus));
    }

    /**
     * Send key(s) to the guest.
     *
     * @param codeset  the set of keycodes
     * @param holdtime the duration that the keys will be held (in milliseconds)
     * @param keys     the key codes to be send
     */
    public void sendKey(KeycodeSet codeset, int holdtime, int... keys) throws LibvirtException {
        processError(libvirt.virDomainSendKey(this.VDP, codeset.ordinal(),
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
        processError(libvirt.virDomainShutdown(VDP));
    }

    /**
     * Creates a new snapshot of a domain based on the snapshot xml contained in
     * xmlDesc.
     *
     * @param xmlDesc string containing an XML description of the domain
     * @param flags   flags for creating the snapshot, see the virDomainSnapshotCreateFlags for the flag options
     * @return the snapshot
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotCreateXML(String xmlDesc, int flags) throws LibvirtException {
        DomainSnapshotPointer ptr = processError(libvirt.virDomainSnapshotCreateXML(VDP, xmlDesc, flags));
        return new DomainSnapshot(virConnect, ptr);
    }

    /**
     * Creates a new snapshot of a domain based on the snapshot xml contained in
     * xmlDesc.
     * <p>
     * This is just a convenience method, it has the same effect
     * as calling {@code snapshotCreateXML(xmlDesc, 0);}.
     *
     * @param xmlDesc string containing an XML description of the domain
     * @return the snapshot, or null on Error
     * @throws LibvirtException
     * @see #snapshotCreateXML(String, int)
     */
    public DomainSnapshot snapshotCreateXML(String xmlDesc) throws LibvirtException {
        return snapshotCreateXML(xmlDesc, 0);
    }

    /**
     * Get the current snapshot for a domain, if any.
     *
     * @return the snapshot
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotCurrent() throws LibvirtException {
        DomainSnapshotPointer ptr = processError(libvirt.virDomainSnapshotCurrent(VDP, 0));
        return new DomainSnapshot(virConnect, ptr);
    }

    /**
     * Collect the list of domain snapshots for the given domain. With the option to pass flags
     *
     * @return The list of names, or null if an error
     * @throws LibvirtException
     */
    public String[] snapshotListNames(int flags) throws LibvirtException {
        int num = snapshotNum();
        if (num > 0) {
            CString[] names = new CString[num];
            int got = processError(libvirt.virDomainSnapshotListNames(VDP, names, num, flags));

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
     * @return The list of names, or null if an error
     * @throws LibvirtException
     * @see #snapshotListNames(int)
     */
    public String[] snapshotListNames() throws LibvirtException {
        return snapshotListNames(0);
    }

    /**
     * Retrieve a snapshot by name
     *
     * @param name the name
     * @return The located snapshot
     * @throws LibvirtException
     */
    public DomainSnapshot snapshotLookupByName(String name) throws LibvirtException {
        DomainSnapshotPointer ptr = processError(libvirt.virDomainSnapshotLookupByName(VDP, name, 0));
        return new DomainSnapshot(virConnect, ptr);
    }

    /**
     * Provides the number of domain snapshots for this domain..
     */
    public int snapshotNum() throws LibvirtException {
        return processError(libvirt.virDomainSnapshotNum(VDP, 0));
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
        processError(libvirt.virDomainSuspend(VDP));
    }

    /**
     * undefines this domain but does not stop it if it is running
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        processError(libvirt.virDomainUndefine(VDP));
    }

    /**
     * Undefines this domain but does not stop if it it is running. With option for passing flags
     *
     * @param flags flags for undefining the domain. See virDomainUndefineFlagsValues for more information
     * @throws LibvirtException
     */
    public void undefine(int flags) throws LibvirtException {
        processError(libvirt.virDomainUndefineFlags(VDP, flags));
    }

    /**
     * Change a virtual device on a domain
     *
     * @param xml   the xml to update with
     * @param flags controls the update
     * @return always 0
     * @throws LibvirtException
     */
    public int updateDeviceFlags(String xml, int flags) throws LibvirtException {
        return processError(libvirt.virDomainUpdateDeviceFlags(VDP, xml, flags));
    }
}
