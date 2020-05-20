package org.libvirt.jna;

import java.nio.ByteBuffer;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

import java.util.Arrays;
import java.util.List;

/**
 * The libvirt interface which is exposed via JNA. The complete API is
 * documented at http://www.libvirt.org/html/libvirt-libvirt.html.
 *
 * Known api calls to be missing
 * LIBVIRT_0.1.0
 * virDefaultErrorFunc
 * virConnCopyLastError
 * virFreeError
 *
 * LIBVIRT_0_5.0
 * virEventRegisterImpl
 * virConnectDomainEventRegister
 * virConnectDomainEventDeregister
 *
 * LIBVIRT_0.6.0
 * virConnectRef
 * virNetworkRef
 * virStoragePoolRef
 * virStorageVolRef
 * virNodeDeviceRef
 *
 * LIBVIRT_0.6.1
 * virFreeError
 * virSaveLastError
 *
 * LIBVIRT_0.6.4
 * virInterfaceRef
 *
 * LIBVIRT_0.7.1
 * virSecretRef
 *
 * LIBVIRT_0.7.2
 * virStreamRef
 *
 * LIBVIRT_0.8.0
 * virNWFilterRef
 *
 */
public interface Libvirt extends Library {
    // Callbacks
    /**
     * Callback interface for authorization
     */
    interface VirConnectAuthCallback extends Callback {
        int authCallback(virConnectCredential cred, int ncred, Pointer cbdata);
    }

    /**
     * Callback interface for connection closed events.
     */
    interface VirConnectCloseFunc extends Callback {
        void callback(ConnectionPointer vcp, int reason, Pointer opaque);
    }

    /**
     * Domain Event Callbacks
     */

    /**
     * Common Event Callback super interface.
     *
     * All domain event callbacks extend this interface.
     *
     * @see #virConnectDomainEventRegisterAny
     */
    interface VirDomainEventCallback extends Callback {}

    interface VirConnectDomainEventIOErrorCallback extends VirDomainEventCallback {
        void eventCallback(ConnectionPointer virConnectPtr,
                           DomainPointer virDomainPointer,
                           String srcPath,
                           String devAlias,
                           int action,
                           Pointer opaque);
    }

    interface VirConnectDomainEventGenericCallback extends VirDomainEventCallback {
        void eventCallback(ConnectionPointer virConnectPtr,
                           DomainPointer virDomainPointer, Pointer opaque);
    }

    interface VirConnectDomainEventCallback extends VirDomainEventCallback {
        int eventCallback(ConnectionPointer virConnectPtr,
                          DomainPointer virDomainPointer,
                          int event,
                          int detail,
                          Pointer opaque);
    }

    // PMWakeup and PMSuspend have the same callback interface.
    interface VirConnectDomainEventPMChangeCallback extends VirDomainEventCallback {
        void eventCallback(ConnectionPointer virConnectPtr,
                           DomainPointer virDomainPointer,
                           int reason, Pointer opaque);
    }

    /**
     * Error callback
     */
    interface VirErrorCallback extends Callback {
        void errorCallback(Pointer userData, virError error);
    }

    /**
     * Stream callbacks
     */
    interface VirStreamSinkFunc extends Callback {
        int sinkCallback(StreamPointer virStreamPtr, String data,
                         NativeLong nbytes, Pointer opaque);
    }

    interface VirStreamSourceFunc extends Callback {
        int sourceCallback(StreamPointer virStreamPtr, String data,
                           NativeLong nbytes, Pointer opaque);
    }

    interface VirStreamEventCallback extends Callback {
        void eventCallback(StreamPointer virStreamPointer, int events, Pointer opaque);
    }

    /**
     * Generic Callbacks
     */
    interface VirFreeCallback extends Callback {
        void freeCallback(Pointer opaque);
    }

    /*
     * Timeout Callback
     */
    interface VirEventTimeoutCallback extends Callback {
        void tick(int timerID, Pointer opaque);
    }

