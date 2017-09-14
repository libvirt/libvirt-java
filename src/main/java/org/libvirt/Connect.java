package org.libvirt;

import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.ErrorHandler.processErrorIfZero;
import static org.libvirt.Library.getConstant;
import static org.libvirt.Library.libvirt;
import static org.libvirt.flags.BitFlagsHelper.OR;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.sun.jna.Memory;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.LongByReference;
import org.libvirt.event.BlockJobListener;
import org.libvirt.event.ConnectionCloseListener;
import org.libvirt.event.ConnectionCloseReason;
import org.libvirt.event.DomainEvent;
import org.libvirt.event.DomainEventType;
import org.libvirt.event.EventListener;
import org.libvirt.event.IOErrorAction;
import org.libvirt.event.IOErrorListener;
import org.libvirt.event.LifecycleListener;
import org.libvirt.event.PMSuspendListener;
import org.libvirt.event.PMSuspendReason;
import org.libvirt.event.PMWakeupListener;
import org.libvirt.event.PMWakeupReason;
import org.libvirt.event.RebootListener;
import org.libvirt.event.enums.ConnectDomainEventBlockJobStatus;
import org.libvirt.event.enums.DomainEventID;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.callbacks.VirConnectCloseFunc;
import org.libvirt.jna.callbacks.VirConnectDomainEventBlockJobCallback;
import org.libvirt.jna.callbacks.VirConnectDomainEventCallback;
import org.libvirt.jna.callbacks.VirConnectDomainEventGenericCallback;
import org.libvirt.jna.callbacks.VirConnectDomainEventIOErrorCallback;
import org.libvirt.jna.callbacks.VirConnectDomainEventPMChangeCallback;
import org.libvirt.jna.callbacks.VirDomainEventCallback;
import org.libvirt.jna.callbacks.VirErrorCallback;
import org.libvirt.jna.pointers.ConnectionPointer;
import org.libvirt.jna.pointers.DevicePointer;
import org.libvirt.jna.pointers.DomainPointer;
import org.libvirt.jna.pointers.InterfacePointer;
import org.libvirt.jna.pointers.NetworkFilterPointer;
import org.libvirt.jna.pointers.NetworkPointer;
import org.libvirt.jna.pointers.SecretPointer;
import org.libvirt.jna.pointers.StoragePoolPointer;
import org.libvirt.jna.pointers.StorageVolPointer;
import org.libvirt.jna.pointers.StreamPointer;
import org.libvirt.jna.structures.virConnectAuth;
import org.libvirt.jna.structures.virNodeInfo;
import org.libvirt.jna.structures.virSecurityModel;
import org.libvirt.jna.types.CString;

/**
 * The Connect object represents a connection to a local or remote
 * hypervisor/driver.
 *
 * @author stoty
 */
public class Connect {

    // registered event listeners by DomainEventID
    private Map<EventListener, RegisteredEventListener>[] eventListeners = makeHashMapArray(DomainEventID.SIZE);

    private static final class RegisteredEventListener {
        public final int callbackId;

        // We need to keep a reference to the callback to prevent it from being GCed
        @SuppressWarnings("unused")
        public final VirDomainEventCallback callback;

        public RegisteredEventListener(VirDomainEventCallback callback, int callbackId) {
            this.callback = callback;
            this.callbackId = callbackId;
        }
    }

    @SuppressWarnings("unchecked")
    private static <K, V> HashMap<K, V>[] makeHashMapArray(int size) {
        return new HashMap[size];
    }

    private class CloseFunc implements VirConnectCloseFunc {
        final ConnectionCloseListener listener;

        CloseFunc(ConnectionCloseListener l) {
            this.listener = l;
        }

        @Override
        public void callback(ConnectionPointer VCP, int reason, Pointer opaque) {
            this.listener.onClose(Connect.this,
                    getConstant(ConnectionCloseReason.class, reason));
        }
    }

    private CloseFunc registeredCloseFunc = null;

    public enum OpenFlags implements BitFlags {
        /**
         * Open a connection in read-only mode
         */
        READONLY(1),

        /**
         * Don't try to resolve URI aliases
         */
        NO_ALIASES(2);

        OpenFlags(int v) {
            this.value = v;
        }

        @Override
        public int getBit() {
            return value;
        }

        private final int value;
    }

    /**
     * Get the version of a connection.
     *
     * @param conn the connection to use.
     * @return -1 in case of failure, versions have the format major * 1,000,000 + minor * 1,000 + release.
     * @deprecated Use {@link #getLibVersion} instead.
     */
    @Deprecated
    public static long connectionVersion(Connect conn) {
        LongByReference libVer = new LongByReference();
        int result = Libvirt.INSTANCE.virConnectGetLibVersion(conn.VCP, libVer);
        return result != -1 ? libVer.getValue() : -1;
    }

    /**
     * Helper function to convert bytes into ints for the UUID calls
     */
    public static int[] convertUUIDBytes(byte bytes[]) {
        int[] returnValue = new int[Libvirt.VIR_UUID_BUFLEN];
        for (int x = 0; x < Libvirt.VIR_UUID_BUFLEN; x++) {
            // For some reason, the higher bytes come back wierd.
            // We only want the lower 2 bytes.
            returnValue[x] = (bytes[x] & 255);
        }
        return returnValue;
    }

    /**
     * Helper function to convert UUIDs into a stirng for the UUID calls
     */
    public static byte[] createUUIDBytes(int[] UUID) {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        for (int x = 0; x < Libvirt.VIR_UUID_BUFLEN; x++) {
            bytes[x] = (byte) UUID[x];
        }
        return bytes;
    }

    /**
     * Get the libvirt library version of this connection.
     *
     * @return The version of libvirt used by the daemon running on
     * the connected host in the format {@code major * 1,000,000 + minor * 1,000 + release}.
     */
    public long getLibVersion() throws LibvirtException {
        LongByReference libVer = new LongByReference();
        processError(libvirt.virConnectGetLibVersion(this.VCP, libVer));
        return libVer.getValue();
    }

    /**
     * Sets the error function to a user defined callback
     *
     * @param callback a Class to perform the callback
     */
    public static void setErrorCallback(VirErrorCallback callback) throws LibvirtException {
        Libvirt.INSTANCE.virSetErrorFunc(null, callback);
    }

    /**
     * The native virConnectPtr.
     */
    protected ConnectionPointer VCP;

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        try {
            result = prime * result + ((VCP == null) ? 0 : this.getURI().hashCode());
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error testing connection equality", e);
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
        if (!(obj instanceof Connect)) {
            return false;
        }
        Connect other = (Connect) obj;
        if (VCP == null) {
            return (other.VCP == null);
        } else if (VCP.equals(other.VCP)) {
            return true;
        }

        try {
            return getURI().equals(other.getURI());
        } catch (LibvirtException e) {
            throw new RuntimeException("libvirt error testing connect equality", e);
        }
    }

    /**
     * Protected constructor to return a Connection with ConnectionPointer
     */
    Connect(ConnectionPointer ptr) {
        VCP = ptr;
    }

