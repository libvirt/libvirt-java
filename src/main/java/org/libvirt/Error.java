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

    public static enum ErrorDomain {
        VIR_FROM_NONE, VIR_FROM_XEN, /* Error at Xen hypervisor layer */
        VIR_FROM_XEND, /* Error at connection with xend daemon */
        VIR_FROM_XENSTORE, /* Error at connection with xen store */
        VIR_FROM_SEXPR, /* Error in the S-Expression code */
        VIR_FROM_XML, /* Error in the XML code */
        VIR_FROM_DOM, /* Error when operating on a domain */
        VIR_FROM_RPC, /* Error in the XML-RPC code */
        VIR_FROM_PROXY, /* Error in the proxy code */
        VIR_FROM_CONF, /* Error in the configuration file handling */
        VIR_FROM_QEMU, /* Error at the QEMU daemon */
        VIR_FROM_NET, /* Error when operating on a network */
        VIR_FROM_TEST, /* Error from test driver */
        VIR_FROM_REMOTE, /* Error from remote driver */
        VIR_FROM_OPENVZ, /* Error from OpenVZ driver */
        VIR_FROM_XENXM, /* Error at Xen XM layer */
        VIR_FROM_STATS_LINUX, /* Error in the Linux Stats code */
        VIR_FROM_LXC, /* Error from Linux Container driver */
        VIR_FROM_STORAGE, /* Error from storage driver */
        VIR_FROM_NETWORK, /* Error from network config */
        VIR_FROM_DOMAIN, /* Error from domain config */
        VIR_FROM_UML, /* Error at the UML driver */
        VIR_FROM_NODEDEV, /* Error from node device monitor */
        VIR_FROM_XEN_INOTIFY, /* Error from xen inotify layer */
        VIR_FROM_SECURITY, /* Error from security framework */
        VIR_FROM_VBOX, /* Error from VirtualBox driver */
        VIR_FROM_INTERFACE, /* Error when operating on an interface */
        VIR_FROM_ONE, /* Error from OpenNebula driver */
        VIR_FROM_ESX, /* Error from ESX driver */
        VIR_FROM_PHYP, /* Error from IBM power hypervisor */
        VIR_FROM_SECRET
        /* Error from secret storage */
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
        VIR_ERR_ERROR
    }

    public static enum ErrorNumber {
        VIR_ERR_OK, VIR_ERR_INTERNAL_ERROR, /* internal error */
        VIR_ERR_NO_MEMORY, /* memory allocation failure */
        VIR_ERR_NO_SUPPORT, /* no support for this function */
        VIR_ERR_UNKNOWN_HOST, /* could not resolve hostname */
        VIR_ERR_NO_CONNECT, /* can't connect to hypervisor */
        VIR_ERR_INVALID_CONN, /* invalid connection object */
        VIR_ERR_INVALID_DOMAIN, /* invalid domain object */
        VIR_ERR_INVALID_ARG, /* invalid function argument */
        VIR_ERR_OPERATION_FAILED, /* a command to hypervisor failed */
        VIR_ERR_GET_FAILED, /* a HTTP GET command to failed */
        VIR_ERR_POST_FAILED, /* a HTTP POST command to failed */
        VIR_ERR_HTTP_ERROR, /* unexpected HTTP error code */
        VIR_ERR_SEXPR_SERIAL, /* failure to serialize an S-Expr */
        VIR_ERR_NO_XEN, /* could not open Xen hypervisor control */
        VIR_ERR_XEN_CALL, /* failure doing an hypervisor call */
        VIR_ERR_OS_TYPE, /* unknown OS type */
        VIR_ERR_NO_KERNEL, /* missing kernel information */
        VIR_ERR_NO_ROOT, /* missing root device information */
        VIR_ERR_NO_SOURCE, /* missing source device information */
        VIR_ERR_NO_TARGET, /* missing target device information */
        VIR_ERR_NO_NAME, /* missing domain name information */
        VIR_ERR_NO_OS, /* missing domain OS information */
        VIR_ERR_NO_DEVICE, /* missing domain devices information */
        VIR_ERR_NO_XENSTORE, /* could not open Xen Store control */
        VIR_ERR_DRIVER_FULL, /* too many drivers registered */
        VIR_ERR_CALL_FAILED, /* not supported by the drivers (DEPRECATED) */
        VIR_ERR_XML_ERROR, /* an XML description is not well formed or broken */
        VIR_ERR_DOM_EXIST, /* the domain already exist */
        VIR_ERR_OPERATION_DENIED, /*
                                   * operation forbidden on read-only
                                   * connections
                                   */
        VIR_ERR_OPEN_FAILED, /* failed to open a conf file */
        VIR_ERR_READ_FAILED, /* failed to read a conf file */
        VIR_ERR_PARSE_FAILED, /* failed to parse a conf file */
        VIR_ERR_CONF_SYNTAX, /* failed to parse the syntax of a conf file */
        VIR_ERR_WRITE_FAILED, /* failed to write a conf file */
        VIR_ERR_XML_DETAIL, /* detail of an XML error */
        VIR_ERR_INVALID_NETWORK, /* invalid network object */
        VIR_ERR_NETWORK_EXIST, /* the network already exist */
        VIR_ERR_SYSTEM_ERROR, /* general system call failure */
        VIR_ERR_RPC, /* some sort of RPC error */
        VIR_ERR_GNUTLS_ERROR, /* error from a GNUTLS call */
        VIR_WAR_NO_NETWORK, /* failed to start network */
        VIR_ERR_NO_DOMAIN, /* domain not found or unexpectedly disappeared */
        VIR_ERR_NO_NETWORK, /* network not found */
        VIR_ERR_INVALID_MAC, /* invalid MAC address */
        VIR_ERR_AUTH_FAILED, /* authentication failed */
        VIR_ERR_INVALID_STORAGE_POOL, /* invalid storage pool object */
        VIR_ERR_INVALID_STORAGE_VOL, /* invalid storage vol object */
        VIR_WAR_NO_STORAGE, /* failed to start storage */
        VIR_ERR_NO_STORAGE_POOL, /* storage pool not found */
        VIR_ERR_NO_STORAGE_VOL, /* storage pool not found */
        VIR_WAR_NO_NODE, /* failed to start node driver */
        VIR_ERR_INVALID_NODE_DEVICE, /* invalid node device object */
        VIR_ERR_NO_NODE_DEVICE, /* node device not found */
        VIR_ERR_NO_SECURITY_MODEL, /* security model not found */
        VIR_ERR_OPERATION_INVALID, /* operation is not applicable at this time */
        VIR_WAR_NO_INTERFACE, /* failed to start interface driver */
        VIR_ERR_NO_INTERFACE, /* interface driver not running */
        VIR_ERR_INVALID_INTERFACE, /* invalid interface object */
        VIR_ERR_MULTIPLE_INTERFACES, /* more than one matching interface found */
        VIR_WAR_NO_SECRET, /* failed to start secret storage */
        VIR_ERR_INVALID_SECRET, /* invalid secret */
        VIR_ERR_NO_SECRET
        /* secret not found */

    }

    /**
     * 
     */
    private static final long serialVersionUID = -4780109197014633842L;

    ErrorNumber code;
    ErrorDomain domain;
    String message;
    ErrorLevel level;
    ConnectionPointer VCP; /* Deprecated */
    DomainPointer VDP; /* Deprecated */
    String str1;
    String str2;
    String str3;
    int int1;
    int int2;
    NetworkPointer VNP; /* Deprecated */

    public Error() {

    }

    public Error(virError vError) {
        code = ErrorNumber.values()[vError.code];
        domain = ErrorDomain.values()[vError.domain];
        level = ErrorLevel.values()[vError.level];
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
     * Gets he error code
     * 
     * @return a VirErroNumber
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
     * Returns human-readable informative error messag
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
