package org.libvirt.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

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
 * LIBVIRT_0.4.2 
 * virDomainBlockPeek 
 * virDomainMemoryPeek 
 * 
 * LIBVIRT_0_5.0
 * virEventRegisterImpl 
 * virConnectDomainEventRegister
 * virConnectDomainEventDeregister 
 * 
 * LIBVIRT_0.6.0 
 * virConnectRef 
 * virDomainRef
 * virNetworkRef 
 * virStoragePoolRef 
 * virStorageVolRef 
 * virNodeDeviceRef
 * 
 * LIBVIRT_0.6.1 
 * virFreeError 
 * virSaveLastError 
 * virDomainGetSecurityLabel;
 * virNodeGetSecurityModel; 
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
        public int authCallback(virConnectCredential cred, int ncred, Pointer cbdata);
    }

    /**
     * Error callback
     */
    interface VirErrorCallback extends Callback {
        public void errorCallback(Pointer userData, virError error);
    }
    
    /**
     * Stream callbacks
     */
    interface VirStreamSinkFunc extends Callback {
        public int sinkCallback(StreamPointer virStreamPtr, String data, NativeLong nbytes, Pointer opaque) ;
    }
    
    interface VirStreamSourceFunc extends Callback {
        public int sourceCallback(StreamPointer virStreamPtr, String data, NativeLong nbytes, Pointer opaque) ;
    }    
    
    interface VirStreamEventCallback extends Callback {
        public void eventCallback(StreamPointer virStreamPointer, int events, Pointer opaque) ;
    }
    
    /** 
     * Generic Callbacks
     */
    interface VirFreeCallback extends Callback {
        public void freeCallback(Pointer opaque) ;
    }    
    
    interface VirConnectDomainEventGenericCallback extends Callback {
        public void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, Pointer opaque) ;
    }
    
    Libvirt INSTANCE = (Libvirt) Native.loadLibrary("virt", Libvirt.class);

    // Constants we need
    public static int VIR_UUID_BUFLEN = 16;
    public static int VIR_UUID_STRING_BUFLEN = (36 + 1);
    public static int VIR_DOMAIN_SCHED_FIELD_LENGTH = 80;

    // Connection Functions
    public String virConnectBaselineCPU(ConnectionPointer virConnectPtr, String[] xmlCPUs, int ncpus, int flags);
    public int virConnCopyLastError(ConnectionPointer virConnectPtr, virError to);
    public int virConnectClose(ConnectionPointer virConnectPtr);
    public int virConnectCompareCPU(ConnectionPointer virConnectPtr, String xmlDesc, int flags);
    public int virConnectDomainEventRegisterAny(ConnectionPointer virConnectPtr, DomainPointer virDomainPtr, int eventID, Libvirt.VirConnectDomainEventGenericCallback cb, Pointer opaque, Libvirt.VirFreeCallback freecb);
    public int virConnectDomainEventDeregisterAny(ConnectionPointer virConnectPtr, int callbackID) ;
    public void virConnSetErrorFunc(ConnectionPointer virConnectPtr, Pointer userData, VirErrorCallback callback);
    public int virConnectIsEncrypted(ConnectionPointer virConnectPtr) ;
    public int virConnectIsSecure(ConnectionPointer virConnectPtr) ;    
    public String virConnectFindStoragePoolSources(ConnectionPointer virConnectPtr, String type, String srcSpec, int flags);
    public String virConnectGetCapabilities(ConnectionPointer virConnectPtr);
    public String virConnectGetHostname(ConnectionPointer virConnectPtr);
    public int virConnectGetLibVersion(ConnectionPointer virConnectPtr, LongByReference libVer);
    public int virConnectGetMaxVcpus(ConnectionPointer virConnectPtr, String type);
    public String virConnectGetType(ConnectionPointer virConnectPtr);
    public String virConnectGetURI(ConnectionPointer virConnectPtr);
    public int virConnectGetVersion(ConnectionPointer virConnectPtr, LongByReference hvVer);
    public int virConnectListDefinedDomains(ConnectionPointer virConnectPtr, String[] name, int maxnames);
    public int virConnectListDefinedNetworks(ConnectionPointer virConnectPtr, String[] name, int maxnames);
    public int virConnectListDefinedStoragePools(ConnectionPointer virConnectPtr, String[] names, int maxnames);
    public int virConnectListDefinedInterfaces(ConnectionPointer virConnectPtr, String[] name, int maxNames);
    public int virConnectListDomains(ConnectionPointer virConnectPtr, int[] ids, int maxnames);
    public int virConnectListInterfaces(ConnectionPointer virConnectPtr, String[] name, int maxNames);
    public int virConnectListNetworks(ConnectionPointer virConnectPtr, String[] name, int maxnames);
    public int virConnectListNWFilters(ConnectionPointer virConnectPtr, String[] name, int maxnames);    
    public int virConnectListSecrets(ConnectionPointer virConnectPtr, String[] uids, int maxUids);
    public int virConnectListStoragePools(ConnectionPointer virConnectPtr, String[] names, int maxnames);
    public int virConnectNumOfDefinedDomains(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDefinedNetworks(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDefinedInterfaces(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDefinedStoragePools(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDomains(ConnectionPointer virConnectPtr);
    public int virConnectNumOfInterfaces(ConnectionPointer virConnectPtr);
    public int virConnectNumOfNetworks(ConnectionPointer virConnectPtr);
    public int virConnectNumOfNWFilters(ConnectionPointer virConnectPtr);    
    public int virConnectNumOfSecrets(ConnectionPointer virConnectPtr);      
    public int virConnectNumOfStoragePools(ConnectionPointer virConnectPtr);  
    public ConnectionPointer virConnectOpen(String name);
    public ConnectionPointer virConnectOpenAuth(String name, virConnectAuth auth, int flags);
    public ConnectionPointer virConnectOpenReadOnly(String name);
    public virError virConnGetLastError(ConnectionPointer virConnectPtr);
    public int virConnResetLastError(ConnectionPointer virConnectPtr);
    public String virConnectDomainXMLFromNative(ConnectionPointer virConnectPtr, String nativeFormat,
            String nativeConfig, int flags);
    public String virConnectDomainXMLToNative(ConnectionPointer virConnectPtr, String nativeFormat, String domainXML,
            int flags);
    
    // Global functions
    public int virGetVersion(LongByReference libVer, String type, LongByReference typeVer);
    public int virInitialize();
    public int virCopyLastError(virError error);
    public virError virGetLastError();
    public void virResetLastError();
    public void virSetErrorFunc(Pointer userData, VirErrorCallback callback);    

    // Domain functions
    public int virDomainAbortJob(DomainPointer virDomainPtr);
    public int virDomainAttachDevice(DomainPointer virDomainPtr, String deviceXML);
    public int virDomainAttachDeviceFlags(DomainPointer virDomainPtr, String deviceXML, int flags);    
    public int virDomainBlockStats(DomainPointer virDomainPtr, String path, virDomainBlockStats stats, int size);
    public int virDomainCoreDump(DomainPointer virDomainPtr, String to, int flags);
    public int virDomainCreate(DomainPointer virDomainPtr);
    public int virDomainCreateWithFlags(DomainPointer virDomainPtr, int flags);    
    public DomainPointer virDomainCreateLinux(ConnectionPointer virConnectPtr, String xmlDesc, int flags);
    public DomainPointer virDomainCreateXML(ConnectionPointer virConnectPtr, String xmlDesc, int flags);
    public DomainPointer virDomainDefineXML(ConnectionPointer virConnectPtr, String xmlDesc);
    public int virDomainDestroy(DomainPointer virDomainPtr);
    public int virDomainDetachDevice(DomainPointer virDomainPtr, String deviceXML);
    public int virDomainDetachDeviceFlags(DomainPointer virDomainPtr, String deviceXML, int flags);    
    public int virDomainFree(DomainPointer virDomainPtr);
    public int virDomainGetAutostart(DomainPointer virDomainPtr, IntByReference value);
    public ConnectionPointer virDomainGetConnect(DomainPointer virDomainPtr);  
    public int virDomainGetBlockInfo(DomainPointer virDomainPtr, String path, virDomainBlockInfo info, int flags);
    public int virDomainGetID(DomainPointer virDomainPtr);
    public int virDomainGetInfo(DomainPointer virDomainPtr, virDomainInfo vInfo);
    public int virDomainGetJobInfo(DomainPointer virDomainPtr, virDomainJobInfo vInfo);    
    public NativeLong virDomainGetMaxMemory(DomainPointer virDomainPtr);
    public int virDomainGetMaxVcpus(DomainPointer virDomainPtr);
    public String virDomainGetName(DomainPointer virDomainPtr);
    public String virDomainGetOSType(DomainPointer virDomainPtr);
    public int virDomainGetSchedulerParameters(DomainPointer virDomainPtr, virSchedParameter[] params,
            IntByReference nparams);
    public String virDomainGetSchedulerType(DomainPointer virDomainPtr, IntByReference nparams);
    public int virDomainGetUUID(DomainPointer virDomainPtr, byte[] uuidString);
    public int virDomainGetUUIDString(DomainPointer virDomainPtr, byte[] uuidString);
    public int virDomainGetVcpus(DomainPointer virDomainPtr, virVcpuInfo[] info, int maxInfo, byte[] cpumaps, int maplen);
    public String virDomainGetXMLDesc(DomainPointer virDomainPtr, int flags);
    public int virDomainHasCurrentSnapshot(DomainPointer virDomainPtr, int flags);
    public int virDomainHasManagedSaveImage(DomainPointer virDomainPtr, int flags);    
    public int virDomainInterfaceStats(DomainPointer virDomainPtr, String path, virDomainInterfaceStats stats, int size);
    public int virDomainIsActive(DomainPointer virDomainPtr);
    public int virDomainIsPersistent(DomainPointer virDomainPtr);    
    public DomainPointer virDomainLookupByID(ConnectionPointer virConnectPtr, int id);
    public DomainPointer virDomainLookupByName(ConnectionPointer virConnectPtr, String name);
    public DomainPointer virDomainLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public DomainPointer virDomainLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public int virDomainManagedSave(DomainPointer virDomainPtr, int flags);
    public int virDomainManagedSaveRemove(DomainPointer virDomainPtr, int flags);    
    public DomainPointer virDomainMigrate(DomainPointer virDomainPtr, ConnectionPointer virConnectPtr,
            NativeLong flags, String dname, String uri, NativeLong bandwidth);
    public int virDomainMigrateSetMaxDowntime(DomainPointer virDomainPtr, long downtime, int flags);    
    public int virDomainMigrateToURI(DomainPointer virDomainPtr, String duri, 
            NativeLong flags, String dname, NativeLong bandwidth);
    public int virDomainMemoryStats(DomainPointer virDomainPtr, virDomainMemoryStats[] stats, int nr_stats, int flags);
    public int virDomainPinVcpu(DomainPointer virDomainPtr, int vcpu, byte[] cpumap, int maplen);
    public int virDomainReboot(DomainPointer virDomainPtr, int flags);
    public int virDomainRestore(ConnectionPointer virConnectPtr, String from);
    public int virDomainRevertToSnapshot(DomainSnapshotPointer virDomainSnapshotPtr, int flags);
    public int virDomainResume(DomainPointer virDomainPtr);
    public int virDomainSave(DomainPointer virDomainPtr, String to);
    public int virDomainSetAutostart(DomainPointer virDomainPtr, int autoStart);
    public int virDomainSetMaxMemory(DomainPointer virDomainPtr, NativeLong maxMemory);
    public int virDomainSetMemory(DomainPointer virDomainPtr, NativeLong maxMemory);
    public int virDomainSetSchedulerParameters(DomainPointer virDomainPtr, virSchedParameter[] params, int nparams);
    public int virDomainSetVcpus(DomainPointer virDomainPtr, int nvcpus);
    public int virDomainShutdown(DomainPointer virDomainPtr);
    public int virDomainSuspend(DomainPointer virDomainPtr);
    public int virDomainUpdateDeviceFlags(DomainPointer virDomainPtr, String xml, int flags);
    public int virDomainUndefine(DomainPointer virDomainPtr);

    // Network functions
    public ConnectionPointer virNetworkGetConnect(NetworkPointer virnetworkPtr);
    public int virNetworkCreate(NetworkPointer virConnectPtr);
    public NetworkPointer virNetworkCreateXML(ConnectionPointer virConnectPtr, String xmlDesc);
    public NetworkPointer virNetworkDefineXML(ConnectionPointer virConnectPtr, String xmlDesc);
    public int virNetworkDestroy(NetworkPointer virConnectPtr);
    public int virNetworkFree(NetworkPointer virConnectPtr);
    public int virNetworkGetAutostart(NetworkPointer virNetworkPtr, IntByReference value);
    public String virNetworkGetBridgeName(NetworkPointer virNetworkPtr);
    public String virNetworkGetName(NetworkPointer virNetworkPtr);
    public int virNetworkGetUUID(NetworkPointer virNetworkPtr, byte[] uuidString);
    public int virNetworkGetUUIDString(NetworkPointer virNetworkPtr, byte[] uuidString);
    public String virNetworkGetXMLDesc(NetworkPointer virNetworkPtr, int flags);
    public int virNetworkIsActive(NetworkPointer virNetworkPtr);
    public int virNetworkIsPersistent(NetworkPointer virNetworkPtr);       
    public NetworkPointer virNetworkLookupByName(ConnectionPointer virConnectPtr, String name);
    public NetworkPointer virNetworkLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public NetworkPointer virNetworkLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public int virNetworkSetAutostart(NetworkPointer virConnectPtr, int autoStart);
    public int virNetworkUndefine(NetworkPointer virConnectPtr);

    // Node functions
    public int virNodeGetInfo(ConnectionPointer virConnectPtr, virNodeInfo virNodeInfo);
    public int virNodeGetCellsFreeMemory(ConnectionPointer virConnectPtr, LongByReference freeMems, int startCell,
            int maxCells);
    public long virNodeGetFreeMemory(ConnectionPointer virConnectPtr);

    // Node/Device functions
    public int virNodeNumOfDevices(ConnectionPointer virConnectPtr, String capabilityName, int flags);
    public int virNodeListDevices(ConnectionPointer virConnectPtr, String capabilityName, String[] names, int maxnames,
            int flags);
    public DevicePointer virNodeDeviceLookupByName(ConnectionPointer virConnectPtr, String name);
    public String virNodeDeviceGetName(DevicePointer virDevicePointer);
    public String virNodeDeviceGetParent(DevicePointer virDevicePointer);
    public int virNodeDeviceNumOfCaps(DevicePointer virDevicePointer);
    public int virNodeDeviceListCaps(DevicePointer virDevicePointer, String[] names, int maxNames);
    public String virNodeDeviceGetXMLDesc(DevicePointer virDevicePointer);
    public int virNodeDeviceFree(DevicePointer virDevicePointer);
    public int virNodeDeviceDettach(DevicePointer virDevicePointer);
    public int virNodeDeviceReAttach(DevicePointer virDevicePointer);
    public int virNodeDeviceReset(DevicePointer virDevicePointer);
    public DevicePointer virNodeDeviceCreateXML(ConnectionPointer virConnectPtr, String xml, int flags);
    public int virNodeDeviceDestroy(DevicePointer virDevicePointer);

    // Storage Pool
    public int virStoragePoolBuild(StoragePoolPointer storagePoolPtr, int flags);
    public int virStoragePoolCreate(StoragePoolPointer storagePoolPtr, int flags);
    public StoragePoolPointer virStoragePoolCreateXML(ConnectionPointer virConnectPtr, String xml, int flags);
    public StoragePoolPointer virStoragePoolDefineXML(ConnectionPointer virConnectPtr, String xml, int flags);
    public int virStoragePoolDelete(StoragePoolPointer storagePoolPtr, int flags);
    public int virStoragePoolDestroy(StoragePoolPointer storagePoolPtr);
    public int virStoragePoolFree(StoragePoolPointer storagePoolPtr);
    public int virStoragePoolGetAutostart(StoragePoolPointer storagePoolPtr, IntByReference value);
    public int virStoragePoolGetInfo(StoragePoolPointer storagePoolPtr, virStoragePoolInfo info);
    public String virStoragePoolGetName(StoragePoolPointer storagePoolPtr);
    public int virStoragePoolGetUUID(StoragePoolPointer storagePoolPtr, byte[] uuidString);
    public int virStoragePoolGetUUIDString(StoragePoolPointer storagePoolPtr, byte[] uuidString);
    public String virStoragePoolGetXMLDesc(StoragePoolPointer storagePoolPtr, int flags);
    public int virStoragePoolListVolumes(StoragePoolPointer storagePoolPtr, String[] names, int maxnames);
    public int virStoragePoolIsActive(StoragePoolPointer storagePoolPtr);
    public int virStoragePoolIsPersistent(StoragePoolPointer storagePoolPtr);      
    public StoragePoolPointer virStoragePoolLookupByName(ConnectionPointer virConnectPtr, String name);
    public StoragePoolPointer virStoragePoolLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public StoragePoolPointer virStoragePoolLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public StoragePoolPointer virStoragePoolLookupByVolume(StorageVolPointer storageVolPtr);
    public int virStoragePoolNumOfVolumes(StoragePoolPointer storagePoolPtr);
    public int virStoragePoolRefresh(StoragePoolPointer storagePoolPtr);
    public int virStoragePoolSetAutostart(StoragePoolPointer storagePoolPtr, int autostart);
    public int virStoragePoolUndefine(StoragePoolPointer storagePoolPtr);

    // Storage Vol
    public StorageVolPointer virStorageVolCreateXML(StoragePoolPointer storagePoolPtr, String xml, int flags);
    public StorageVolPointer virStorageVolCreateXMLFrom(StoragePoolPointer storagePoolPtr, String xml,
            StorageVolPointer cloneVolume, int flags);
    public int virStorageVolDelete(StorageVolPointer storageVolPtr, int flags);
    public int virStorageVolFree(StorageVolPointer storageVolPtr);
    public int virStorageVolGetInfo(StorageVolPointer storageVolPtr, virStorageVolInfo info);
    public String virStorageVolGetKey(StorageVolPointer storageVolPtr);
    public String virStorageVolGetName(StorageVolPointer storageVolPtr);
    public String virStorageVolGetPath(StorageVolPointer storageVolPtr);
    public String virStorageVolGetXMLDesc(StorageVolPointer storageVolPtr, int flags);
    public StorageVolPointer virStorageVolLookupByKey(ConnectionPointer virConnectPtr, String name);
    public StorageVolPointer virStorageVolLookupByName(StoragePoolPointer storagePoolPtr, String name);
    public StorageVolPointer virStorageVolLookupByPath(ConnectionPointer virConnectPtr, String path);
    public int virStorageVolWipe(StorageVolPointer storageVolPtr, int flags);

    // Interface Methods
    public int virInterfaceCreate(InterfacePointer virDevicePointer);
    public InterfacePointer virInterfaceDefineXML(ConnectionPointer virConnectPtr, String xml, int flags);    
    public int virInterfaceDestroy(InterfacePointer virDevicePointer);    
    public int virInterfaceFree(InterfacePointer virDevicePointer);    
    public String virInterfaceGetName(InterfacePointer virInterfacePtr);
    public String virInterfaceGetMACString(InterfacePointer virInterfacePtr);
    public String virInterfaceGetXMLDesc(InterfacePointer virInterfacePtr, int flags);
    public int virInterfaceIsActive(InterfacePointer virDevicePointer);
    public InterfacePointer virInterfaceLookupByMACString(ConnectionPointer virConnectPtr, String mac);
    public InterfacePointer virInterfaceLookupByName(ConnectionPointer virConnectPtr, String name);    
    public int virInterfaceUndefine(InterfacePointer virDevicePointer);
    
    // Secret Methods
    public ConnectionPointer virSecretGetConnect(SecretPointer virSecretPtr);
    public int virSecretFree(SecretPointer virSecretPtr);    
    public SecretPointer virSecretDefineXML(ConnectionPointer virConnectPtr, String xml, int flags);
    public int virSecretGetUUID(SecretPointer virSecretPtr, byte[] uuidString);
    public int virSecretGetUUIDString(SecretPointer virSecretPtr, byte[] uuidString);
    public String virSecretGetUsageID(SecretPointer virSecretPtr);   
    public String virSecretGetValue(SecretPointer virSecretPtr, NativeLong value_size, int flags);      
    public String virSecretGetXMLDesc(SecretPointer virSecretPtr, int flags);      
    public SecretPointer virSecretLookupByUsage(ConnectionPointer virConnectPtr, int usageType, String usageID);
    public SecretPointer virSecretLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public SecretPointer virSecretLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public int virSecretSetValue(SecretPointer virSecretPtr, String value, NativeLong value_size, int flags);        
    public int virSecretUndefine(SecretPointer virSecretPtr);
    
    //Stream Methods
    public int virStreamAbort(StreamPointer virStreamPtr) ;    
    public int virStreamEventAddCallback(StreamPointer virStreamPtr, int events, Libvirt.VirStreamEventCallback cb, 
            Pointer opaque, Libvirt.VirFreeCallback ff);
    public int virStreamEventUpdateCallback(StreamPointer virStreamPtr, int events);
    public int virStreamEventRemoveCallback(StreamPointer virStreamPtr);        
    public int virStreamFinish(StreamPointer virStreamPtr) ;
    public int virStreamFree(StreamPointer virStreamPtr) ;
    public StreamPointer virStreamNew(ConnectionPointer virConnectPtr, int flags) ;
    public int virStreamSend(StreamPointer virStreamPtr, String data, NativeLong size);
    public int virStreamSendAll(StreamPointer virStreamPtr, Libvirt.VirStreamSourceFunc handler, Pointer opaque);    
    public int virStreamRecv(StreamPointer virStreamPtr, byte[] data, NativeLong length);
    public int virStreamRecvAll(StreamPointer virStreamPtr, Libvirt.VirStreamSinkFunc handler, Pointer opaque);
    
    //DomainSnapshot Methods
    public DomainSnapshotPointer virDomainSnapshotCreateXML(DomainPointer virDomainPtr, String xmlDesc, int flags);
    public DomainSnapshotPointer virDomainSnapshotCurrent(DomainPointer virDomainPtr, int flags);
    public int virDomainSnapshotDelete(DomainSnapshotPointer virDomainSnapshotPtr, int flags);       
    public String virDomainSnapshotGetXMLDesc(DomainSnapshotPointer virDomainSnapshotPtr, int flags);
    public int virDomainSnapshotFree(DomainSnapshotPointer virDomainSnapshotPtr);    
    public int virDomainSnapshotListNames(DomainPointer virDomainPtr, String[] names, int nameslen, int flags);
    public DomainSnapshotPointer virDomainSnapshotLookupByName(DomainPointer virDomainPtr, String name, int flags);    
    public int virDomainSnapshotNum(DomainPointer virDomainPtr, int flags);
    
    // Network Filter Methods
    public String virNWFilterGetXMLDesc(NetworkFilterPointer virNWFilterPtr, int flags);
    public NetworkFilterPointer virNWFilterDefineXML(ConnectionPointer virConnectPtr, String xml);    
    public int virNWFilterFree(NetworkFilterPointer virNWFilterPtr);
    public NetworkFilterPointer virNWFilterLookupByName(ConnectionPointer virConnectPtr, String name);
    public NetworkFilterPointer virNWFilterLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public NetworkFilterPointer virNWFilterLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public String virNWFilterGetName(NetworkFilterPointer virNWFilterPtr);   
    public int virNWFilterGetUUID(NetworkFilterPointer virNWFilterPtr, byte[] uuidString);
    public int virNWFilterGetUUIDString(NetworkFilterPointer virNWFilterPtr, byte[] uuidString);    
    public int virNWFilterUndefine(NetworkFilterPointer virNWFilterPtr);    
}