    /**
     * Construct a Connect object from a known native virConnectPtr For use when
     * native libvirt returns a virConnectPtr, i.e. error handling.
     *
     * @param VCP the virConnectPtr pointing to an existing native virConnect
     *            structure
     */
    @Deprecated
    Connect(long VCP) {
        throw new RuntimeException("No longer supported");
    }

    /**
     * Constructs a read-write Connect object from the supplied URI.
     *
     * @param uri The connection URI
     * @throws LibvirtException
     */
    public Connect(String uri) throws LibvirtException {
        this(uri, null, 0);
    }

    /**
     * Constructs a read-write Connect object from the supplied URI.
     *
     * @param uri The connection URI
     * @throws LibvirtException
     */
    public Connect(URI uri, OpenFlags... flags) throws LibvirtException {
        this(uri, null, flags);
    }

    /**
     * Constructs a Connect object from the supplied URI.
     *
     * @param uri      The connection URI
     * @param readOnly Whether the connection is read-only
     * @throws LibvirtException
     */
    public Connect(String uri, boolean readOnly) throws LibvirtException {
        this(uri, null, readOnly ? OpenFlags.READONLY.getBit() : 0);
    }

    /**
     * Constructs a Connect object from the supplied URI, using the supplied
     * authentication callback
     *
     * @param uri   The connection URI
     * @param auth  a ConnectAuth object
     * @param flags
     * @throws LibvirtException
     */
    public Connect(String uri, ConnectAuth auth, int flags) throws LibvirtException {
        virConnectAuth vAuth = null;

        if (auth != null) {
            vAuth = new virConnectAuth();
            vAuth.cb = auth;
            vAuth.cbdata = null;
            vAuth.ncredtype = auth.credType.length;
            int[] authInts = new int[vAuth.ncredtype];

            for (int x = 0; x < vAuth.ncredtype; x++) {
                authInts[x] = auth.credType[x].mapToInt();
            }

            Memory mem = new Memory(4 * vAuth.ncredtype);
            mem.write(0, authInts, 0, vAuth.ncredtype);
            vAuth.credtype = mem.share(0);
        }

        VCP = libvirt.virConnectOpenAuth(uri, vAuth, flags);
        // Check for an error
        processError(VCP);
    }

    /**
     * Constructs a Connect object from the supplied URI, using the supplied
     * authentication callback
     *
     * @param uri   The connection URI
     * @param auth  a ConnectAuth object
     * @param flags
     * @throws LibvirtException
     */
    public Connect(URI uri, ConnectAuth auth, OpenFlags... flags) throws LibvirtException {
        this(uri.toString(), auth, OR(flags));
    }

    /**
     * Constructs a Connect object from the supplied URI, using the supplied
     * authentication callback
     *
     * @param uri  The connection URI
     * @param auth a ConnectAuth object
     * @throws LibvirtException
     */
    public Connect(URI uri, ConnectAuth auth) throws LibvirtException {
        this(uri.toString(), auth, 0);
    }

    /**
     * Computes the most feature-rich CPU which is compatible with all given
     * host CPUs.
     *
     * @param xmlCPUs array of XML descriptions of host CPUs
     * @return XML description of the computed CPU or NULL on error.
     * @throws LibvirtException
     */
    public String baselineCPU(String[] xmlCPUs) throws LibvirtException {
        CString result = libvirt.virConnectBaselineCPU(VCP, xmlCPUs, xmlCPUs.length, 0);
        return processError(result).toString();
    }

    /**
     * Closes the connection to the hypervisor/driver. Calling any methods on
     * the object after close() will result in an exception.
     *
     * @return number of remaining references (>= 0)
     * @throws LibvirtException
     */
    public int close() throws LibvirtException {
        int success = 0;
        if (VCP != null) {
            success = libvirt.virConnectClose(VCP);

            // if the connection has been closed (i.e. the reference count is
            // down to zero), forget about the registered close function
            if (success == 0) {
                registeredCloseFunc = null;
            }

            // If leave an invalid pointer dangling around JVM crashes and burns
            // if someone tries to call a method on us
            // We rely on the underlying libvirt error handling to detect that
            // it's called with a null virConnectPointer
            VCP = null;
        }
        return processError(success);
    }

    /**
     * Compares the given CPU description with the host CPU
     *
     * @param xmlDesc
     * @return comparison result according to enum CPUCompareResult
     * @throws LibvirtException
     */
    public CPUCompareResult compareCPU(String xmlDesc) throws LibvirtException {
        int rawResult = processError(libvirt.virConnectCompareCPU(VCP, xmlDesc, 0));
        return CPUCompareResult.get(rawResult);
    }

    /**
     * Create a new device on the VM host machine, for example, virtual HBAs
     * created using vport_create.
     *
     * @param xmlDesc the device to create
     * @return the Device object
     * @throws LibvirtException
     */
    public Device deviceCreateXML(String xmlDesc) throws LibvirtException {
        DevicePointer ptr = processError(libvirt.virNodeDeviceCreateXML(VCP, xmlDesc, 0));
        return new Device(this, ptr);
    }

    /**
     * Fetch a device based on its unique name
     *
     * @param name name of device to fetch
     * @return Device object
     * @throws LibvirtException
     */
    public Device deviceLookupByName(String name) throws LibvirtException {
        DevicePointer ptr = processError(libvirt.virNodeDeviceLookupByName(VCP, name));
        return new Device(this, ptr);
    }

    /**
     * Launches a new Linux guest domain. The domain is based on an XML
     * description similar to the one returned by virDomainGetXMLDesc(). This
     * function may require priviledged access to the hypervisor.
     *
     * @param xmlDesc the Domain description in XML
     * @param flags   an optional set of flags (unused)
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainCreateLinux(String xmlDesc, int flags) throws LibvirtException {
        DomainPointer ptr = processError(libvirt.virDomainCreateLinux(VCP, xmlDesc, flags));

        return new Domain(this, ptr);
    }

    /**
     * Launch a new guest domain, based on an XML description
     *
     * @param xmlDesc
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainCreateXML(String xmlDesc, int flags) throws LibvirtException {
        DomainPointer ptr = processError(libvirt.virDomainCreateXML(VCP, xmlDesc, flags));
        return new Domain(this, ptr);
    }

    /**
     * Defines a domain, but does not start it
     *
     * @param xmlDesc
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainDefineXML(String xmlDesc) throws LibvirtException {
        DomainPointer ptr = processError(libvirt.virDomainDefineXML(VCP, xmlDesc));
        return new Domain(this, ptr);
    }

    /**
     * Removes the event listener for the given eventID parameter so
     * that it no longer receives events.
     *
     * @param eventID the domain event identifier
     * @param l       the event listener
     * @throws LibvirtException
     */
    private void domainEventDeregister(int eventID, EventListener l) throws LibvirtException {
        if (l == null) {
            return;
        }

        Map<EventListener, RegisteredEventListener> handlers = eventListeners[eventID];

        if (handlers == null) {
            return;
        }

        RegisteredEventListener listener = handlers.remove(l);

        if (listener != null) {
            processError(libvirt.virConnectDomainEventDeregisterAny(VCP, listener.callbackId));
        }
    }