    Libvirt INSTANCE =
            (Libvirt) Native.loadLibrary(Platform.isWindows() ? "virt-0" : "virt", Libvirt.class);

    // Constants we need
    int VIR_UUID_BUFLEN = 16;
    int VIR_UUID_STRING_BUFLEN = 36 + 1;
    int VIR_DOMAIN_SCHED_FIELD_LENGTH = 80;

    // Connection Functions
    CString virConnectBaselineCPU(ConnectionPointer virConnectPtr,
                                  String[] xmlCPUs, int ncpus, int flags);

    ///
    /// Structure definitions
    ///

    class SecurityLabel extends Structure {
        private static final int VIR_SECURITY_LABEL_BUFLEN = 4096 + 1;
        private static final List<String> FIELDS = Arrays.asList("label", "enforcing");

        public byte[] label = new byte[VIR_SECURITY_LABEL_BUFLEN];
        public int enforcing;

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    };

    class SecurityModel extends Structure {
        private static final int VIR_SECURITY_MODEL_BUFLEN = 256 + 1;
        private static final int VIR_SECURITY_DOI_BUFLEN = 256 + 1;

        private static final List<String> FIELDS = Arrays.asList("model", "doi");

        public byte[] model = new byte[VIR_SECURITY_MODEL_BUFLEN];
        public byte[] doi = new byte[VIR_SECURITY_DOI_BUFLEN];

        @Override
        protected List<String> getFieldOrder() {
            return FIELDS;
        }
    }

    /**
     * @deprecated as of libvirt 0.6.0, all errors reported in the
     * per-connection object are also duplicated in the global error
     * object. This method remains for backwards compatibility. Use
     * {@link #virCopyLastError} instead.
    */
    @Deprecated
    int virConnCopyLastError(ConnectionPointer virConnectPtr, virError to);
    int virConnectClose(ConnectionPointer virConnectPtr);
    int virConnectCompareCPU(ConnectionPointer virConnectPtr, String xmlDesc, int flags);

    // Register Domain Event Callbacks
    int virConnectDomainEventRegisterAny(ConnectionPointer virConnectPtr,
            DomainPointer virDomainPtr, int eventID, VirDomainEventCallback cb,
            Pointer opaque, Libvirt.VirFreeCallback freecb);

    int virConnectRegisterCloseCallback(ConnectionPointer virConnectPtr,
                                        VirConnectCloseFunc cb,
                                        Pointer opaque, Pointer freeOpaque);
    int virConnectUnregisterCloseCallback(ConnectionPointer virConnectPtr,
                                          VirConnectCloseFunc cb);

