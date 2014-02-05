package org.libvirt;

import java.io.Serializable;

import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.DomainPointer;
import org.libvirt.jna.NetworkPointer;
import org.libvirt.jna.virError;

/**
 * An error which is returned from libvirt,
 */
public class Error implements Serializable {

    /**
     * Returns the element of the given array at the specified index,
     * or the last element of the array if the index is not less than
     * {@code values.length}.
     *
     * @return n-th item of {@code values} when {@code n <
     *          values.length}, otherwise the last item of {@code values}.
     */
    private static final <T> T safeElementAt(final int n, final T[] values) {
        assert(n >= 0 && values.length > 0);

        int idx = Math.min(n, values.length - 1);
        return values[idx];
    }

    public static enum ErrorDomain {
        VIR_FROM_NONE,
        /** Error at Xen hypervisor layer */
        VIR_FROM_XEN,
        /** Error at connection with xend daemon */
        VIR_FROM_XEND,
        /** Error at connection with xen store */
        VIR_FROM_XENSTORE,
        /** Error in the S-Expression code */
        VIR_FROM_SEXPR,
        /** Error in the XML code */
        VIR_FROM_XML,
        /** Error when operating on a domain */
        VIR_FROM_DOM,
        /** Error in the XML-RPC code */
        VIR_FROM_RPC,
        /** Error in the proxy code */
        VIR_FROM_PROXY,
        /** Error in the configuration file handling */
        VIR_FROM_CONF,
        /** Error at the QEMU daemon */
        VIR_FROM_QEMU,
        /** Error when operating on a network */
        VIR_FROM_NET,
        /** Error from test driver */
        VIR_FROM_TEST,
        /** Error from remote driver */
        VIR_FROM_REMOTE,
        /** Error from OpenVZ driver */
        VIR_FROM_OPENVZ,
        /** Error at Xen XM layer */
        VIR_FROM_XENXM,
        /** Error in the Linux Stats code */
        VIR_FROM_STATS_LINUX,
        /** Error from Linux Container driver */
        VIR_FROM_LXC,
        /** Error from storage driver */
        VIR_FROM_STORAGE,
        /** Error from network config */
        VIR_FROM_NETWORK,
        /** Error from domain config */
        VIR_FROM_DOMAIN,
        /** Error at the UML driver */
        VIR_FROM_UML,
        /** Error from node device monitor */
        VIR_FROM_NODEDEV,
        /** Error from xen inotify layer */
        VIR_FROM_XEN_INOTIFY,
        /** Error from security framework */
        VIR_FROM_SECURITY,
        /** Error from VirtualBox driver */
        VIR_FROM_VBOX,
        /** Error when operating on an interface */
        VIR_FROM_INTERFACE,
        /** Error from OpenNebula driver */
        VIR_FROM_ONE,
        /** Error from ESX driver */
        VIR_FROM_ESX,
        /** Error from IBM power hypervisor */
        VIR_FROM_PHYP,
        /** Error from secret storage */
        VIR_FROM_SECRET,
        /** Error from CPU driver */
        VIR_FROM_CPU,
        /** Error from XenAPI */
        VIR_FROM_XENAPI,
        /** Error from network filter driver */
        VIR_FROM_NWFILTER,
        /** Error from Synchronous hooks */
        VIR_FROM_HOOK,
        /** Error from domain snapshot */
        VIR_FROM_DOMAIN_SNAPSHOT,
        /** Error from auditing subsystem */
        VIR_FROM_AUDIT,
        /** Error from sysinfo/SMBIOS */
        VIR_FROM_SYSINFO,
        /** Error from I/O streams */
        VIR_FROM_STREAMS,
        /** Error from VMware driver */
        VIR_FROM_VMWARE,
        /** Error from event loop impl */
        VIR_FROM_EVENT,
        /** Error from libxenlight driver */
        VIR_FROM_LIBXL,
        /** Error from lock manager */
        VIR_FROM_LOCKING,
        /** Error from Hyper-V driver */
        VIR_FROM_HYPERV,
        /** Error from capabilities */
        VIR_FROM_CAPABILITIES,
        /** Error from URI handling */
        VIR_FROM_URI,
        /** Error from auth handling */
        VIR_FROM_AUTH,
        /** Error from DBus */
        VIR_FROM_DBUS,

