package org.libvirt;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import org.libvirt.event.AgentLifecycleListener;
import org.libvirt.event.BlockJobListener;
import org.libvirt.event.IOErrorListener;
import org.libvirt.event.LifecycleListener;
import org.libvirt.event.PMSuspendListener;
import org.libvirt.event.PMWakeupListener;
import org.libvirt.event.RebootListener;
import org.libvirt.jna.CString;
import org.libvirt.jna.CStringByReference;
import org.libvirt.jna.DomainPointer;
import org.libvirt.jna.DomainSnapshotPointer;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.SizeT;
import org.libvirt.jna.virDomainBlockInfo;
import org.libvirt.jna.virDomainBlockJobInfo;
import org.libvirt.jna.virDomainBlockStats;
import org.libvirt.jna.virDomainInfo;
import org.libvirt.jna.virDomainInterface;
import org.libvirt.jna.virDomainInterfaceStats;
import org.libvirt.jna.virDomainJobInfo;
import org.libvirt.jna.virDomainMemoryStats;
import org.libvirt.jna.virSchedParameter;
import org.libvirt.jna.virTypedParameter;
import org.libvirt.jna.virVcpuInfo;

import static org.libvirt.Library.libvirt;
import static org.libvirt.Library.libvirtQemu;
import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.ErrorHandler.processErrorIfZero;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.ptr.IntByReference;