    int virConnectDomainEventDeregisterAny(ConnectionPointer virConnectPtr, int callbackID);
    void virConnSetErrorFunc(ConnectionPointer virConnectPtr, Pointer userData,
                             VirErrorCallback callback);
    int virConnectIsAlive(ConnectionPointer virConnectPtr);
    int virConnectIsEncrypted(ConnectionPointer virConnectPtr);
    int virConnectIsSecure(ConnectionPointer virConnectPtr);
    CString virConnectFindStoragePoolSources(ConnectionPointer virConnectPtr,
                                             String type, String srcSpec, int flags);
    CString virConnectGetCapabilities(ConnectionPointer virConnectPtr);
    CString virConnectGetHostname(ConnectionPointer virConnectPtr);
    int virConnectGetLibVersion(ConnectionPointer virConnectPtr,
                                LongByReference libVer);
    int virConnectGetMaxVcpus(ConnectionPointer virConnectPtr, String type);
    CString virConnectGetSysinfo(ConnectionPointer virConnectPtr, int flags);
    String virConnectGetType(ConnectionPointer virConnectPtr);
    CString virConnectGetURI(ConnectionPointer virConnectPtr);
    int virConnectGetVersion(ConnectionPointer virConnectPtr,
                             LongByReference hvVer);
    int virConnectListDefinedDomains(ConnectionPointer virConnectPtr,
                                     CString[] name, int maxnames);
    int virConnectListDefinedNetworks(ConnectionPointer virConnectPtr,
                                      CString[] name, int maxnames);
    int virConnectListDefinedStoragePools(ConnectionPointer virConnectPtr,
                                          CString[] names, int maxnames);
    int virConnectListDefinedInterfaces(ConnectionPointer virConnectPtr,
                                        CString[] name, int maxNames);
    int virConnectListDomains(ConnectionPointer virConnectPtr,
                              int[] ids, int maxnames);
    int virConnectListInterfaces(ConnectionPointer virConnectPtr,
                                 CString[] name, int maxNames);
    int virConnectListNetworks(ConnectionPointer virConnectPtr,
                               CString[] name, int maxnames);
    int virConnectListNWFilters(ConnectionPointer virConnectPtr,
                                CString[] name, int maxnames);
    int virConnectListSecrets(ConnectionPointer virConnectPtr,
                              CString[] uids, int maxUids);
    int virConnectListStoragePools(ConnectionPointer virConnectPtr,
                                   CString[] names, int maxnames);
    int virConnectNumOfDefinedDomains(ConnectionPointer virConnectPtr);
    int virConnectNumOfDefinedNetworks(ConnectionPointer virConnectPtr);
    int virConnectNumOfDefinedInterfaces(ConnectionPointer virConnectPtr);
    int virConnectNumOfDefinedStoragePools(ConnectionPointer virConnectPtr);
    int virConnectNumOfDomains(ConnectionPointer virConnectPtr);
    int virConnectNumOfInterfaces(ConnectionPointer virConnectPtr);
    int virConnectNumOfNetworks(ConnectionPointer virConnectPtr);
    int virConnectNumOfNWFilters(ConnectionPointer virConnectPtr);
    int virConnectNumOfSecrets(ConnectionPointer virConnectPtr);
    int virConnectNumOfStoragePools(ConnectionPointer virConnectPtr);
    ConnectionPointer virConnectOpen(String name);
    ConnectionPointer virConnectOpenAuth(String name, virConnectAuth auth, int flags);
    ConnectionPointer virConnectOpenReadOnly(String name);
    int virConnectSetKeepAlive(ConnectionPointer virConnectPtr,
                               int interval, int count);

    /**
     * @deprecated as of libvirt 0.6.0, all errors reported in the
     * per-connection object are also duplicated in the global error
     * object. This method remains only for backwards compatibility.
     * Use {@link #virGetLastError} instead.
     */
    @Deprecated
    virError virConnGetLastError(ConnectionPointer virConnectPtr);
    void virConnResetLastError(ConnectionPointer virConnectPtr);
    CString virConnectDomainXMLFromNative(ConnectionPointer virConnectPtr,
                                          String nativeFormat,
                                          String nativeConfig,
                                          int flags);
    CString virConnectDomainXMLToNative(ConnectionPointer virConnectPtr,
                                        String nativeFormat, String domainXML,
                                        int flags);

    // Global functions
    int virGetVersion(LongByReference libVer, String type,
                      LongByReference typeVer);
    int virInitialize();
    int virCopyLastError(virError error);
    virError virGetLastError();
    void virResetLastError();
    void virSetErrorFunc(Pointer userData, VirErrorCallback callback);

    // Event loop functions.
    int virEventRegisterDefaultImpl();
    int virEventRunDefaultImpl();

