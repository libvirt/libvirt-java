package org.libvirt;

import java.io.Serializable;

import org.libvirt.jna.virError;

import com.sun.jna.Pointer;

/**
 * An error which is returned from libvirt,
 */
public class Error implements Serializable {

    public static enum ErrorDomain {
        VIR_FROM_NONE,
        /**
         * Error at Xen hypervisor layer
         */
        VIR_FROM_XEN,
        /**
         * Error at connection with xend daemon
         */
        VIR_FROM_XEND,
        /**
         * Error at connection with xen store
         */
        VIR_FROM_XENSTORE,
        /**
         * Error in the S-Epression code
         */
        VIR_FROM_SEXPR,
        /**
         * Error in the XML code
         */
        VIR_FROM_XML,
        /**
         * Error when operating on a domain
         */
        VIR_FROM_DOM,
        /**
         * Error in the XML-RPC code
         */
        VIR_FROM_RPC,
        /**
         * Error in the proxy code
         */
        VIR_FROM_PROXY,
        /**
         * Error in the configuration file handling
         */
        VIR_FROM_CONF,
        /**
         * Error at the QEMU daemon
         */
        VIR_FROM_QEMU,
        /**
         * Error when operating on a network
         */
        VIR_FROM_NET,
        /**
         * Error from test driver
         */
        VIR_FROM_TEST,
        /**
         * Error from remote driver
         */
        VIR_FROM_REMOTE,
        /**
         * Error from OpenVZ driver
         */
        VIR_FROM_OPENVZ,
        /**
         * Error at Xen XM layer
         */
        VIR_FROM_XENXM,
        /**
         * Error in the Linux Stats code
         */
        VIR_FROM_STATS_LINUX,
        /**
         * Error from Linux Container driver
         */
        VIR_FROM_LXC,
        /**
         * Error from storage driver
         */
        VIR_FROM_STORAGE
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
        VIR_ERR_OK,
        /**
         * internal error
         */
        VIR_ERR_INTERNAL_ERROR,
        /**
         * memory allocation failure
         */
        VIR_ERR_NO_MEMORY,
        /**
         * no support for this function
         */
        VIR_ERR_NO_SUPPORT,
        /**
         * could not resolve hostname
         */
        VIR_ERR_UNKNOWN_HOST,
        /**
         * can't connect to hypervisor
         */
        VIR_ERR_NO_CONNECT,
        /**
         * invalid connection object
         */
        VIR_ERR_INVALID_CONN,
        /**
         * invalid domain object
         */
        VIR_ERR_INVALID_DOMAIN,
        /**
         * invalid function argument
         */
        VIR_ERR_INVALID_ARG,
        /**
         * a command to hypervisor failed
         */
        VIR_ERR_OPERATION_FAILED,
        /**
         * a HTTP GET command to failed
         */
        VIR_ERR_GET_FAILED,
        /**
         * a HTTP POST command to failed
         */
        VIR_ERR_POST_FAILED,
        /**
         * unexpected HTTP error code
         */
        VIR_ERR_HTTP_ERROR,
        /**
         * failure to serialize an S-Expr
         */
        VIR_ERR_SEXPR_SERIAL,
        /**
         * could not open Xen hypervisor control
         */
        VIR_ERR_NO_XEN,
        /**
         * failure doing an hypervisor call
         */
        VIR_ERR_XEN_CALL,
        /**
         * unknown OS type
         */
        VIR_ERR_OS_TYPE,
        /**
         * missing kernel information
         */
        VIR_ERR_NO_KERNEL,
        /**
         * missing root device information
         */
        VIR_ERR_NO_ROOT,
        /**
         * missing source device information
         */
        VIR_ERR_NO_SOURCE,
        /**
         * missing target device information
         */
        VIR_ERR_NO_TARGET,
        /**
         * missing domain name information
         */
        VIR_ERR_NO_NAME,
        /**
         * missing domain OS information
         */
        VIR_ERR_NO_OS,
        /**
         * missing domain devices information
         */
        VIR_ERR_NO_DEVICE,
        /**
         * could not open Xen Store control
         */
        VIR_ERR_NO_XENSTORE,
        /**
         * too many drivers registered
         */
        VIR_ERR_DRIVER_FULL,
        /**
         * not supported by the drivers (DEPRECATED)
         */
        VIR_ERR_CALL_FAILED,
        /**
         * an XML description is not well formed or broken
         */
        VIR_ERR_XML_ERROR,
        /**
         * the domain already exist
         */
        VIR_ERR_DOM_EXIST,
        /**
         * operation forbidden on read-only connections
         */
        VIR_ERR_OPERATION_DENIED,
        /**
         * failed to open a conf file
         */
        VIR_ERR_OPEN_FAILED,
        /**
         * failed to read a conf file
         */
        VIR_ERR_READ_FAILED,
        /**
         * failed to parse a conf file
         */
        VIR_ERR_PARSE_FAILED,
        /**
         * failed to parse the syntax of a conf file
         */
        VIR_ERR_CONF_SYNTAX,
        /**
         * failed to write a conf file
         */
        VIR_ERR_WRITE_FAILED,
        /**
         * detail of an XML error
         */
        VIR_ERR_XML_DETAIL,
        /**
         * invalid network object
         */
        VIR_ERR_INVALID_NETWORK,
        /**
         * the network already exist
         */
        VIR_ERR_NETWORK_EXIST,
        /**
         * general system call failure
         */
        VIR_ERR_SYSTEM_ERROR,
        /**
         * some sort of RPC error
         */
        VIR_ERR_RPC,
        /**
         * error from a GNUTLS call
         */
        VIR_ERR_GNUTLS_ERROR,
        /**
         * failed to start network
         */
        VIR_WAR_NO_NETWORK,
        /**
         * omain not found or unexpectedly disappeared
         */
        VIR_ERR_NO_DOMAIN,
        /**
         * network not found
         */
        VIR_ERR_NO_NETWORK,
        /**
         * invalid MAC adress
         */
        VIR_ERR_INVALID_MAC,
        /**
         * authentication failed
         */
        VIR_ERR_AUTH_FAILED,
        /**
         * invalid storage pool object
         */
        VIR_ERR_INVALID_STORAGE_POOL,
        /**
         * invalid storage vol object
         */
        VIR_ERR_INVALID_STORAGE_VOL,
        /**
         * failed to start storage
         */
        VIR_WAR_NO_STORAGE,
        /**
         * storage pool not found
         */
        VIR_ERR_NO_STORAGE_POOL,
        /**
         * storage pool not found
         */
        VIR_ERR_NO_STORAGE_VOL
    }

    /**
     * 
     */
    private static final long serialVersionUID = -4780109197014633842L;

    ErrorNumber code;
    ErrorDomain domain;
    String message;
    ErrorLevel level;
    Pointer VCP; /* Deprecated */
    Pointer VDP; /* Deprecated */
    String str1;
    String str2;
    String str3;
    int int1;
    int int2;
    Pointer VNP; /* Deprecated */

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

    public String toString() {
        StringBuffer output = new StringBuffer();
        output.append("level:" + level + "\n");
        output.append("code:" + code + "\n");
        output.append("domain:" + domain + "\n");
        output.append("hasConn:" + hasConn() + "\n");
        output.append("hasDom:" + hasDom() + "\n");
        output.append("hasNet:" + hasNet() + "\n");
        output.append("message:" + message + "\n");
        output.append("str1:" + str1 + "\n");
        output.append("str2:" + str2 + "\n");
        output.append("str3:" + str3 + "\n");
        output.append("int1:" + int1 + "\n");
        output.append("int2:" + int2 + "\n");
        return output.toString();

    }
}