        /** unknown error domain */
        VIR_FROM_UNKNOWN; // must be the last entry!

        protected static final ErrorDomain wrap(int value) {
            return safeElementAt(value, values());
        }
    }

    public static enum ErrorLevel {
        VIR_ERR_NONE,
        /**
         * A simple warning
         */
        VIR_ERR_WARNING,
        /**
         * An error
         */
        VIR_ERR_ERROR,

        VIR_ERR_UNKNOWN; /* must be the last entry! */

        protected static final ErrorLevel wrap(int value) {
            return safeElementAt(value, values());
        }
    }

    public static enum ErrorNumber {
        VIR_ERR_OK,
        /** internal error */
        VIR_ERR_INTERNAL_ERROR,
        /** memory allocation failure */
        VIR_ERR_NO_MEMORY,
        /** no support for this function */
        VIR_ERR_NO_SUPPORT,
        /** could not resolve hostname */
        VIR_ERR_UNKNOWN_HOST,
        /** can't connect to hypervisor */
        VIR_ERR_NO_CONNECT,
        /** invalid connection object */
        VIR_ERR_INVALID_CONN,
        /** invalid domain object */
        VIR_ERR_INVALID_DOMAIN,
        /** invalid function argument */
        VIR_ERR_INVALID_ARG,
        /** a command to hypervisor failed */
        VIR_ERR_OPERATION_FAILED,
        /** a HTTP GET command to failed */
        VIR_ERR_GET_FAILED,
        /** a HTTP POST command to failed */
        VIR_ERR_POST_FAILED,
        /** unexpected HTTP error code */
        VIR_ERR_HTTP_ERROR,
        /** failure to serialize an S-Expr */
        VIR_ERR_SEXPR_SERIAL,
        /** could not open Xen hypervisor control */
        VIR_ERR_NO_XEN,
        /** failure doing an hypervisor call */
        VIR_ERR_XEN_CALL,
        /** unknown OS type */
        VIR_ERR_OS_TYPE,
        /** missing kernel information */
        VIR_ERR_NO_KERNEL,
        /** missing root device information */
        VIR_ERR_NO_ROOT,
        /** missing source device information */
        VIR_ERR_NO_SOURCE,
        /** missing target device information */
        VIR_ERR_NO_TARGET,
        /** missing domain name information */
        VIR_ERR_NO_NAME,
        /** missing domain OS information */
        VIR_ERR_NO_OS,
        /** missing domain devices information */
        VIR_ERR_NO_DEVICE,
        /** could not open Xen Store control */
        VIR_ERR_NO_XENSTORE,
        /** too many drivers registered */
        VIR_ERR_DRIVER_FULL,
        /** not supported by the drivers (DEPRECATED) */
        VIR_ERR_CALL_FAILED,
        /** an XML description is not well formed or broken */
        VIR_ERR_XML_ERROR,
        /** the domain already exist */
        VIR_ERR_DOM_EXIST,
        /** operation forbidden on read-only connections */
        VIR_ERR_OPERATION_DENIED,
        /** failed to open a conf file */
        VIR_ERR_OPEN_FAILED,
        /** failed to read a conf file */
        VIR_ERR_READ_FAILED,
        /** failed to parse a conf file */
        VIR_ERR_PARSE_FAILED,
        /** failed to parse the syntax of a conf file */
        VIR_ERR_CONF_SYNTAX,
        /** failed to write a conf file */
        VIR_ERR_WRITE_FAILED,
        /** detail of an XML error */
        VIR_ERR_XML_DETAIL,
        /** invalid network object */
        VIR_ERR_INVALID_NETWORK,
        /** the network already exist */
        VIR_ERR_NETWORK_EXIST,
        /** general system call failure */
        VIR_ERR_SYSTEM_ERROR,
        /** some sort of RPC error */
        VIR_ERR_RPC,
        /** error from a GNUTLS call */
        VIR_ERR_GNUTLS_ERROR,
        /** failed to start network */
        VIR_WAR_NO_NETWORK,
        /** domain not found or unexpectedly disappeared */
        VIR_ERR_NO_DOMAIN,
        /** network not found */
        VIR_ERR_NO_NETWORK,
        /** invalid MAC address */
        VIR_ERR_INVALID_MAC,
        /** authentication failed */
        VIR_ERR_AUTH_FAILED,
        /** invalid storage pool object */
        VIR_ERR_INVALID_STORAGE_POOL,
        /** invalid storage vol object */
        VIR_ERR_INVALID_STORAGE_VOL,
        /** failed to start storage */
        VIR_WAR_NO_STORAGE,
        /** storage pool not found */
        VIR_ERR_NO_STORAGE_POOL,
        /** storage pool not found */
        VIR_ERR_NO_STORAGE_VOL,
        /** failed to start node driver */
        VIR_WAR_NO_NODE,
        /** invalid node device object */
        VIR_ERR_INVALID_NODE_DEVICE,
        /** node device not found */
        VIR_ERR_NO_NODE_DEVICE,
        /** security model not found */
        VIR_ERR_NO_SECURITY_MODEL,
        /** operation is not applicable at this time */
        VIR_ERR_OPERATION_INVALID,
        /** failed to start interface driver */
        VIR_WAR_NO_INTERFACE,
        /** interface driver not running */
        VIR_ERR_NO_INTERFACE,
        /** invalid interface object */
        VIR_ERR_INVALID_INTERFACE,
        /** more than one matching interface found */
        VIR_ERR_MULTIPLE_INTERFACES,
        /** failed to start secret storage */
        VIR_WAR_NO_SECRET,
        /** invalid secret */
        VIR_ERR_INVALID_SECRET,
        /** secret not found */
        VIR_ERR_NO_SECRET,
        /** unsupported configuration construct */
        VIR_ERR_CONFIG_UNSUPPORTED,
        /** timeout occurred during operation */
        VIR_ERR_OPERATION_TIMEOUT,
        /** a migration worked, but making the VM persist on the dest
         *  host failed */
        VIR_ERR_MIGRATE_PERSIST_FAILED,
        /** a synchronous hook script failed */
        VIR_ERR_HOOK_SCRIPT_FAILED,
        /** invalid domain snapshot */
        VIR_ERR_INVALID_DOMAIN_SNAPSHOT,
        /** domain snapshot not found */
        VIR_ERR_NO_DOMAIN_SNAPSHOT,
        /** stream pointer not valid */
        VIR_ERR_INVALID_STREAM,
        /** valid API use but unsupported by the given driver */
        VIR_ERR_ARGUMENT_UNSUPPORTED,
        /** storage pool probe failed */
        VIR_ERR_STORAGE_PROBE_FAILED,
        /** storage pool already built */
        VIR_ERR_STORAGE_POOL_BUILT,
        /** force was not requested for a risky domain snapshot
            revert */
        VIR_ERR_SNAPSHOT_REVERT_RISKY,
        /** operation on a domain was canceled/aborted by user */
        VIR_ERR_OPERATION_ABORTED,
        /** authentication cancelled */
        VIR_ERR_AUTH_CANCELLED,
        /** The metadata is not present */
        VIR_ERR_NO_DOMAIN_METADATA,
        /** Migration is not safe */
        VIR_ERR_MIGRATE_UNSAFE,
        /** integer overflow */
        VIR_ERR_OVERFLOW,
        /** action prevented by block copy job */
        VIR_ERR_BLOCK_COPY_ACTIVE,