    // Domain functions
    int virDomainAbortJob(DomainPointer virDomainPtr);
    int virDomainAttachDevice(DomainPointer virDomainPtr, String deviceXML);
    int virDomainAttachDeviceFlags(DomainPointer virDomainPtr,
                                   String deviceXML, int flags);
    int virDomainBlockPeek(DomainPointer virDomainPtr,
                           String disk, long offset, SizeT size,
                           ByteBuffer buffer, int flags);
    int virDomainBlockStats(DomainPointer virDomainPtr, String path,
                            virDomainBlockStats stats, SizeT size);
    int virDomainBlockResize(DomainPointer virDomainPtr,
                             String disk, long size, int flags);
    int virDomainCoreDump(DomainPointer virDomainPtr, String to, int flags);
    int virDomainCreate(DomainPointer virDomainPtr);
    int virDomainCreateWithFlags(DomainPointer virDomainPtr, int flags);
    DomainPointer virDomainCreateLinux(ConnectionPointer virConnectPtr,
                                       String xmlDesc, int flags);
    DomainPointer virDomainCreateXML(ConnectionPointer virConnectPtr,
                                     String xmlDesc, int flags);
    DomainPointer virDomainDefineXML(ConnectionPointer virConnectPtr,
                                     String xmlDesc);
    int virDomainDestroy(DomainPointer virDomainPtr);
    int virDomainDetachDevice(DomainPointer virDomainPtr, String deviceXML);
    int virDomainDetachDeviceFlags(DomainPointer virDomainPtr,
                                   String deviceXML, int flags);
    int virDomainFree(DomainPointer virDomainPtr);
    int virDomainGetAutostart(DomainPointer virDomainPtr, IntByReference value);
    int virDomainGetBlockInfo(DomainPointer virDomainPtr, String path,
                              virDomainBlockInfo info, int flags);
    int virDomainGetID(DomainPointer virDomainPtr);
    int virDomainGetInfo(DomainPointer virDomainPtr, virDomainInfo vInfo);
    int virDomainGetJobInfo(DomainPointer virDomainPtr, virDomainJobInfo vInfo);
    NativeLong virDomainGetMaxMemory(DomainPointer virDomainPtr);
    int virDomainGetMaxVcpus(DomainPointer virDomainPtr);
    String virDomainGetName(DomainPointer virDomainPtr);
    CString virDomainGetOSType(DomainPointer virDomainPtr);
    int virDomainGetSchedulerParameters(DomainPointer virDomainPtr,
                                        virSchedParameter[] params,
                                        IntByReference nparams);
    CString virDomainGetSchedulerType(DomainPointer virDomainPtr,
                                      IntByReference nparams);
    int virDomainGetSecurityLabel(DomainPointer virDomainPtr,
                                  SecurityLabel seclabel);
    int virDomainGetUUID(DomainPointer virDomainPtr, byte[] uuidString);
    int virDomainGetUUIDString(DomainPointer virDomainPtr, byte[] uuidString);
    int virDomainGetVcpus(DomainPointer virDomainPtr, virVcpuInfo[] info,
                          int maxInfo, byte[] cpumaps, int maplen);
    CString virDomainGetXMLDesc(DomainPointer virDomainPtr, int flags);
    int virDomainHasCurrentSnapshot(DomainPointer virDomainPtr, int flags);
    int virDomainHasManagedSaveImage(DomainPointer virDomainPtr, int flags);
    int virDomainInterfaceStats(DomainPointer virDomainPtr, String path,
                                virDomainInterfaceStats stats, SizeT size);
    int virDomainIsActive(DomainPointer virDomainPtr);
    int virDomainIsPersistent(DomainPointer virDomainPtr);
    int virDomainIsUpdated(DomainPointer virDomainPtr);
    DomainPointer virDomainLookupByID(ConnectionPointer virConnectPtr, int id);
    DomainPointer virDomainLookupByName(ConnectionPointer virConnectPtr,
                                        String name);
    DomainPointer virDomainLookupByUUID(ConnectionPointer virConnectPtr,
                                        byte[] uuidBytes);
    DomainPointer virDomainLookupByUUIDString(ConnectionPointer virConnectPtr,
                                              String uuidstr);
    int virDomainManagedSave(DomainPointer virDomainPtr, int flags);
    int virDomainManagedSaveRemove(DomainPointer virDomainPtr, int flags);
    int virDomainMemoryPeek(DomainPointer virDomainPtr, long start, SizeT size,
                            ByteBuffer buffer, int flags);
    DomainPointer virDomainMigrate(DomainPointer virDomainPtr,
                                   ConnectionPointer virConnectPtr,
                                   NativeLong flags, String dname,
                                   String uri, NativeLong bandwidth);
    DomainPointer virDomainMigrate2(DomainPointer virDomainPtr,
                                    ConnectionPointer virConnectPtr, String dxml,
                                    NativeLong flags, String dname, String uri,
                                    NativeLong bandwidth);
    int virDomainMigrateSetMaxDowntime(DomainPointer virDomainPtr,
                                       long downtime, int flags);
    int virDomainMigrateToURI(DomainPointer virDomainPtr, String duri,
            NativeLong flags, String dname, NativeLong bandwidth);
    int virDomainMigrateToURI2(DomainPointer virDomainPtr,
                               String dconnuri, String miguri,
                               String dxml, NativeLong flags,
                               String dname, NativeLong bandwidth);
    int virDomainMemoryStats(DomainPointer virDomainPtr,
                             virDomainMemoryStats[] stats, int nrStats, int flags);
    int virDomainPinVcpu(DomainPointer virDomainPtr,
                         int vcpu, byte[] cpumap, int maplen);
    int virDomainPMSuspendForDuration(DomainPointer virDomainPtr,
                                      int target, long duration, int flags);
    int virDomainPMWakeup(DomainPointer virDomainPtr, int flags);
    int virDomainReboot(DomainPointer virDomainPtr, int flags);
    int virDomainRef(DomainPointer virDomainPtr);
    int virDomainReset(DomainPointer virDomainPtr, int flags);
    int virDomainRestore(ConnectionPointer virConnectPtr, String from);
    int virDomainRevertToSnapshot(DomainSnapshotPointer virDomainSnapshotPtr, int flags);
    int virDomainResume(DomainPointer virDomainPtr);
    int virDomainSave(DomainPointer virDomainPtr, String to);
    int virDomainSendKey(DomainPointer virDomainPtr, int codeset, int holdtime,
                         int[] keycodes, int nkeycodes, int flags);
    CString virDomainScreenshot(DomainPointer virDomainPtr,
                                StreamPointer virStreamPtr, int screen, int flags);
    int virDomainSetAutostart(DomainPointer virDomainPtr, int autoStart);
    int virDomainSetMaxMemory(DomainPointer virDomainPtr, NativeLong maxMemory);
    int virDomainSetMemory(DomainPointer virDomainPtr, NativeLong maxMemory);
    int virDomainSetSchedulerParameters(DomainPointer virDomainPtr,
                                        virSchedParameter[] params, int nparams);
    int virDomainSetVcpus(DomainPointer virDomainPtr, int nvcpus);
    int virDomainShutdown(DomainPointer virDomainPtr);
    int virDomainSuspend(DomainPointer virDomainPtr);
    int virDomainUpdateDeviceFlags(DomainPointer virDomainPtr,
                                   String xml, int flags);
    int virDomainUndefine(DomainPointer virDomainPtr);
    int virDomainUndefineFlags(DomainPointer virDomainPtr, int flags);