import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static final class BlockCommitFlags {
        /** NULL base means next backing file, not whole chain */
        public static int SHALLOW         = bit(0);

        /** Delete any files that are now invalid after their contents have been committed */
        public static int DELETE          = bit(1);

        /** Allow a two-phase commit when top is the active layer */
        public static int ACTIVE          = bit(2);

        /** keep the backing chain referenced using relative names */
        public static int RELATIVE        = bit(3);

        /** bandwidth in bytes/s instead of MiB/s */
        public static int BANDWIDTH_BYTES = bit(4);
    }

    public static final class BlockCopyFlags {
        /** Limit copy to top of source backing chain */
        public static int SHALLOW            = bit(0);

        /** Reuse existing external file for a copy */
        public static int REUSE_EXT          = bit(1);

        /** Don't force usage of recoverable job for the copy operation */
        public static int TRANSIENT_JOB      = bit(2);

        /**
         * Force the copy job to synchronously propagate guest writes into the destination image,
         * so that the copy is guaranteed to converge
         */
        public static int SYNCHRONOUS_WRITES = bit(3);
    }

    public static final class BlockCopyParameters {
        /**
         * The maximum bandwidth in bytes/s, and is used while getting the copy operation
         * into the mirrored phase, with a type of ullong.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_DOMAIN_BLOCK_COPY_BANDWIDTH">
         *     Libvirt Documentation</a>
         */
        public static String BANDWIDTH = "bandwidth";

        /**
         *  How much data in bytes can be in flight between source and destination,
         *  as an unsigned long long.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_DOMAIN_BLOCK_COPY_BUF_SIZE">
         *     Libvirt Documentation</a>
         */
        public static String BUF_SIZE = "buf-size";

        /**
         * The granularity in bytes at which the copy operation recognizes dirty blocks that need copying,
         * as an unsigned int.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_DOMAIN_BLOCK_COPY_GRANULARITY">
         *     Libvirt Documentation</a>
         */
        public static String GRANULARITY = "granularity";
    }

    /**
     * Contains multiple constants that defines "virDomainMigrate* params" multiple field.
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html">Libvirt domain documentation.</a>, and
     *      <a href="https://gitlab.com/libvirt/libvirt/-/blob/master/include/libvirt/libvirt-domain.h">libvirt-domain.h</a>.
     */
    public static final class DomainMigrateParameters {
        /**
         * Lists the block devices to be migrated.
         * At the moment this is only supported by the QEMU driver but not for the tunnelled migration.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_MIGRATE_DISKS">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_MIGRATE_DISKS = "migrate_disks";

        /**
         * The new configuration to be used for the domain on the destination host as
         * TYPED_PARAM_STRING. The configuration must include an identical set of
         * virtual devices, to ensure a stable guest ABI across migration.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_DEST_XML">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_DEST_XML = "destination_xml";

        /**
         * The new persistent configuration to be used for the domain on the destination host as
         * TYPED_PARAM_STRING. This field cannot be used to rename the domain during migration.
         * Domain name in the destination XML must match the original domain name.
         * Omitting this parameter keeps the original domain persistent configuration.
         *  @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_PERSIST_XML">
         *      Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_PERSIST_XML = "persistent_xml";

        /**
         * The maximum bandwidth (in MiB/s) that will be used for migration as TYPED_PARAM_ULLONG.
         * If set to 0 or omitted, libvirt will choose a suitable default. Some hypervisors do not
         * support this feature and will return an error if this field is used and is not 0.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_BANDWIDTH">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_BANDWIDTH = "bandwidth";

        /**
         * The maximum bandwidth (in MiB/s) that will be used for post-copy phase of a migration
         * as TYPED_PARAM_ULLONG. If set to 0 or omitted, post-copy migration speed will not be limited.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_BANDWIDTH_POSTCOPY">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_BANDWIDTH_POSTCOPY = "bandwidth.postcopy";

        /**
         * The name to be used for the domain on the destination host as TYPED_PARAM_STRING.
         * Omitting this parameter keeps the domain name the same. This field is only allowed to be
         * used with hypervisors that support domain renaming during migration.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_DEST_NAME">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_DEST_NAME = "destination_name";

        /**
         * URI to use for migrating client's connection to domain's graphical console, as TYPED_PARAM_STRING.
         * If specified, the client will be asked to automatically reconnect using these parameters instead of
         * the automatically computed ones.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_GRAPHICS_URI">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_GRAPHICS_URI = "graphics_uri";

        /**
         * The listen address that hypervisor on the destination side should bind to for incoming migration.
         * Both IPv4 and IPv6 addresses are accepted as well as hostnames (the resolving is done on destination).
         * Some hypervisors do not support this feature and will return an error if this field is used.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_LISTEN_ADDRESS">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_LISTEN_ADDRESS = "listen_address";

        /**
         * URI to use for initiating domain migration as TYPED_PARAM_STRING. It takes a hypervisor specific format.
         * The uri_transports element of the hypervisor capabilities XML includes details of the
         * supported URI schemes. When omitted libvirt will auto-generate suitable default URI.
         * It is typically only necessary to specify this URI if the destination host has multiple
         * interfaces and a specific interface is required to transmit migration data.
         * This field may not be used when VIR_MIGRATE_TUNNELLED flag is set.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_URI">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_URI = "migrate_uri";

        /**
         * Port that destination server should use for incoming disks migration.
         * Type is TYPED_PARAM_INT. If set to 0 or omitted, libvirt will choose a suitable default.
         * At the moment this is only supported by the QEMU driver.
         *
         * For more details, please check Libvirt documentation:7
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_DISKS_PORT"> Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_DISKS_PORT = "disks_port";

        /**
         * URI used for incoming disks migration. Type is TYPED_PARAM_STRING. Only schemes "tcp" and
         * "unix" are accepted. TCP URI can currently only provide a server and port to listen on
         * (and connect to), UNIX URI may only provide a path component for a UNIX socket.
         * UNIX URI is only usable if the management application makes sure that socket created with
         * this name on the destination will be reachable from the source under the same exact path.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_DISKS_URI">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_DISKS_URI = "disks_uri";

        /**
         * The name of the method used to compress migration traffic as TYPED_PARAM_STRING.
         * Supported compression methods: xbzrle, mt.
         * The parameter may be specified multiple times if more than one method should be used.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_COMPRESSION">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_COMPRESSION = "compression";

        /**
         * The level of compression for multithread compression as TYPED_PARAM_INT.
         * Accepted values are in range 0-9. 0 is no compression, 1 is maximum speed
         * and 9 is maximum compression.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_COMPRESSION_MT_LEVEL">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_COMPRESSION_MT_LEVEL = "compression.mt.level";

        /**
         * The number of compression threads for multithread compression as TYPED_PARAM_INT.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_COMPRESSION_MT_THREADS">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_COMPRESSION_MT_THREADS = "compression.mt.threads";

        /**
         * The number of decompression threads for multithread compression as TYPED_PARAM_INT.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_COMPRESSION_MT_DTHREADS">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_COMPRESSION_MT_DTHREADS = "compression.mt.dthreads";

        /**
         * The size of page cache for xbzrle compression as TYPED_PARAM_ULLONG.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_COMPRESSION_XBZRLE_CACHE">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_COMPRESSION_XBZRLE_CACHE = "compression.xbzrle.cache";

        /**
         * The initial percentage guest CPUs are throttled to when auto-convergence decides migration
         * is not converging as TYPED_PARAM_INT.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_AUTO_CONVERGE_INITIAL">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_AUTO_CONVERGE_INITIAL = "auto_converge.initial";

        /**
         * The increment added to VIR_MIGRATE_PARAM_AUTO_CONVERGE_INITIAL whenever the hypervisor
         * decides the current rate is not enough to ensure convergence of the migration. As TYPED_PARAM_INT.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_AUTO_CONVERGE_INCREMENT">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_AUTO_CONVERGE_INCREMENT = "auto_converge.increment";

        /**
         * The number of connections used during parallel migration as TYPED_PARAM_INT.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_PARALLEL_CONNECTIONS">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_PARALLEL_CONNECTIONS = "parallel.connections";

        /**
         * Override the destination host name used for TLS verification as TYPED_PARAM_STRING.
         * Normally the TLS certificate from the destination host must match the host's name for TLS
         * verification to succeed. When the certificate does not match the destination hostname and
         * the expected certificate's hostname is known, this parameter can be used to pass this
         * expected hostname when starting the migration.
         *
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#VIR_MIGRATE_PARAM_TLS_DESTINATION">
         *     Libvirt Documentation</a>
         */
        public static final String VIR_MIGRATE_PARAM_TLS_DESTINATION = "tls.destination";
    }

    public static final class BlockJobInfoFlags {
        /** bandwidth in bytes/s instead of MiB/s */
        public static int BANDWIDTH_BYTES = bit(0);
    }

    public static final class BlockJobAbortFlags {
        public static int ASYNC = bit(0);
        public static int PIVOT = bit(1);
    }

    public static final class BlockResizeFlags {
        /**
         * size is in bytes instead of KiB
         */
        public static final int BYTES = 1;
    }

    public static final class CreateFlags {
        /**  Default behavior */
        public static final int NONE         = 0;

        /**  Launch guest in paused state */
        public static final int PAUSED       = bit(0);

        /**  Automatically kill guest when virConnectPtr is closed */
        public static final int AUTODESTROY  = bit(1);

        /**  Avoid file system cache pollution */
        public static final int BYPASS_CACHE = bit(2);

        /**  Boot, discarding any managed save */
        public static final int FORCE_BOOT   = bit(3);

        /**  Validate the XML document against schema */
        public static final int VALIDATE     = bit(4);
    }

    public static final class InterfaceAddressesSource {
        /** Parse DHCP lease file */
        public static final int VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_LEASE = 0;

        /** Query qemu guest agent */
        public static final int VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_AGENT = 1;

        /** Query ARP tables */
        public static final int VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_ARP = 2;
    }


    public static final class MetadataType {
        /** Operate on &lt;description> */
        public static final int DESCRIPTION = 0;

        /** Operate on &lt;title> */
        public static final int TITLE       = 1;

        /** Operate on &lt;metadata> */
        public static final int ELEMENT     = 2;
    }

    public static final class MigrateFlags {
        /** live migration */
        public static final int LIVE              = bit(0);

        /** direct source -> dest host control channel */
        public static final int PEER2PEER         = bit(1);

        /** tunnel migration data over libvirtd connection */
        public static final int TUNNELED          = bit(2);

        /** persist the VM on the destination */
        public static final int PERSIST_DEST      = bit(3);

        /** undefine the VM on the source */
        public static final int UNDEFINE_SOURCE   = bit(4);

        /** pause on remote side */
        public static final int PAUSED            = bit(5);

        /** migration with non-shared storage with full disk copy */
        public static final int NON_SHARED_DISK   = bit(6);

        /** migration with non-shared storage with incremental copy
         * (same base image shared between source and destination)
         */
        public static final int NON_SHARED_INC    = bit(7);

        /** protect for changing domain configuration through the
         * whole migration process; this will be used automatically
         * when supported
         */
        public static final int CHANGE_PROTECTION = bit(8);

        /** force migration even if it is considered unsafe */
        public static final int UNSAFE            = bit(9);

        /** Migrate a domain definition without starting the domain on the
         *  destination and without stopping it on the source host */
        public static final int OFFLINE           = bit(10);

        /** Compress migration data */
        public static final int COMPRESSED        = bit(11);

        /** Cancel migration if a soft error (such as I/O error) happens
         *  during migration */
        public static final int ABORT_ON_ERROR    = bit(12);

        /** Enable algorithms that ensure a live migration will
         * eventually converge */
        public static final int AUTO_CONVERGE     = bit(13);

        /** This flag can be used with RDMA migration (i.e., when
         * PARAM_URI starts with "rdma://") to tell the
         * hypervisor to pin all domain's memory at once before migration
         * starts rather then letting it pin memory pages as needed */
        public static final int RDMA_PIN_ALL      = bit(14);

        /** Setting the POSTCOPY flag tells libvirt to enable
         * post-copy migration */
        public static final int POSTCOPY          = bit(15);

        /** Setting the TLS flag will cause the migration
         * to attempt to use the TLS environment configured by the
         * hypervisor in order to perform the migration. */
        public static final int TLS               = bit(16);

        /** Send memory pages to the destination host through several
         * network connections */
        public static final int PARALLEL          = bit(17);

    }

    public static final class XMLFlags {
        /** dump security sensitive information too */
        public static final int SECURE = bit(0);

        /** dump inactive domain information*/
        public static final int INACTIVE = bit(1);

        /** update guest CPU requirements according to host CPU */
        public static final int UPDATE_CPU   = bit(2);

        /** dump XML suitable for migration */
        public static final int MIGRATABLE   = bit(3);
    }

    public static final class UndefineFlags {
        /** Also remove any managed save */
        public static final int MANAGED_SAVE = bit(0);

        /** If last use of domain, then also remove any snapshot metadata */
        public static final int SNAPSHOTS_METADATA = bit(1);
    }

    public static final class RebootFlags {
        /** hypervisor choice */
        public static final int DEFAULT        = 0;

        /** Send ACPI event */
        public static final int ACPI_POWER_BTN = bit(0);

        /**  Use guest agent */
        public static final int GUEST_AGENT    = bit(1);

        /**  Use initctl */
        public static final int INITCTL        = bit(2);

        /**  Send a signal */
        public static final int SIGNAL          = bit(3);

        /**  Use paravirt guest control */
        public static final int PARAVIRT        = bit(4);
    }

    public static final class SnapshotCreateFlags {

        /**	Restore or alter metadata */
        public static final int REDEFINE    = bit(0);

        /**	With redefine, make snapshot current */
        public static final int CURRENT     = bit(1);

        /**	Make snapshot without remembering it */
        public static final int NO_METADATA = bit(2);

        /**	Stop running guest after snapshot */
        public static final int HALT        = bit(3);

        /**	disk snapshot, not full system */
        public static final int DISK_ONLY   = bit(4);

        /**	reuse any existing external files */
        public static final int REUSE_EXT   = bit(5);

        /**	use guest agent to quiesce all mounted file systems within the domain */
        public static final int QUIESCE     = bit(6);

        /**	atomically avoid partial changes */
        public static final int ATOMIC      = bit(7);

        /**	create the snapshot while the guest is running */
        public static final int LIVE        = bit(8);

        /**	validate the XML against the schema */
        public static final int VALIDATE    = bit(9);
    }

    public static final class SnapshotListFlags {

        /** List all descendants, not just children, when listing a snapshot
         * For historical reasons, groups do not use contiguous bits. */
        public static final int DESCENDANTS  = bit(0);

        /**  Filter by snapshots with no parents, when listing a domain */
        public static final int ROOTS        = bit(0);

        /**  Filter by snapshots which have metadata */
        public static final int METADATA     = bit(1);

        /**  Filter by snapshots with no children */
        public static final int LEAVES       = bit(2);

        /**  Filter by snapshots that have children */
        public static final int NO_LEAVES    = bit(3);

        /**  Filter by snapshots with no metadata */
        public static final int NO_METADATA  = bit(4);

        /**  Filter by snapshots taken while guest was shut off */
        public static final int INACTIVE     = bit(5);

        /**  Filter by snapshots taken while guest was active, and with
         * memory state */
        public static final int ACTIVE       = bit(6);

        /**  Filter by snapshots taken while guest was active, but without
         * memory state */
        public static final int DISK_ONLY    = bit(7);

        /**  Filter by snapshots stored internal to disk images */
        public static final int INTERNAL     = bit(8);

        /**  Filter by snapshots that use files external to disk images */
        public static final int EXTERNAL     = bit(9);

        /**  Ensure parents occur before children in the resulting list */
        public static final int TOPOLOGICAL  = bit(10);
    }

    public static final class ModificationImpact {

        /** Affect current domain state */
        public static final int CURRENT = 0;

        /** Affect running domain state */
        public static final int LIVE    = bit(0);

        /** Affect persistent domain state */
        public static final int CONFIG  = bit(1);
    }

    public static final class DeviceModifyFlags {
        public static final int CONFIG  = ModificationImpact.CONFIG;
        public static final int CURRENT = ModificationImpact.CURRENT;
        public static final int LIVE    = ModificationImpact.LIVE;
        public static final int FORCE   = bit(2);
    }


    public static final class VcpuFlags {
        public static final int CONFIG  = ModificationImpact.CONFIG;
        public static final int CURRENT = ModificationImpact.CURRENT;
        public static final int LIVE    = ModificationImpact.LIVE;

        /** Max rather than current count */
        public static final int MAXIMUM      = bit(2);

        /** Modify state of the cpu in the guest */
        public static final int GUEST        = bit(3);

        /** Make vcpus added hot(un)pluggable */
        public static final int HOTPLUGGABLE = bit(4);
    }

    public static final class DomainSetUserPasswordFlags {
        /** Password is not encrypted */
        public static final int DOMAIN_PASSWORD_NOT_ENCRYPTED = 0;
        /** Password is already encrypted */
        public static final int DOMAIN_PASSWORD_ENCRYPTED = bit(1);
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainAbortJob">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainAttachDevice">Libvirt
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
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainAttachDeviceFlags">
            Libvirt Documentation</a>
     * @param xmlDesc
     *            XML description of one device
     * @param flags
     *            the an OR'ed set of {@link DeviceModifyFlags}
     * @throws LibvirtException
     */
    public void attachDeviceFlags(final String xmlDesc, final int flags)
            throws LibvirtException {
        processError(libvirt.virDomainAttachDeviceFlags(vdp, xmlDesc, flags));
    }

    /**
     * This function migrates domain's live block device (disk) to another
     * block device.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainBlockCopy">
     *    virDomainBlockCopy</a>
     * @param diskPath
     *            Path to current disk
     * @param xmlDesc
     *            XML description of destination disk
     * @param params
     *            Hypervisor-specific tuning parameters
     * @param flags
     *            Bitwise OR'ed values of {@link BlockCopyFlags}
     * @throws LibvirtException
     */
    public void blockCopy(final String diskPath, final String xmlDesc,
                          final TypedParameter[] params, final int flags) throws LibvirtException {
        assert params != null : "blockCopy typed parameters cannot be null";
        virTypedParameter[] input = generateNativeVirTypedParameters(params);
        processError(libvirt.virDomainBlockCopy(vdp, diskPath, xmlDesc, input, input.length, flags));
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
     * Commit changes that were made to temporary top-level files within a disk
     * image backing file chain into a lower-level base file.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainBlockCommit">
     *    virDomainBlockCommit</a>
     * @param disk path to the block device, or device shorthand
     * @param base path to backing file to merge into, or device shorthand, or
     *             NULL for default
     * @param top path to file within backing chain that contains data to be
     *            merged, or device shorthand, or NULL to merge all possible data
     * @param bandwidth (optional) specify bandwidth limit; flags determine the unit
     * @param flags bitwise-OR of {@link BlockCommitFlags}
     * @throws LibvirtException
     */
    public void blockCommit(String disk, String base, String top, long bandwidth, int flags) throws LibvirtException {
        processError(libvirt.virDomainBlockCommit(vdp, disk, base, top, bandwidth, flags));
    }

    /**
     * Request block job information for the given disk.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainGetBlockJobInfo">
     *    virDomainGetBlockJobInfo</a>
     * @param disk path to the block device, or device shorthand
     * @param flags see {@link BlockJobInfoFlags}
     * @return the statistics in a BlockJobInfo object
     * @throws LibvirtException
     */
    public DomainBlockJobInfo getBlockJobInfo(String disk, int flags) throws LibvirtException {
        final virDomainBlockJobInfo info = new virDomainBlockJobInfo();
        processError(libvirt.virDomainGetBlockJobInfo(vdp, disk, info, flags));
        return new DomainBlockJobInfo(info);
    }

    /**
     * Cancel the active block job on the given disk.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainBlockJobAbort">
     *    virDomainBlockJobAbort</a>
     * @param disk path to the block device, or device shorthand
     * @param flags see {@link BlockJobAbortFlags}
     * @throws LibvirtException
     */
    public void blockJobAbort(String disk, int flags) throws LibvirtException {
        processError(libvirt.virDomainBlockJobAbort(vdp, disk, flags));
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
     * between a single virtual &amp; all physical CPUs of a domain.
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainDetachDevice">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainDetachDeviceFlags">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainGetInfo">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainGetJobInfo">Libvirt
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
     * Retrieves the appropriate domain element given by type.
     *
     * @param type type of metadata, see {@link MetadataType}
     * @param uri XML namespace identifier if type == MetadataType.ELEMENT, null otherwise
     * @param flags bitwise-OR of {@link ModificationImpact}
     * @return the metadata string
     * @throws LibvirtException
     */
    public String getMetadata(int type, String uri, int flags) throws LibvirtException {
        return processError(libvirt.virDomainGetMetadata(vdp, type, uri, flags));
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
     * @param flags bitwise-OR of {@link XMLFlags}
     * @return the XML description String
     * @throws LibvirtException
     * @see <a href="https://libvirt.org/format.html#Normal1" >The XML
     *      Description format </a>
     */
    public String getXMLDesc(final int flags) throws LibvirtException {
        return processError(libvirt.virDomainGetXMLDesc(vdp, flags)).toString();
    }

    /**
     * Determine if the domain has a snapshot
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt.html#virDomainHasCurrentSnapshot"
     *      >Libvirt Documentation</a>
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int hasCurrentSnapshot() throws LibvirtException {
        return processError(libvirt.virDomainHasCurrentSnapshot(vdp, 0));
    }

    /**
     * Determine if the domain has a managed save image
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt.html#virDomainHasManagedSaveImage"
     *      >Libvirt Documentation</a>
     * @return 0 if no image is present, 1 if an image is present, and -1 in
     *         case of error
     * @throws LibvirtException
     */
    public int hasManagedSaveImage() throws LibvirtException {
        return processError(libvirt.virDomainHasManagedSaveImage(vdp, 0));
    }

    /** Retrieves a list of the network interfaces present in given domain along with their IP and MAC addresses.
     *  Note that single interface can have multiple or even 0 IP addresses.
     *  If source is VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_LEASE, the DHCP lease file associated with any virtual
     *  networks will be examined to obtain the interface addresses. This only returns data for interfaces which are
     *  connected to virtual networks managed by libvirt.
     *  If source is VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_AGENT, a configured guest agent is needed for successful
     *  return from this API. Moreover, if guest agent is used then the interface name is the one seen by guest OS.
     *  To match such interface with the one from dom XML use MAC address or IP range.
     *  If source is VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_ARP, the host ARP table will be check to obtain the interface
     *  addresses. As the arp cache refreshes in time, the returned ip address may be unreachable. Depending on the
     *  route table config of the guest, the returned mac address may be duplicated.
     *
     *  Note that for some source values some pieces of returned ifaces might be unset (e.g.
     *  VIR_DOMAIN_INTERFACE_ADDRESSES_SRC_ARP does not set IP address prefix as ARP table does not have any notion
     *  of that).
     *
     *  name and hwaddr of the returned interfaces are never NULL.
     *
     * @param source one of the {@link InterfaceAddressesSource} constants
     * @param flags currently unused, pass zero
     * @return the interfaces of this domain
     * @throws LibvirtException if something goes wrong
     */
    public Collection<DomainInterface> interfaceAddresses(int source, int flags) throws LibvirtException {
        PointerByReference ifaces = new PointerByReference();
        ifaces.setValue(Pointer.NULL);
        int count = libvirt.virDomainInterfaceAddresses(vdp, ifaces, source, flags);

        if (ifaces.getValue() == null) {
            if (count != 0) {
                processError(count);
                throw new IllegalStateException("virDomainInterfaceAddresses returned " + count);
            }
            return Collections.emptyList();
        }

        try {
            if (count < 0) {
                processError(count);
                throw new IllegalStateException("virDomainInterfaceAddresses returned " + count);
            }
            return Arrays.stream(ifaces.getValue().getPointerArray(0, count))
                    .map(virDomainInterface::new)
                    .map(vdi -> {
                        DomainInterface di = new DomainInterface(vdi);
                        libvirt.virDomainInterfaceFree(vdi.getPtr());
                        return di;
                    })
                    .collect(Collectors.toList());
        } finally {
            Library.free(ifaces.getValue());
        }
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainIsActive">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainIsPersistent">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainManagedSave">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainManagedSaveRemove">Libvirt
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
     * of more of the following: Domain.MigrateFlags.LIVE Attempt a live
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
     * Migrate the domain object from its current host to the destination host
     * given by dconn (a connection to the destination host).
     * See VIR_MIGRATE_PARAM_* and virDomainMigrateFlags for detailed
     * description of accepted migration parameters and flags.
     * See virDomainMigrateFlags documentation for description of individual
     * flags. VIR_MIGRATE_TUNNELLED and VIR_MIGRATE_PEER2PEER are not supported
     * by this API, use virDomainMigrateToURI3 instead.
     * <br>
     * There are many limitations on migration imposed by the underlying
     * technology - for example it may not be possible to migrate between
     * different processors even with the same architecture, or between different
     * types of hypervisor. virDomainFree should be used to free the resources
     * after the returned domain object is no longer needed.
     * <br> <br>
     * For more informations, please @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainMigrate3"> virDomainMigrate3</a>
     * @param dconn
     *            destination host (a Connect object)
     * @param params
     *            (optional) migration parameters
     *
     * @param flags
     *            bitwise-OR of virDomainMigrateFlags
     * @return
     *            the new domain object if the migration was successful. Note that
     *            the new domain object exists in the scope of the destination
     *            connection (dconn).
     * @throws LibvirtException
     */
    public Domain migrate(final Connect dconn, final TypedParameter[] params, long flags) throws LibvirtException {
        assert params != null : "migrate Typed parameters cannot be null";
        virTypedParameter[] virTypedParameters = generateNativeVirTypedParameters(params);
        DomainPointer newPtr = processError(libvirt.virDomainMigrate3(vdp, dconn.vcp, virTypedParameters, params.length, new NativeLong(flags)));
        return new Domain(dconn, newPtr);
    }

    /**
     * This methods creates an array of virTypedParameter objects based on the
     * given array of TypedParameter objects. The way it has been designed ensures
     * that the output will be in contiguous memory, regardless of the memory
     * allocated for each of the provided "TypedParameter[]", avoiding
     * "non contiguous memory due to bad backing address at Structure array".
     *
     * @param params
     *          Array of TypedParameter objects which can be:
     *          TypedBooleanParameter, TypedBooleanParameter, TypedDoubleParameter,
     *          TypedIntParameter, TypedLongParameter, TypedStringParameter,
     *          TypedUintParameter, or TypedUlongParameter
     * @return
     *          An array of "virTypedParameter" objects in contiguous memory.
     */
    private virTypedParameter[] generateNativeVirTypedParameters(TypedParameter[] params) {
        virTypedParameter param = new virTypedParameter();
        virTypedParameter[] virTypedParameters = (virTypedParameter[]) param.toArray(params.length);
        for (int x = 0; x < params.length; x++) {
            virTypedParameter temporaryTypedParameter = TypedParameter.toNative(params[x]);
            virTypedParameters[x].field = temporaryTypedParameter.field;
            virTypedParameters[x].type = temporaryTypedParameter.type;
            virTypedParameters[x].value = temporaryTypedParameter.value;
        }
        return virTypedParameters;
    }

    /**
     * Sets maximum tolerable time for which the domain is allowed to be paused
     * at the end of live migration.
     *
     * @see <a
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainMigrateSetMaxDowntime">LIbvirt
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
     * {@link MigrateFlags#PEER2PEER PEER2PEER}
     * is flag set), or in miguri (if neither the
     * {@link MigrateFlags#PEER2PEER PEER2PEER} nor the
     * {@link MigrateFlags#TUNNELED TUNNELED} migration
     * flag is set in flags).
     *
     * @see <a
     * href="https://libvirt.org/html/libvirt-libvirt.html#virDomainMigrateToURI">
     * virDomainMigrateToURI</a>
     *
     * @param dconnuri
     *            (optional) URI for target libvirtd if @flags includes PEER2PEER
     * @param miguri
     *            (optional) URI for invoking the migration, not if @flags includs TUNNELED
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
     *       href="https://libvirt.org/html/libvirt-libvirt.html#virDomainMigrateToURI">
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
     * Injects a <em>wakeup</em> into the guest that previously used
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
     *            extra flags for the reboot operation, see {@link RebootFlags}
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
     * Adds a callback to receive notifications of Block Job events
     *
     * @see <a
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny">Libvirt
     *      Documentation</a>
     * @param cb
     * @throws LibvirtException
     */
    public void addBlockJobListener(final BlockJobListener cb) throws LibvirtException {
        virConnect.domainEventRegister(this, cb);
    }

    /**
     * Removes BlockJobListener from the event framework, so it no longer receives events
     * @param cb
     *         The BlockJobListener
     * @throws LibvirtException
     */
    public void removeBlockJobListener(final BlockJobListener cb) throws LibvirtException {
        virConnect.removeBlockJobListener(cb);
    }

    /**
     * Adds a callback to receive notifications of IOError domain events
     * occurring on this domain.
     *
     * @see <a
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny">Libvirt
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
     *       href="https://libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
     *      >virConnectDomainEventRegisterAny</a>
     * @since 1.5.2
     */
    public void addRebootListener(final RebootListener l)
            throws LibvirtException {
        virConnect.domainEventRegister(this, l);
    }

    /**
     * Adds the specified listener to receive agent lifecycle events for this domain.
     *
     * @param  cb  the agent lifecycle listener
     * @throws    LibvirtException on failure
     *
     * @see Connect#addAgentLifecycleListener
     * @see Connect#removeAgentLifecycleListener
     * @see
     *  <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virConnectDomainEventRegisterAny">
    virConnectDomainEventRegisterAny</a>
     */
    public void addAgentLifecycleListener(final AgentLifecycleListener cb) throws LibvirtException {
        virConnect.domainEventRegister(this, cb);
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
     *       href="https://libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
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
     *       href="https://libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
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
     *       href="https://libvirt.org/html/libvirt-libvirt.html#virConnectDomainEventRegisterAny"
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
     *      "https://libvirt.org/html/libvirt-libvirt.html#virDomainRevertToSnapshot"
     *      >Libvirt Documentation</a>
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
     * Sets the appropriate domain element given by type to the value of metadata.
     *
     * A type of MetadataType.DESCRIPTION is free-form text; MetadataType.TITLE is
     * free-form, but no newlines are permitted, and should be short (although the length
     * is not enforced). For these two options key and uri are irrelevant and must be set
     * to null.
     *
     * For type MetadataType.ELEMENT metadata must be well-formed XML belonging to
     * namespace defined by uri with local name key.
     *
     * Passing null for metadata says to remove that element from the domain XML (passing
     * the empty string leaves the element present).
     *
     * @param type see {@link MetadataType}
     * @param metadata the new metadata content
     * @param key XML namespace prefix for type MetadataType.ELEMENT, null otherwise
     * @param uri XML namespace URI for typeMetadataType.ELEMENT, null otherwise
     * @param flags see {@link ModificationImpact}
     * @throws LibvirtException
     */
    public void setMetadata(int type, String metadata, String key, String uri, int flags)
            throws LibvirtException {
        processError(libvirt.virDomainSetMetadata(vdp, type, metadata, key, uri, flags));
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
     * Sets the user password to the value specified by password.
     * If flags contain DomainSetUserPasswordFlags.DOMAIN_PASSWORD_ENCRYPTED, the password
     * is assumed to be encrypted by the method required by the guest OS.
     *
     * @param user the username that will get a new password
     * @param password the password to set
     * @param flags see {@link DomainSetUserPasswordFlags}
     * @throws LibvirtException
     */
    public void setUserPassword(String user, String password, int flags) throws LibvirtException {
        processError(libvirt.virDomainSetUserPassword(vdp, user, password, flags));
    }

    /**
     * Dynamically changes the number of virtual CPUs used by this domain. Note
     * that this call may fail if the underlying virtualization hypervisor does
     * not support it or if growing the number is arbitrary limited. This
     * function requires privileged access to the hypervisor.
     *
     * @param nvcpus
     *            the new number of virtual CPUs for this domain
     * @throws LibvirtException
     */
    public void setVcpus(final int nvcpus) throws LibvirtException {
        processError(libvirt.virDomainSetVcpus(vdp, nvcpus));
    }

    /**
     * Dynamically changes the number of virtual CPUs used by this domain. Note
     * that this call may fail if the underlying virtualization hypervisor does
     * not support it or if growing the number is arbitrary limited. This
     * function requires privileged access to the hypervisor.
     *
     * @param nvcpus
     *            the new number of virtual CPUs for this domain
     * @param flags
     *            {@link VcpuFlags}
     * @throws LibvirtException
     */
    public void setVcpusFlags(final int nvcpus, final int flags) throws LibvirtException {
        processError(libvirt.virDomainSetVcpusFlags(vdp, nvcpus, flags));
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotCreateXML">Libvirt
     *      Documentation</a>
     * @param xmlDesc
     *            string containing an XML description of the domain
     * @param flags
     *            flags for creating the snapshot, see the {@link SnapshotCreateFlags } for the flag options
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotCreateXML">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotCurrent">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotListNames">Libvirt
     *      Documentation</a>
     * @param flags {@link SnapshotListFlags}
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotListNames">
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotLookupByName">Libvirt
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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotNum">Libvirt
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
     * @see <a href="https://libvirt.org/html/libvirt-libvirt.html#virDomainUndefineFlags">
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
     * @see <a href="https://libvirt.org/html/libvirt-libvirt.html#virDomainUpdateDeviceFlags">
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
     *      href="https://libvirt.org/html/libvirt-libvirt-qemu.html#virDomainQemuAgentCommand">Libvirt
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
        CString result = libvirtQemu != null ? libvirtQemu.virDomainQemuAgentCommand(vdp, cmd, timeout, flags) : new CString(null);
        processError(result);
        return result.toString();
    }

    /**
     * Qemu Monitor Command - it will only work with hypervisor connections to the QEMU driver.
     *
     *@see <a
     *      href="https://libvirt.org/html/libvirt-libvirt-qemu.html#virDomainQemuMonitorCommand">Libvirt
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