        /** unknown error */
        VIR_ERR_UNKNOWN; // must be the last entry!

        protected static final ErrorNumber wrap(int value) {
            return safeElementAt(value, values());
        }
    }

    /**
     *
     */
    private static final long serialVersionUID = -4780109197014633842L;

    private ErrorNumber code;
    private ErrorDomain domain;
    private String message;
    private ErrorLevel level;
    private ConnectionPointer VCP; /* Deprecated */
    private DomainPointer VDP; /* Deprecated */
    private String str1;
    private String str2;
    private String str3;
    private int int1;
    private int int2;
    private NetworkPointer VNP; /* Deprecated */

    public Error(virError vError) {
        code = ErrorNumber.wrap(vError.code);
        domain = ErrorDomain.wrap(vError.domain);
        level = ErrorLevel.wrap(vError.level);
        message = vError.message;
        str1 = vError.str1;
        str2 = vError.str2;
        str3 = vError.str3;
        int1 = vError.int1;
        int2 = vError.int2;
        VCP = vError.conn;
        VDP = vError.dom;
        VNP = vError.net;
    }

    /**
     * Gets the error code
     *
     * @return a VirErrorNumber
     */
    public ErrorNumber getCode() {
        return code;
    }

    /**
     * returns the Connection associated with the error, if available
     * Deprecated, always throw an exception now
     *
     * @return the Connect object
     * @throws ErrorException
     * @deprecated
     */
    @Deprecated
    public Connect getConn() throws ErrorException {
        throw new ErrorException("No Connect object available");
    }