    // Network functions
    int virNetworkCreate(NetworkPointer virConnectPtr);
    NetworkPointer virNetworkCreateXML(ConnectionPointer virConnectPtr, String xmlDesc);
    NetworkPointer virNetworkDefineXML(ConnectionPointer virConnectPtr, String xmlDesc);
    int virNetworkDestroy(NetworkPointer virConnectPtr);
    int virNetworkFree(NetworkPointer virConnectPtr);
    int virNetworkGetAutostart(NetworkPointer virNetworkPtr, IntByReference value);
    CString virNetworkGetBridgeName(NetworkPointer virNetworkPtr);
    String virNetworkGetName(NetworkPointer virNetworkPtr);
    int virNetworkGetUUID(NetworkPointer virNetworkPtr, byte[] uuidString);
    int virNetworkGetUUIDString(NetworkPointer virNetworkPtr, byte[] uuidString);
    CString virNetworkGetXMLDesc(NetworkPointer virNetworkPtr, int flags);
    int virNetworkIsActive(NetworkPointer virNetworkPtr);
    int virNetworkIsPersistent(NetworkPointer virNetworkPtr);
    NetworkPointer virNetworkLookupByName(ConnectionPointer virConnectPtr, String name);
    NetworkPointer virNetworkLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    NetworkPointer virNetworkLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    int virNetworkSetAutostart(NetworkPointer virConnectPtr, int autoStart);
    int virNetworkUndefine(NetworkPointer virConnectPtr);