    private void domainEventRegister(Domain domain, int eventID, VirDomainEventCallback cb, EventListener l) throws LibvirtException {
        Map<EventListener, RegisteredEventListener> handlers = eventListeners[eventID];

        if (handlers == null) {
            handlers = new HashMap<>();
            eventListeners[eventID] = handlers;
        } else if (handlers.containsKey(l)) {
            return;
        }

        DomainPointer ptr = domain == null ? null : domain.VDP;
        int ret = processError(libvirt.virConnectDomainEventRegisterAny(VCP, ptr, eventID, cb, null, null));

        // track the handler
        // Note: it is important that the callback does not get GCed
        handlers.put(l, new RegisteredEventListener(cb, ret));
    }

    void domainEventRegister(Domain domain, final IOErrorListener cb) throws LibvirtException {
        if (cb == null) {
            throw new IllegalArgumentException("IOError callback cannot be null");
        }

        VirConnectDomainEventIOErrorCallback virCB = new VirConnectDomainEventIOErrorCallback() {
            @Override
            public void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer,
                                      String srcPath,
                                      String devAlias,
                                      int action,
                                      Pointer opaque) {
                assert VCP.equals(virConnectPtr);

                try {
                    Domain d = Domain.constructIncRef(Connect.this, virDomainPointer);
                    cb.onIOError(d,
                            srcPath,
                            devAlias,
                            getConstant(IOErrorAction.class, action));
                } catch (LibvirtException e) {
                    throw new RuntimeException("libvirt error in IOError callback", e);
                }
            }
        };