    /**
     * returns the Domain associated with the error, if available
     *
     * @return Domain object
     * @throws ErrorException
     * @deprecated
     */
    @Deprecated
    public Domain getDom() throws ErrorException {
        throw new ErrorException("No Domain object available");
    }

    /**
     * Tells What part of the library raised this error
     *
     * @return a ErrorDomain
     */
    public ErrorDomain getDomain() {
        return domain;
    }

    /**
     * @return extra number information
     */
    public int getInt1() {
        return int1;
    }

    /**
     * @return extra number information
     */
    public int getInt2() {
        return int2;
    }

    /**
     * Tells how consequent is the error
     *
     * @return a ErrorLevel
     */
    public ErrorLevel getLevel() {
        return level;
    }

    /**
     * Returns a human-readable informative error message
     *
     * @return error message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns the network associated with the error, if available
     *
     * @return Network object
     * @throws ErrorException
     * @deprecated
     */
    @Deprecated
    public Network getNet() throws ErrorException {
        throw new ErrorException("No Network object available");
    }

    /**
     * @return extra string information
     */
    public String getStr1() {
        return str1;
    }

    /**
     * @return extra string information
     */
    public String getStr2() {
        return str2;
    }

    /**
     * @return extra string information
     */
    public String getStr3() {
        return str3;
    }

    /**
     * Does this error has a valid Connection object attached? NOTE: deprecated,
     * should return false
     *
     * @return false
     */
    public boolean hasConn() {
        return false;
    }

    /**
     * Does this error has a valid Domain object attached? NOTE: deprecated,
     * should return false
     *
     * @return false
     */
    public boolean hasDom() {
        return false;
    }

    /**
     * Does this error has a valid Network object attached? NOTE: deprecated,
     * should return false
     *
     * @return false
     */
    public boolean hasNet() {
        return false;
    }

    @Override
    public String toString() {
        return String.format("level:%s%ncode:%s%ndomain:%s%nhasConn:%b%nhasDom:%b%nhasNet:%b%nmessage:%s%nstr1:%s%nstr2:%s%nstr3:%s%nint1:%d%nint2:%d%n", level, code, domain, hasConn(), hasDom(), hasNet(), message, str1, str2, str3, int1, int2);
    }
}