    // Node functions
    int virNodeGetInfo(ConnectionPointer virConnectPtr, virNodeInfo virNodeInfo);
    int virNodeGetCellsFreeMemory(ConnectionPointer virConnectPtr,
                                  LongByReference freeMems,
                                  int startCell, int maxCells);
    long virNodeGetFreeMemory(ConnectionPointer virConnectPtr);
    int virNodeGetSecurityModel(ConnectionPointer virConnectPtr, SecurityModel secmodel);

    // Node/Device functions
    int virNodeNumOfDevices(ConnectionPointer virConnectPtr,
                            String capabilityName, int flags);
    int virNodeListDevices(ConnectionPointer virConnectPtr,
                           String capabilityName, CString[] names, int maxnames,
                           int flags);
    DevicePointer virNodeDeviceLookupByName(ConnectionPointer virConnectPtr, String name);
    String virNodeDeviceGetName(DevicePointer virDevicePointer);
    String virNodeDeviceGetParent(DevicePointer virDevicePointer);
    int virNodeDeviceNumOfCaps(DevicePointer virDevicePointer);
    int virNodeDeviceListCaps(DevicePointer virDevicePointer,
                              CString[] names, int maxNames);
    CString virNodeDeviceGetXMLDesc(DevicePointer virDevicePointer, int flags);
    int virNodeDeviceFree(DevicePointer virDevicePointer);
    int virNodeDeviceDettach(DevicePointer virDevicePointer);
    int virNodeDeviceReAttach(DevicePointer virDevicePointer);
    int virNodeDeviceReset(DevicePointer virDevicePointer);
    DevicePointer virNodeDeviceCreateXML(ConnectionPointer virConnectPtr,
                                         String xml, int flags);
    int virNodeDeviceDestroy(DevicePointer virDevicePointer);

    // Storage Pool
    int virStoragePoolBuild(StoragePoolPointer storagePoolPtr, int flags);
    int virStoragePoolCreate(StoragePoolPointer storagePoolPtr, int flags);
    StoragePoolPointer virStoragePoolCreateXML(ConnectionPointer virConnectPtr,
                                               String xml, int flags);
    StoragePoolPointer virStoragePoolDefineXML(ConnectionPointer virConnectPtr,
                                               String xml, int flags);
    int virStoragePoolDelete(StoragePoolPointer storagePoolPtr, int flags);
    int virStoragePoolDestroy(StoragePoolPointer storagePoolPtr);
    int virStoragePoolFree(StoragePoolPointer storagePoolPtr);
    int virStoragePoolGetAutostart(StoragePoolPointer storagePoolPtr, IntByReference value);
    int virStoragePoolGetInfo(StoragePoolPointer storagePoolPtr, virStoragePoolInfo info);
    String virStoragePoolGetName(StoragePoolPointer storagePoolPtr);
    int virStoragePoolGetUUID(StoragePoolPointer storagePoolPtr, byte[] uuidString);
    int virStoragePoolGetUUIDString(StoragePoolPointer storagePoolPtr, byte[] uuidString);
    CString virStoragePoolGetXMLDesc(StoragePoolPointer storagePoolPtr, int flags);
    int virStoragePoolListVolumes(StoragePoolPointer storagePoolPtr,
                                  CString[] names, int maxnames);
    int virStoragePoolIsActive(StoragePoolPointer storagePoolPtr);
    int virStoragePoolIsPersistent(StoragePoolPointer storagePoolPtr);
    StoragePoolPointer virStoragePoolLookupByName(ConnectionPointer virConnectPtr,
                                                  String name);
    StoragePoolPointer virStoragePoolLookupByUUID(ConnectionPointer virConnectPtr,
                                                  byte[] uuidBytes);
    StoragePoolPointer virStoragePoolLookupByUUIDString(ConnectionPointer virConnectPtr,
                                                        String uuidstr);
    StoragePoolPointer virStoragePoolLookupByVolume(StorageVolPointer storageVolPtr);
    int virStoragePoolNumOfVolumes(StoragePoolPointer storagePoolPtr);
    int virStoragePoolRefresh(StoragePoolPointer storagePoolPtr, int flags);
    int virStoragePoolSetAutostart(StoragePoolPointer storagePoolPtr, int autostart);
    int virStoragePoolUndefine(StoragePoolPointer storagePoolPtr);