        domainEventRegister(domain, DomainEventID.VIR_DOMAIN_EVENT_ID_IO_ERROR.getValue(), virCB, cb);
    }

    void domainEventRegister(Domain domain, final RebootListener cb) throws LibvirtException {
        if (cb == null) {
            throw new IllegalArgumentException("RebootCallback cannot be null");
        }

        VirConnectDomainEventGenericCallback virCB = new VirConnectDomainEventGenericCallback() {
            @Override
            public void eventCallback(ConnectionPointer virConnectPtr,
                                      DomainPointer virDomainPointer,
                                      Pointer opaque) {
                assert VCP.equals(virConnectPtr);

                try {
                    Domain d = Domain.constructIncRef(Connect.this, virDomainPointer);
                    cb.onReboot(d);
                } catch (LibvirtException e) {
                    throw new RuntimeException("libvirt error in reboot callback", e);
                }
            }
        };

        domainEventRegister(domain, DomainEventID.VIR_DOMAIN_EVENT_ID_REBOOT.getValue(), virCB, cb);
    }

    void domainEventRegister(Domain domain, final LifecycleListener cb) throws LibvirtException {
        if (cb == null) {
            throw new IllegalArgumentException("LifecycleCallback cannot be null");
        }

        VirConnectDomainEventCallback virCB = new VirConnectDomainEventCallback() {
            @Override
            public int eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer,
                                     final int eventCode,
                                     final int detailCode,
                                     Pointer opaque) {
                assert VCP.equals(virConnectPtr);

                try {
                    Domain dom = Domain.constructIncRef(Connect.this, virDomainPointer);
                    DomainEventType type = getConstant(DomainEventType.class, eventCode);
                    DomainEvent event = new DomainEvent(type, detailCode);

                    cb.onLifecycleChange(dom, event);
                } catch (LibvirtException e) {
                    throw new RuntimeException("libvirt error in lifecycle callback", e);
                }

                // always return 0, regardless of what the
                // callback method returned. This may need to be
                // changed in the future, in case the return value
                // is used for something by libvirt.
                return 0;
            }
        };

        domainEventRegister(domain, DomainEventID.VIR_DOMAIN_EVENT_ID_LIFECYCLE.getValue(), virCB, cb);
    }

    void domainEventRegister(Domain domain, final BlockJobListener l) throws LibvirtException {
        if (l == null) {
            throw new IllegalArgumentException("BlockJobListener cannot be null");
        }

        VirConnectDomainEventBlockJobCallback virCB = (virConnectPtr, virDomainPointer, disk, type, status, opaque) -> {
            assert VCP.equals(virConnectPtr);

            try {
                Domain d = Domain.constructIncRef(Connect.this, virDomainPointer);

                switch (ConnectDomainEventBlockJobStatus.valueOf(status)) {
                    case VIR_DOMAIN_BLOCK_JOB_COMPLETED:
                        l.onBlockJobCompleted(d, disk, type);
                    case VIR_DOMAIN_BLOCK_JOB_FAILED:
                        l.onBlockJobFailed(d, disk, type);
                    case VIR_DOMAIN_BLOCK_JOB_CANCELED:
                        l.onBlockJobCanceled(d, disk, type);
                    case VIR_DOMAIN_BLOCK_JOB_READY:
                        l.onBlockJobReady(d, disk, type);
                }
            } catch (LibvirtException e) {
                throw new RuntimeException("libvirt error in block job callback", e);
            }
        };

        domainEventRegister(domain, DomainEventID.VIR_DOMAIN_EVENT_ID_BLOCK_JOB.getValue(), virCB, l);
    }

    void domainEventRegister(Domain domain, final PMWakeupListener cb) throws LibvirtException {
        if (cb == null) {
            throw new IllegalArgumentException("PMWakeupCallback cannot be null");
        }

        VirDomainEventCallback virCB =
                new VirConnectDomainEventPMChangeCallback() {
                    @Override
                    public void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer,
                                              int reason, Pointer opaque) {
                        assert VCP.equals(virConnectPtr);

                        try {
                            Domain d = Domain.constructIncRef(Connect.this, virDomainPointer);
                            cb.onPMWakeup(d, getConstant(PMWakeupReason.class, reason));
                        } catch (LibvirtException e) {
                            throw new RuntimeException("libvirt error handling PMWakeup callback", e);
                        }
                    }
                };

        domainEventRegister(domain, DomainEventID.VIR_DOMAIN_EVENT_ID_PMWAKEUP.getValue(), virCB, cb);
    }

    void domainEventRegister(Domain domain, final PMSuspendListener cb) throws LibvirtException {
        if (cb == null) {
            throw new IllegalArgumentException("PMSuspendCallback cannot be null");
        }

        VirDomainEventCallback virCB =
                new VirConnectDomainEventPMChangeCallback() {
                    @Override
                    public void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer,
                                              int reason, Pointer opaque) {
                        assert VCP.equals(virConnectPtr);

                        try {
                            Domain d = Domain.constructIncRef(Connect.this, virDomainPointer);
                            cb.onPMSuspend(d, getConstant(PMSuspendReason.class, reason));
                        } catch (LibvirtException e) {
                            throw new RuntimeException("libvirt error in PMSuspend callback", e);
                        }
                    }
                };

        domainEventRegister(domain, DomainEventID.VIR_DOMAIN_EVENT_ID_PMSUSPEND.getValue(), virCB, cb);
    }

    /**
     * Register the specified connection close listener to receive notifications
     * when this connection is closed.
     * <p>
     * <strong>Note:</strong> There can only be at most one registered listener
     * at a time.
     *
     * @param l the connection close listener
     * @throws LibvirtException on failure
     * @see #unregisterCloseListener
     */
    public void registerCloseListener(final ConnectionCloseListener l) throws LibvirtException {
        CloseFunc cf = new CloseFunc(l);

        processError(libvirt.virConnectRegisterCloseCallback(this.VCP,
                cf,
                null,
                null));
        this.registeredCloseFunc = cf;
    }

    /**
     * Unregister the previously registered close listener.
     * <p>
     * When there currently is no registered close listener, this method
     * does nothing.
     *
     * @see #registerCloseListener
     */
    public void unregisterCloseListener() throws LibvirtException {
        if (this.registeredCloseFunc != null) {
            processError(libvirt.virConnectUnregisterCloseCallback(this.VCP,
                    this.registeredCloseFunc));
            this.registeredCloseFunc = null;
        }
    }

    /**
     * Adds the specified I/O error listener to receive I/O error events
     * for domains of this connection.
     *
     * @param l the I/O error listener
     * @throws LibvirtException on failure
     */
    public void addIOErrorListener(final IOErrorListener l) throws LibvirtException {
        domainEventRegister(null, l);
    }

    /**
     * Adds the specified listener to receive lifecycle events for
     * domains of this connection.
     *
     * @param l the lifecycle listener
     * @throws LibvirtException on failure
     * @see #removeLifecycleListener
     * @see Domain#addLifecycleListener
     */
    public void addLifecycleListener(final LifecycleListener l) throws LibvirtException {
        domainEventRegister(null, l);
    }

    /**
     * Removes the specified lifecycle event listener so that it no longer
     * receives lifecycle events.
     *
     * @param l the lifecycle event listener
     * @throws LibvirtException
     */
    public void removeLifecycleListener(LifecycleListener l) throws LibvirtException {
        domainEventDeregister(DomainEventID.VIR_DOMAIN_EVENT_ID_LIFECYCLE.getValue(), l);
    }

    /**
     * Adds the specified listener to receive blockjob events for
     * domains of this connection.
     *
     * @param l the blockjob listener
     * @throws LibvirtException on failure
     * @see #removeBlockJobListener
     * @see Domain#addBlockJobListener
     */
    public void addBlockJobListener(final BlockJobListener l) throws LibvirtException {
        domainEventRegister(null, l);
    }

    /**
     * Removes the specified lifblockjobecycle event listener so that it no longer
     * receives blockjob events.
     *
     * @param l the blockjob event listener
     * @throws LibvirtException
     */
    public void removeBlockJobListener(BlockJobListener l) throws LibvirtException {
        domainEventDeregister(DomainEventID.VIR_DOMAIN_EVENT_ID_BLOCK_JOB.getValue(), l);
    }

    /**
     * Adds the specified listener to receive PMSuspend events for
     * domains of this connection.
     *
     * @param l the PMSuspend listener
     * @throws LibvirtException on failure
     * @see #removePMSuspendListener
     * @see Domain#addPMSuspendListener
     * @since 1.5.2
     */
    public void addPMSuspendListener(final PMSuspendListener l) throws LibvirtException {
        domainEventRegister(null, l);
    }

    /**
     * Removes the specified PMSuspend listener so that it no longer
     * receives PMSuspend events.
     *
     * @param l the PMSuspend listener
     * @throws LibvirtException
     * @since 1.5.2
     */
    public void removePMSuspendListener(final PMSuspendListener l) throws LibvirtException {
        domainEventDeregister(DomainEventID.VIR_DOMAIN_EVENT_ID_PMWAKEUP.getValue(), l);
    }

    /**
     * Adds the specified listener to receive PMWakeup events for
     * domains of this connection.
     *
     * @param l the PMWakeup listener
     * @throws LibvirtException on failure
     * @see #removePMWakeupListener
     * @see Domain#addPMWakeupListener
     * @since 1.5.2
     */
    public void addPMWakeupListener(final PMWakeupListener l) throws LibvirtException {
        domainEventRegister(null, l);
    }

    /**
     * Removes the specified PMWakeup listener so that it no longer
     * receives PMWakeup events.
     *
     * @param l the PMWakeup listener
     * @throws LibvirtException
     */
    public void removePMWakeupListener(final PMWakeupListener l) throws LibvirtException {
        domainEventDeregister(DomainEventID.VIR_DOMAIN_EVENT_ID_PMWAKEUP.getValue(), l);
    }

    /**
     * Adds the specified reboot listener to receive reboot events for
     * domains of this connection.
     *
     * @param l the reboot listener
     * @throws LibvirtException on failure
     * @see Domain#addRebootListener
     * @since 1.5.2
     */
    public void addRebootListener(final RebootListener l) throws LibvirtException {
        domainEventRegister(null, l);
    }

    /**
     * Removes the specified I/O error listener so that it no longer
     * receives I/O error events.
     *
     * @param l the I/O error listener
     * @throws LibvirtException
     */
    public void removeIOErrorListener(IOErrorListener l) throws LibvirtException {
        domainEventDeregister(DomainEventID.VIR_DOMAIN_EVENT_ID_IO_ERROR.getValue(), l);
    }

    /**
     * Finds a domain based on the hypervisor ID number.
     *
     * @param id the hypervisor id
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByID(int id) throws LibvirtException {
        DomainPointer ptr = processError(libvirt.virDomainLookupByID(VCP, id));
        return new Domain(this, ptr);
    }

    /**
     * Looks up a domain based on its name.
     *
     * @param name the name of the domain
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByName(String name) throws LibvirtException {
        DomainPointer ptr = processError(libvirt.virDomainLookupByName(VCP, name));
        return new Domain(this, ptr);
    }

    /**
     * Looks up a domain based on its UUID in array form. The UUID Array
     * contains an unpacked representation of the UUID, each int contains only
     * one byte.
     *
     * @param UUID the UUID as an unpacked int array
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        DomainPointer ptr = processError(libvirt.virDomainLookupByUUID(VCP, uuidBytes));
        return new Domain(this, ptr);
    }

    /**
     * Fetch a domain based on its globally unique id
     *
     * @param uuid a java UUID
     * @return a new domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByUUID(UUID uuid) throws LibvirtException {
        return domainLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a domain based on its UUID in String form.
     *
     * @param UUID the UUID in canonical String representation
     * @return the Domain object
     * @throws LibvirtException
     */
    public Domain domainLookupByUUIDString(String UUID) throws LibvirtException {
        DomainPointer ptr = processError(libvirt.virDomainLookupByUUIDString(VCP, UUID));
        return new Domain(this, ptr);
    }

    /**
     * Reads a native XML configuration document, and generates generates a
     * domain configuration file describing the domain. The format of the native
     * data is hypervisor dependant.
     *
     * @return domain XML as String, or {@code null} on error
     * @throws LibvirtException
     */
    public String domainXMLFromNative(String nativeFormat, String nativeConfig, int flags) throws LibvirtException {
        CString result = libvirt.virConnectDomainXMLFromNative(VCP, nativeFormat, nativeConfig, 0);
        return processError(result).toString();
    }

    /**
     * Reads a domain XML configuration document, and generates generates a
     * native configuration file describing the domain. The format of the native
     * data is hypervisor dependant.
     *
     * @return domain XML as String, or {@code null} on error
     * @throws LibvirtException
     */
    public String domainXMLToNative(String nativeFormat, String domainXML, int flags) throws LibvirtException {
        CString returnValue = libvirt.virConnectDomainXMLToNative(VCP, nativeFormat, domainXML, 0);
        return processError(returnValue).toString();
    }

    @Override
    protected void finalize() throws LibvirtException {
        close();
    }

    /**
     * Talks to a storage backend and attempts to auto-discover the set of
     * available storage pool sources. e.g. For iSCSI this would be a set of
     * iSCSI targets. For NFS this would be a list of exported paths. The
     * srcSpec (optional for some storage pool types, e.g. local ones) is an
     * instance of the storage pool&apos;s source element specifying where to
     * look for the pools. srcSpec is not required for some types (e.g., those
     * querying local storage resources only)
     *
     * @param type     type of storage pool to discover
     * @param srcSpecs XML document specifying discovery sourc
     * @param flags    unused
     * @return an xml document consisting of a SourceList element containing a
     * source document appropriate to the given pool type for each
     * discovered source.
     * @throws LibvirtException
     */
    public String findStoragePoolSources(String type, String srcSpecs, int flags) throws LibvirtException {
        CString returnValue = libvirt.virConnectFindStoragePoolSources(VCP, type, srcSpecs, flags);
        return processError(returnValue).toString();
    }

    /**
     * Provides capabilities of the hypervisor / driver.
     *
     * @return an XML String describing the capabilities.
     * @throws LibvirtException
     */
    public String getCapabilities() throws LibvirtException {
        return processError(libvirt.virConnectGetCapabilities(VCP)).toString();
    }

    /**
     * NUMA Support
     */
    public long getCellsFreeMemory(int startCells, int maxCells) throws LibvirtException {
        LongByReference returnValue = new LongByReference();
        processError(libvirt.virNodeGetCellsFreeMemory(VCP,
                returnValue,
                startCells,
                maxCells));
        return returnValue.getValue();
    }

    /**
     * Returns the free memory for the connection
     */
    public long getFreeMemory() throws LibvirtException {
        return processErrorIfZero(libvirt.virNodeGetFreeMemory(VCP));
    }

    /**
     * Returns the system hostname on which the hypervisor is running. (the
     * result of the gethostname(2) system call) If we are connected to a remote
     * system, then this returns the hostname of the remote system.
     *
     * @return the hostname
     * @throws LibvirtException
     */
    public String getHostName() throws LibvirtException {
        return processError(libvirt.virConnectGetHostname(VCP)).toString();
    }

    /**
     * Returns the version of the hypervisor against which the library was
     * compiled.
     * <p>
     * Since libvirt 0.9.3 this simply returns the same version number
     * as {@link Library#getVersion}.
     *
     * @param type The type of connection/driver to look at. See
     *             {@link #getType()}. May be {@code null}.
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     * @deprecated To get the version of the running hypervisor use
     * {@link #getVersion()} instead.
     */
    @Deprecated
    public long getHypervisorVersion(String type) throws LibvirtException {
        LongByReference libVer = new LongByReference();
        LongByReference typeVer = new LongByReference();
        processError(libvirt.virGetVersion(libVer, type, typeVer));
        return libVer.getValue();
    }

    /**
     * Gets the version of the native libvirt library that the JNI part is
     * linked to.
     *
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     * @deprecated use {@link Library#getVersion} instead
     */
    @Deprecated
    public long getLibVirVersion() throws LibvirtException {
        LongByReference libVer = new LongByReference();
        processError(libvirt.virGetVersion(libVer, null, null));
        return libVer.getValue();
    }

    /**
     * Provides the maximum number of virtual CPUs supported for a guest VM of a
     * specific type. The 'type' parameter here corresponds to the 'type'
     * attribute in the <domain> element of the XML.
     *
     * @param type
     * @return the number of CPUs
     * @throws LibvirtException
     */
    public int getMaxVcpus(String type) throws LibvirtException {
        return processError(libvirt.virConnectGetMaxVcpus(VCP, type));
    }

    /**
     * Returns the security model of the connected node.
     */
    public SecurityModel getSecurityModel() throws LibvirtException {
        virSecurityModel secmodel = new virSecurityModel();

        processError(libvirt.virNodeGetSecurityModel(this.VCP, secmodel));

        if (secmodel.model[0] == 0) {
            return null;
        } else {
            return new SecurityModel(secmodel);
        }
    }

    /**
     * Returns the XML description of the sysinfo details for the host
     * on which the hypervisor is running.
     * <p>
     * This information is generally available only for hypervisors
     * running with root privileges.
     *
     * @return sysinfo details in the same format as the {@code
     * <sysinfo>} element of a domain XML.
     * @since 1.5.2
     */
    public String getSysinfo() throws LibvirtException {
        return processError(libvirt.virConnectGetSysinfo(this.VCP, 0)).toString();
    }

    /**
     * Gets the name of the Hypervisor software used.
     *
     * @return the name
     * @throws LibvirtException
     */
    public String getType() throws LibvirtException {
        return processError(libvirt.virConnectGetType(VCP));
    }

    /**
     * Returns the URI (name) of the hypervisor connection. Normally this is the
     * same as or similar to the string passed to the
     * virConnectOpen/virConnectOpenReadOnly call, but the driver may make the
     * URI canonical.
     *
     * @return the URI
     * @throws LibvirtException
     */
    public String getURI() throws LibvirtException {
        return processError(libvirt.virConnectGetURI(VCP)).toString();
    }

    /**
     * Gets the version level of the Hypervisor running. This may work only with
     * hypervisor call, i.e. with priviledged access to the hypervisor, not with
     * a Read-Only connection. If the version can't be extracted by lack of
     * capacities returns 0.
     *
     * @return major * 1,000,000 + minor * 1,000 + release
     * @throws LibvirtException
     */
    public long getVersion() throws LibvirtException {
        LongByReference hvVer = new LongByReference();
        processError(libvirt.virConnectGetVersion(VCP, hvVer));
        return hvVer.getValue();
    }

    /**
     * Define an interface (or modify existing interface configuration)
     *
     * @param xmlDesc the interface to create
     * @return the Interface object
     * @throws LibvirtException
     */
    public Interface interfaceDefineXML(String xmlDesc) throws LibvirtException {
        InterfacePointer ptr = processError(libvirt.virInterfaceDefineXML(VCP, xmlDesc, 0));
        return new Interface(this, ptr);
    }

    /**
     * Try to lookup an interface on the given hypervisor based on its MAC.
     *
     * @throws LibvirtException
     */
    public Interface interfaceLookupByMACString(String mac) throws LibvirtException {
        InterfacePointer ptr = processError(libvirt.virInterfaceLookupByMACString(VCP, mac));
        return new Interface(this, ptr);
    }

    /**
     * Try to lookup an interface on the given hypervisor based on its name.
     *
     * @throws LibvirtException
     */
    public Interface interfaceLookupByName(String name) throws LibvirtException {
        InterfacePointer ptr = processError(libvirt.virInterfaceLookupByName(VCP, name));
        return new Interface(this, ptr);
    }

    /**
     * Determine if the connection is encrypted
     *
     * @return 1 if encrypted, 0 if not encrypted
     * @throws LibvirtException
     */
    public int isEncrypted() throws LibvirtException {
        return processError(libvirt.virConnectIsEncrypted(VCP));
    }

    /**
     * Determine if the connection is secure
     *
     * @return 1 if secure, 0 if not secure
     * @throws LibvirtException
     */
    public int isSecure() throws LibvirtException {
        return processError(libvirt.virConnectIsSecure(VCP));
    }

    /**
     * Lists the names of the defined but inactive domains
     *
     * @return an Array of Strings that contains the names of the defined
     * domains currently inactive
     * @throws LibvirtException
     */
    public String[] listDefinedDomains() throws LibvirtException {
        int maxnames = numOfDefinedDomains();
        if (maxnames > 0) {
            final CString[] names = new CString[maxnames];
            final int n = processError(libvirt.virConnectListDefinedDomains(VCP, names, maxnames));
            return Library.toStringArray(names, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Provides the list of names of defined interfaces on this host
     *
     * @return an Array of Strings that contains the names of the interfaces on
     * this host
     * @throws LibvirtException
     */
    public String[] listDefinedInterfaces() throws LibvirtException {
        final int max = numOfDefinedInterfaces();
        if (max > 0) {
            final CString[] ifs = new CString[max];
            final int n = processError(libvirt.virConnectListDefinedInterfaces(VCP, ifs, max));
            return Library.toStringArray(ifs, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Lists the inactive networks
     *
     * @return an Array of Strings that contains the names of the inactive
     * networks
     * @throws LibvirtException
     */
    public String[] listDefinedNetworks() throws LibvirtException {
        int maxnames = numOfDefinedNetworks();
        if (maxnames > 0) {
            final CString[] names = new CString[maxnames];
            final int n = processError(libvirt.virConnectListDefinedNetworks(VCP, names, maxnames));
            return Library.toStringArray(names, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Provides the list of names of inactive storage pools.
     *
     * @return an Array of Strings that contains the names of the defined
     * storage pools
     * @throws LibvirtException
     */
    public String[] listDefinedStoragePools() throws LibvirtException {
        int num = numOfDefinedStoragePools();
        if (num > 0) {
            CString[] pools = new CString[num];
            final int n = processError(libvirt.virConnectListDefinedStoragePools(VCP, pools, num));
            return Library.toStringArray(pools, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * List the names of the devices on this node
     *
     * @param capabilityName optional capability name
     */
    public String[] listDevices(String capabilityName) throws LibvirtException {
        int maxDevices = numOfDevices(capabilityName);
        if (maxDevices > 0) {
            CString[] names = new CString[maxDevices];
            final int n = processError(libvirt.virNodeListDevices(VCP, capabilityName, names, maxDevices, 0));
            return Library.toStringArray(names, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Lists the active domains.
     *
     * @return and array of the IDs of the active domains
     * @throws LibvirtException
     */
    public int[] listDomains() throws LibvirtException {
        int maxids = numOfDomains();
        int[] ids = new int[maxids];

        if (maxids > 0) {
            processError(libvirt.virConnectListDomains(VCP, ids, maxids));
        }
        return ids;
    }

    /**
     * Provides the list of names of interfaces on this host
     *
     * @return an Array of Strings that contains the names of the interfaces on
     * this host
     * @throws LibvirtException
     */
    public String[] listInterfaces() throws LibvirtException {
        int num = numOfInterfaces();
        if (num > 0) {
            CString[] ifs = new CString[num];
            final int n = processError(libvirt.virConnectListInterfaces(VCP, ifs, num));
            return Library.toStringArray(ifs, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Lists the names of the network filters
     *
     * @return an Array of Strings that contains the names network filters
     * @throws LibvirtException
     */
    public String[] listNetworkFilters() throws LibvirtException {
        int maxnames = numOfNetworkFilters();
        if (maxnames > 0) {
            CString[] names = new CString[maxnames];
            final int n = processError(libvirt.virConnectListNWFilters(VCP, names, maxnames));
            return Library.toStringArray(names, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Lists the active networks.
     *
     * @return an Array of Strings that contains the names of the active
     * networks
     * @throws LibvirtException
     */
    public String[] listNetworks() throws LibvirtException {
        int maxnames = numOfNetworks();
        if (maxnames > 0) {
            CString[] names = new CString[maxnames];
            final int n = processError(libvirt.virConnectListNetworks(VCP, names, maxnames));
            return Library.toStringArray(names, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Retrieve the List UUIDs of defined secrets
     *
     * @return an Array of Strings that contains the uuids of the defined
     * secrets
     */
    public String[] listSecrets() throws LibvirtException {
        int num = numOfSecrets();
        if (num > 0) {
            CString[] returnValue = new CString[num];
            final int n = processError(libvirt.virConnectListSecrets(VCP, returnValue, num));
            return Library.toStringArray(returnValue, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Provides the list of names of active storage pools.
     *
     * @return an Array of Strings that contains the names of the defined
     * storage pools
     * @throws LibvirtException
     */
    public String[] listStoragePools() throws LibvirtException {
        int num = numOfStoragePools();
        if (num > 0) {
            CString[] returnValue = new CString[num];
            final int n = processError(libvirt.virConnectListStoragePools(VCP, returnValue, num));
            return Library.toStringArray(returnValue, n);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Creates and starts a new virtual network. The properties of the network
     * are based on an XML description similar to the one returned by
     * virNetworkGetXMLDesc()
     *
     * @param xmlDesc the Network Description
     * @return the Network object representing the created network
     * @throws LibvirtException
     */
    public Network networkCreateXML(String xmlDesc) throws LibvirtException {
        NetworkPointer ptr = processError(libvirt.virNetworkCreateXML(VCP, xmlDesc));
        return new Network(this, ptr);
    }

    /**
     * Defines a network, but does not create it. The properties of the network
     * are based on an XML description similar to the one returned by
     * virNetworkGetXMLDesc()
     *
     * @param xmlDesc
     * @return the resulting Network object
     * @throws LibvirtException
     */
    public Network networkDefineXML(String xmlDesc) throws LibvirtException {
        NetworkPointer ptr = processError(libvirt.virNetworkDefineXML(VCP, xmlDesc));
        return new Network(this, ptr);
    }

    /**
     * Defines a networkFilter
     *
     * @param xmlDesc the descirption of the filter
     * @return the new filer
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterDefineXML(String xmlDesc) throws LibvirtException {
        NetworkFilterPointer ptr = processError(libvirt.virNWFilterDefineXML(VCP, xmlDesc));
        return new NetworkFilter(this, ptr);
    }

    /**
     * Fetch a network filter based on its unique name
     *
     * @param name name of network filter to fetch
     * @return network filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByName(String name) throws LibvirtException {
        NetworkFilterPointer ptr = processError(libvirt.virNWFilterLookupByName(VCP, name));
        return new NetworkFilter(this, ptr);
    }

    /**
     * Looks up a network filter based on its UUID in array form. The UUID Array
     * contains an unpacked representation of the UUID, each int contains only
     * one byte.
     *
     * @param UUID the UUID as an unpacked int array
     * @return the network filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        NetworkFilterPointer ptr = processError(libvirt.virNWFilterLookupByUUID(VCP, uuidBytes));
        return new NetworkFilter(this, ptr);
    }

    /**
     * Fetch a network filter based on its globally unique id
     *
     * @param uuid a java UUID
     * @return a new network filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByUUID(UUID uuid) throws LibvirtException {
        return networkFilterLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a network filter based on its UUID in String form.
     *
     * @param UUID the UUID in canonical String representation
     * @return the Network Filter object
     * @throws LibvirtException
     */
    public NetworkFilter networkFilterLookupByUUIDString(String UUID) throws LibvirtException {
        NetworkFilterPointer ptr = processError(libvirt.virNWFilterLookupByUUIDString(VCP, UUID));
        return new NetworkFilter(this, ptr);
    }

    /**
     * Looks up a network on the based on its name.
     *
     * @param name name of the network
     * @return The Network object found
     * @throws LibvirtException
     */
    public Network networkLookupByName(String name) throws LibvirtException {
        NetworkPointer ptr = processError(libvirt.virNetworkLookupByName(VCP, name));
        return new Network(this, ptr);
    }

    /**
     * Looks up a network based on its UUID represented as an int array. The
     * UUID Array contains an unpacked representation of the UUID, each int
     * contains only one byte.
     *
     * @param UUID the UUID as an unpacked int array
     * @return The Network object found
     * @throws LibvirtException
     * @deprecated use the UUIDString or UUID API.
     */
    @Deprecated
    public Network networkLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        NetworkPointer ptr = processError(libvirt.virNetworkLookupByUUID(VCP, uuidBytes));
        return new Network(this, ptr);
    }

    /**
     * Fetch a network based on its globally unique id
     *
     * @param uuid a java UUID
     * @return a new network object
     * @throws LibvirtException
     */
    public Network networkLookupByUUID(UUID uuid) throws LibvirtException {
        return networkLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a network based on its UUID represented as a String.
     *
     * @param UUID the UUID in canonical String representation
     * @return The Network object found
     * @throws LibvirtException
     */
    public Network networkLookupByUUIDString(String UUID) throws LibvirtException {
        NetworkPointer ptr = processError(libvirt.virNetworkLookupByUUIDString(VCP, UUID));
        return new Network(this, ptr);
    }

    /**
     * Returns a NodeInfo object describing the hardware configuration of the
     * node.
     *
     * @return a NodeInfo object
     * @throws LibvirtException
     */
    public NodeInfo nodeInfo() throws LibvirtException {
        virNodeInfo vInfo = new virNodeInfo();
        processError(libvirt.virNodeGetInfo(VCP, vInfo));
        return new NodeInfo(vInfo);
    }

    /**
     * Provides the number of inactive domains.
     *
     * @return the number of inactive domains
     * @throws LibvirtException
     */
    public int numOfDefinedDomains() throws LibvirtException {
        return processError(libvirt.virConnectNumOfDefinedDomains(VCP));
    }

    /**
     * Provides the number of defined interfaces.
     *
     * @return the number of interfaces
     * @throws LibvirtException
     */
    public int numOfDefinedInterfaces() throws LibvirtException {
        return processError(libvirt.virConnectNumOfDefinedInterfaces(VCP));
    }

    /**
     * Provides the number of inactive networks.
     *
     * @return the number of inactive networks
     * @throws LibvirtException
     */
    public int numOfDefinedNetworks() throws LibvirtException {
        return processError(libvirt.virConnectNumOfDefinedNetworks(VCP));
    }

    /**
     * Provides the number of inactive storage pools
     *
     * @return the number of pools found
     * @throws LibvirtException
     */
    public int numOfDefinedStoragePools() throws LibvirtException {
        return processError(libvirt.virConnectNumOfDefinedStoragePools(VCP));
    }

    /**
     * Provides the number of node devices.
     *
     * @return the number of inactive domains
     * @throws LibvirtException
     */
    public int numOfDevices(String capabilityName) throws LibvirtException {
        return processError(libvirt.virNodeNumOfDevices(VCP, capabilityName, 0));
    }

    /**
     * Provides the number of active domains.
     *
     * @return the number of active domains
     * @throws LibvirtException
     */
    public int numOfDomains() throws LibvirtException {
        return processError(libvirt.virConnectNumOfDomains(VCP));
    }

    /**
     * Provides the number of interfaces.
     *
     * @return the number of interfaces
     * @throws LibvirtException
     */
    public int numOfInterfaces() throws LibvirtException {
        return processError(libvirt.virConnectNumOfInterfaces(VCP));
    }

    /**
     * Provides the number of network filters
     *
     * @return the number of network filters
     * @throws LibvirtException
     */
    public int numOfNetworkFilters() throws LibvirtException {
        return processError(libvirt.virConnectNumOfNWFilters(VCP));
    }

    /**
     * Provides the number of active networks.
     *
     * @return the number of active networks
     * @throws LibvirtException
     */
    public int numOfNetworks() throws LibvirtException {
        return processError(libvirt.virConnectNumOfNetworks(VCP));
    }

    /**
     * Fetch number of currently defined secrets.
     *
     * @return the number of secrets
     */
    public int numOfSecrets() throws LibvirtException {
        return processError(libvirt.virConnectNumOfSecrets(VCP));
    }

    /**
     * Provides the number of active storage pools
     *
     * @return the number of pools found
     * @throws LibvirtException
     */
    public int numOfStoragePools() throws LibvirtException {
        return processError(libvirt.virConnectNumOfStoragePools(VCP));
    }

    /**
     * Restores a domain saved to disk by Domain.save().
     *
     * @param from the path of the saved file on the remote host
     * @throws LibvirtException
     */
    public void restore(String from) throws LibvirtException {
        processError(libvirt.virDomainRestore(VCP, from));
    }

    /**
     * If XML specifies a UUID, locates the specified secret and replaces all
     * attributes of the secret specified by UUID by attributes specified in xml
     * (any attributes not specified in xml are discarded). Otherwise, creates a
     * new secret with an automatically chosen UUID, and initializes its
     * attributes from xml.
     *
     * @param xmlDesc the secret to create
     * @return the Secret object
     * @throws LibvirtException
     */
    public Secret secretDefineXML(String xmlDesc) throws LibvirtException {
        SecretPointer ptr = processError(libvirt.virSecretDefineXML(VCP, xmlDesc, 0));
        return new Secret(this, ptr);
    }

    /**
     * Looks up a secret based on its UUID in array form. The UUID Array
     * contains an unpacked representation of the UUID, each int contains only
     * one byte.
     *
     * @param UUID the UUID as an unpacked int array
     * @return the Secret object
     * @throws LibvirtException
     */
    public Secret secretLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        SecretPointer ptr = processError(libvirt.virSecretLookupByUUID(VCP, uuidBytes));
        return new Secret(this, ptr);
    }

    /**
     * Fetch a secret based on its globally unique id
     *
     * @param uuid a java UUID
     * @return a new domain object
     * @throws LibvirtException
     */
    public Secret secretLookupByUUID(UUID uuid) throws LibvirtException {
        return secretLookupByUUIDString(uuid.toString());
    }

    /**
     * Looks up a secret based on its UUID in String form.
     *
     * @param UUID the UUID in canonical String representation
     * @return the Domain object
     * @throws LibvirtException
     */
    public Secret secretLookupByUUIDString(String UUID) throws LibvirtException {
        SecretPointer ptr = processError(libvirt.virSecretLookupByUUIDString(VCP, UUID));
        return new Secret(this, ptr);
    }

    public void setConnectionErrorCallback(VirErrorCallback callback) throws LibvirtException {
        libvirt.virConnSetErrorFunc(VCP, null, callback);
    }

    /**
     * change the amount of memory reserved to Domain0. Domain0 is the domain
     * where the application runs. This function may requires priviledged access
     * to the hypervisor.
     *
     * @param memory in kilobytes
     * @throws LibvirtException
     */
    public void setDom0Memory(long memory) throws LibvirtException {
        processError(libvirt.virDomainSetMemory(null, new NativeLong(memory)));
    }

    /**
     * Create a new storage based on its XML description. The pool is not
     * persistent, so its definition will disappear when it is destroyed, or if
     * the host is restarted
     *
     * @param xmlDesc XML description for new pool
     * @param flags   future flags, use 0 for now
     * @return StoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolCreateXML(String xmlDesc, int flags) throws LibvirtException {
        StoragePoolPointer ptr = processError(libvirt.virStoragePoolCreateXML(VCP, xmlDesc, flags));
        return new StoragePool(this, ptr);
    }

    /**
     * Define a new inactive storage pool based on its XML description. The pool
     * is persistent, until explicitly undefined.
     *
     * @param xml   XML description for new pool
     * @param flags flags future flags, use 0 for now
     * @return StoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolDefineXML(String xml, int flags) throws LibvirtException {
        StoragePoolPointer ptr = processError(libvirt.virStoragePoolDefineXML(VCP, xml, flags));
        return new StoragePool(this, ptr);
    }

    /**
     * Fetch a storage pool based on its unique name
     *
     * @param name name of pool to fetch
     * @return StoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByName(String name) throws LibvirtException {
        StoragePoolPointer ptr = processError(libvirt.virStoragePoolLookupByName(VCP, name));
        return new StoragePool(this, ptr);
    }

    /**
     * Fetch a storage pool based on its globally unique id
     *
     * @param UUID globally unique id of pool to fetch
     * @return a new network object
     * @throws LibvirtException
     * @deprecated Use the UUIDString or UUID APIs.
     */
    @Deprecated
    public StoragePool storagePoolLookupByUUID(int[] UUID) throws LibvirtException {
        byte[] uuidBytes = Connect.createUUIDBytes(UUID);
        StoragePoolPointer ptr = processError(libvirt.virStoragePoolLookupByUUID(VCP, uuidBytes));
        return new StoragePool(this, ptr);
    }

    /**
     * Fetch a storage pool based on its globally unique id
     *
     * @param uuid a java UUID
     * @return a new network object
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByUUID(UUID uuid) throws LibvirtException {
        return storagePoolLookupByUUIDString(uuid.toString());
    }

    /**
     * Fetch a storage pool based on its globally unique id
     *
     * @param UUID globally unique id of pool to fetch
     * @return VirStoragePool object
     * @throws LibvirtException
     */
    public StoragePool storagePoolLookupByUUIDString(String UUID) throws LibvirtException {
        StoragePoolPointer ptr = processError(libvirt.virStoragePoolLookupByUUIDString(VCP, UUID));
        return new StoragePool(this, ptr);
    }

    /**
     * Fetch a a storage volume based on its globally unique key
     *
     * @param key globally unique key
     * @return a storage volume
     */
    public StorageVol storageVolLookupByKey(String key) throws LibvirtException {
        StorageVolPointer sPtr = processError(libvirt.virStorageVolLookupByKey(VCP, key));
        return new StorageVol(this, sPtr);
    }

    /**
     * Fetch a storage volume based on its locally (host) unique path
     *
     * @param path locally unique path
     * @return a storage volume
     */
    public StorageVol storageVolLookupByPath(String path) throws LibvirtException {
        StorageVolPointer sPtr = processError(libvirt.virStorageVolLookupByPath(VCP, path));
        return new StorageVol(this, sPtr);
    }

    /**
     * Creates a new stream object which can be used to perform streamed I/O
     * with other public API function.
     *
     * @param flags use Stream.VIR_STREAM_NONBLOCK if non-blocking is required
     * @return the new object
     */
    public Stream streamNew(int flags) throws LibvirtException {
        StreamPointer sPtr = processError(libvirt.virStreamNew(VCP, flags));
        return new Stream(this, sPtr);
    }

    /**
     * Verify the connect is active.
     *
     * @return boolean   The true connected, or false not.
     * @throws LibvirtException
     */
    public boolean isConnected() throws LibvirtException {
        return ((VCP != null) ? true : false);
    }

    /**
     * Determine if the connection to the hypervisor is still alive.
     * <p>
     * A connection will be classed as alive if it is either local,
     * or running over a channel (TCP or UNIX socket) which is not closed.
     *
     * @return {@code true} if alive, {@code false} otherwise.
     */
    public boolean isAlive() throws LibvirtException {
        return (1 == processError(libvirt.virConnectIsAlive(VCP)));
    }

    /**
     * Start sending keepalive messages.
     * <p>
     * After {@code interval} seconds of inactivity, consider the
     * connection to be broken when no response is received after
     * {@code count} keepalive messages sent in a row.
     * <p>
     * In other words, sending {@code count + 1} keepalive message
     * results in closing the connection.
     * <p>
     * When interval is <= 0, no keepalive messages will be sent.
     * <p>
     * When count is 0, the connection will be automatically closed after
     * interval seconds of inactivity without sending any keepalive
     * messages.
     * <p>
     * <em>Note</em>: client has to implement and run event loop to be
     * able to use keepalive messages. Failure to do so may result in
     * connections being closed unexpectedly.
     * <p>
     * <em>Note</em>: This API function controls only keepalive messages sent by
     * the client. If the server is configured to use keepalive you still
     * need to run the event loop to respond to them, even if you disable
     * keepalives by this function.
     *
     * @param interval number of seconds of inactivity before a keepalive
     *                 message is sent
     * @param count    number of messages that can be sent in a row
     * @return {@code true} when successful, {@code false} otherwise.
     */
    public boolean setKeepAlive(int interval, int count) throws LibvirtException {
        return (0 == processError(libvirt.virConnectSetKeepAlive(VCP, interval, count)));
    }
}