    // Storage Vol
    StorageVolPointer virStorageVolCreateXML(StoragePoolPointer storagePoolPtr,
                                             String xml, int flags);
    StorageVolPointer virStorageVolCreateXMLFrom(StoragePoolPointer storagePoolPtr,
                                                 String xml,
                                                 StorageVolPointer cloneVolume,
                                                 int flags);
    int virStorageVolDelete(StorageVolPointer storageVolPtr, int flags);
    int virStorageVolFree(StorageVolPointer storageVolPtr);
    int virStorageVolGetInfo(StorageVolPointer storageVolPtr, virStorageVolInfo info);
    String virStorageVolGetKey(StorageVolPointer storageVolPtr);
    String virStorageVolGetName(StorageVolPointer storageVolPtr);
    CString virStorageVolGetPath(StorageVolPointer storageVolPtr);
    CString virStorageVolGetXMLDesc(StorageVolPointer storageVolPtr, int flags);
    StorageVolPointer virStorageVolLookupByKey(ConnectionPointer virConnectPtr,
                                               String name);
    StorageVolPointer virStorageVolLookupByName(StoragePoolPointer storagePoolPtr,
                                                String name);
    StorageVolPointer virStorageVolLookupByPath(ConnectionPointer virConnectPtr,
                                                String path);
    int virStorageVolWipe(StorageVolPointer storageVolPtr, int flags);
    int virStorageVolResize(StorageVolPointer storageVolPtr,
                            long capacity, int flags);

    // Interface Methods
    int virInterfaceCreate(InterfacePointer virDevicePointer, int flags);
    InterfacePointer virInterfaceDefineXML(ConnectionPointer virConnectPtr,
                                           String xml, int flags);
    int virInterfaceDestroy(InterfacePointer virDevicePointer, int flags);
    int virInterfaceFree(InterfacePointer virDevicePointer);
    String virInterfaceGetName(InterfacePointer virInterfacePtr);
    String virInterfaceGetMACString(InterfacePointer virInterfacePtr);
    CString virInterfaceGetXMLDesc(InterfacePointer virInterfacePtr, int flags);
    int virInterfaceIsActive(InterfacePointer virDevicePointer);
    InterfacePointer virInterfaceLookupByMACString(ConnectionPointer virConnectPtr,
                                                   String mac);
    InterfacePointer virInterfaceLookupByName(ConnectionPointer virConnectPtr,
                                              String name);
    int virInterfaceUndefine(InterfacePointer virDevicePointer);

    // Secret Methods
    int virSecretFree(SecretPointer virSecretPtr);
    SecretPointer virSecretDefineXML(ConnectionPointer virConnectPtr,
                                     String xml, int flags);
    int virSecretGetUUID(SecretPointer virSecretPtr, byte[] uuidString);
    int virSecretGetUUIDString(SecretPointer virSecretPtr, byte[] uuidString);
    String virSecretGetUsageID(SecretPointer virSecretPtr);
    int virSecretGetUsageType(SecretPointer virSecretPtr);
    Pointer virSecretGetValue(SecretPointer virSecretPtr,
                              SizeTByReference valueSize, int flags);
    CString virSecretGetXMLDesc(SecretPointer virSecretPtr, int flags);
    SecretPointer virSecretLookupByUsage(ConnectionPointer virConnectPtr,
                                         int usageType, String usageID);
    SecretPointer virSecretLookupByUUID(ConnectionPointer virConnectPtr,
                                        byte[] uuidBytes);
    SecretPointer virSecretLookupByUUIDString(ConnectionPointer virConnectPtr,
                                              String uuidstr);
    int virSecretSetValue(SecretPointer virSecretPtr,
                          String value, SizeT valueSize, int flags);
    int virSecretSetValue(SecretPointer virSecretPtr,
                          byte[] value, SizeT valueSize, int flags);
    int virSecretUndefine(SecretPointer virSecretPtr);

    //Stream Methods
    int virStreamAbort(StreamPointer virStreamPtr);
    int virStreamEventAddCallback(StreamPointer virStreamPtr, int events,
                                  Libvirt.VirStreamEventCallback cb,
                                  Pointer opaque, Libvirt.VirFreeCallback ff);
    int virStreamEventUpdateCallback(StreamPointer virStreamPtr, int events);
    int virStreamEventRemoveCallback(StreamPointer virStreamPtr);
    int virStreamFinish(StreamPointer virStreamPtr);
    int virStreamFree(StreamPointer virStreamPtr);
    StreamPointer virStreamNew(ConnectionPointer virConnectPtr, int flags);
    int virStreamSend(StreamPointer virStreamPtr, ByteBuffer data, SizeT size);
    int virStreamSendAll(StreamPointer virStreamPtr,
                         Libvirt.VirStreamSourceFunc handler, Pointer opaque);
    int virStreamRecv(StreamPointer virStreamPtr, ByteBuffer data, SizeT length);
    int virStreamRecvAll(StreamPointer virStreamPtr,
                         Libvirt.VirStreamSinkFunc handler, Pointer opaque);

    //DomainSnapshot Methods
    DomainSnapshotPointer virDomainSnapshotCreateXML(DomainPointer virDomainPtr,
                                                     String xmlDesc, int flags);
    DomainSnapshotPointer virDomainSnapshotCurrent(DomainPointer virDomainPtr,
                                                   int flags);
    int virDomainSnapshotDelete(DomainSnapshotPointer virDomainSnapshotPtr,
                                int flags);
    CString virDomainSnapshotGetXMLDesc(DomainSnapshotPointer virDomainSnapshotPtr,
                                        int flags);
    int virDomainSnapshotFree(DomainSnapshotPointer virDomainSnapshotPtr);
    int virDomainSnapshotListNames(DomainPointer virDomainPtr,
                                   CString[] names, int nameslen, int flags);
    DomainSnapshotPointer virDomainSnapshotLookupByName(DomainPointer virDomainPtr,
                                                        String name, int flags);
    int virDomainSnapshotNum(DomainPointer virDomainPtr, int flags);

    // Network Filter Methods
    CString virNWFilterGetXMLDesc(NetworkFilterPointer virNWFilterPtr, int flags);
    NetworkFilterPointer virNWFilterDefineXML(ConnectionPointer virConnectPtr,
                                              String xml);
    int virNWFilterFree(NetworkFilterPointer virNWFilterPtr);
    NetworkFilterPointer virNWFilterLookupByName(ConnectionPointer virConnectPtr,
                                                 String name);
    NetworkFilterPointer virNWFilterLookupByUUID(ConnectionPointer virConnectPtr,
                                                 byte[] uuidBytes);
    NetworkFilterPointer virNWFilterLookupByUUIDString(ConnectionPointer virConnectPtr,
                                                       String uuidstr);
    String virNWFilterGetName(NetworkFilterPointer virNWFilterPtr);
    int virNWFilterGetUUID(NetworkFilterPointer virNWFilterPtr, byte[] uuidString);
    int virNWFilterGetUUIDString(NetworkFilterPointer virNWFilterPtr, byte[] uuidString);
    int virNWFilterUndefine(NetworkFilterPointer virNWFilterPtr);

    // Event functions
    int virEventAddTimeout(int milliSeconds, VirEventTimeoutCallback cb,
                           Pointer opaque, Pointer ff);
    int virEventRemoveTimeout(int timer);
    void virEventUpdateTimeout(int timer, int timeout);
}
