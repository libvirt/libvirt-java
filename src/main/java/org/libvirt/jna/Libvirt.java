package org.libvirt.jna;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;

/**
 * The libvirt interface which is exposed via JNA.
 * Known api calls to be missing
 * LIBVIRT_0.1.0
 *   virConnSetErrorFunc
 *   virSetErrorFunc
 *   virDefaultErrorFunc
 *   virConnCopyLastError
 *   virFreeError
 * LIBVIRT_0.4.2
 *   virDomainBlockPeek
 *   virDomainMemoryPeek
 * LIBVIRT_0_5.0
 *   virEventRegisterImpl
 *   virConnectDomainEventRegister
 *   virConnectDomainEventDeregister
 * LIBVIRT_0.6.0
 *   virConnectRef
 *   virDomainRef
 *   virNetworkRef
 *   virStoragePoolRef
 *   virStorageVolRef
 *   virNodeDeviceRef
 * LIBVIRT_0.6.1
 *   virFreeError
 *   virSaveLastError
 *   virDomainGetSecurityLabel;
 *   virNodeGetSecurityModel;
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

    Libvirt INSTANCE = (Libvirt) Native.loadLibrary("virt", Libvirt.class);
    
    // Constants we need
    public static int VIR_UUID_BUFLEN = 16;
    public static int VIR_UUID_STRING_BUFLEN = (36 + 1);
    public static int VIR_DOMAIN_SCHED_FIELD_LENGTH = 80;

    // Connection Functions
    public int virConnCopyLastError(ConnectionPointer virConnectPtr, virError to);
    public int virConnectClose(ConnectionPointer virConnectPtr);
    //TODO Post 0.5.1
    //public String virConnectDomainXMLFromNative(ConnectionPointer virConnectPtr, String nativeFormat, String nativeConfig, int flags) ;
    //public String virConnectDomainXMLToNative(ConnectionPointer virConnectPtr, String nativeFormat, String domainXML, int flags) ;
    public String virConnectFindStoragePoolSources(ConnectionPointer virConnectPtr, String type, String srcSpec, int flags) ;
    public String virConnectGetCapabilities(ConnectionPointer virConnectPtr);
    public String virConnectGetHostname(ConnectionPointer virConnectPtr);
    public int virConnectGetMaxVcpus(ConnectionPointer virConnectPtr, String type);
    public String virConnectGetType(ConnectionPointer virConnectPtr);
    public String virConnectGetURI(ConnectionPointer virConnectPtr);
    public int virConnectGetVersion(ConnectionPointer virConnectPtr, LongByReference hvVer);
    public int virConnectListDefinedDomains(ConnectionPointer virConnectPtr, String[] name, int maxnames);
    public int virConnectListDefinedNetworks(ConnectionPointer virConnectPtr, String[] name, int maxnames);
    public int virConnectListDefinedStoragePools(ConnectionPointer virConnectPtr, String[] names, int maxnames);
    public int virConnectListDomains(ConnectionPointer virConnectPtr, int[] ids, int maxnames);
    //TODO Post 0.5.1
    //public int virConnectListInterfaces(ConnectionPointer virConnectPtr, String[] name, int maxnames);    
    public int virConnectListNetworks(ConnectionPointer virConnectPtr, String[] name, int maxnames);
    public int virConnectListStoragePools(ConnectionPointer virConnectPtr, String[] names, int maxnames);
    public int virConnectNumOfDefinedDomains(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDefinedNetworks(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDefinedStoragePools(ConnectionPointer virConnectPtr);
    public int virConnectNumOfDomains(ConnectionPointer virConnectPtr);
    //TODO Post 0.5.1
    //public int virConnectNumOfInterfaces(ConnectionPointer virConnectPtr);    
    public int virConnectNumOfNetworks(ConnectionPointer virConnectPtr);
    public int virConnectNumOfStoragePools(ConnectionPointer virConnectPtr);
    public ConnectionPointer virConnectOpen(String name);
    public ConnectionPointer virConnectOpenAuth(String name, virConnectAuth auth, int flags);
    public ConnectionPointer virConnectOpenReadOnly(String name);
    public virError virConnGetLastError(ConnectionPointer virConnectPtr);
    public int virConnResetLastError(ConnectionPointer virConnectPtr);

    // Global functions
    public int virGetVersion(LongByReference libVer, String type, LongByReference typeVer);
    public int virInitialize();
    public int virCopyLastError(virError error);
    public virError virGetLastError();    
    public void virResetLastError();

    // Domain functions
    public ConnectionPointer virDomainGetConnect(DomainPointer virDomainPtr) ;
    public int virDomainAttachDevice(DomainPointer virDomainPtr, String deviceXML);
    public int virDomainBlockStats(DomainPointer virDomainPtr, String path, virDomainBlockStats stats, int size);
    public int virDomainCoreDump(DomainPointer virDomainPtr, String to, int flags);
    public int virDomainCreate(DomainPointer virDomainPtr);
    public DomainPointer virDomainCreateLinux(ConnectionPointer virConnectPtr, String xmlDesc, int flags);
    public DomainPointer virDomainCreateXML(ConnectionPointer virConnectPtr, String xmlDesc, int flags);
    public DomainPointer virDomainDefineXML(ConnectionPointer virConnectPtr, String xmlDesc);
    public int virDomainDestroy(DomainPointer virDomainPtr);
    public int virDomainDetachDevice(DomainPointer virDomainPtr, String deviceXML);
    public int virDomainFree(DomainPointer virDomainPtr);
    public int virDomainGetAutostart(DomainPointer virDomainPtr, IntByReference value);
    public int virDomainGetID(DomainPointer virDomainPtr);
    public int virDomainGetInfo(DomainPointer virDomainPtr, virDomainInfo vInfo);
    public NativeLong virDomainGetMaxMemory(DomainPointer virDomainPtr);
    public int virDomainGetMaxVcpus(DomainPointer virDomainPtr);
    public String virDomainGetName(DomainPointer virDomainPtr);
    public String virDomainGetOSType(DomainPointer virDomainPtr);
    public int virDomainGetSchedulerParameters(DomainPointer virDomainPtr, virSchedParameter[] params, IntByReference nparams);
    public String virDomainGetSchedulerType(DomainPointer virDomainPtr, IntByReference nparams);
    public int virDomainGetUUID(DomainPointer virDomainPtr, byte[] uuidString);
    public int virDomainGetUUIDString(DomainPointer virDomainPtr, byte[] uuidString);
    public int virDomainGetVcpus(DomainPointer virDomainPtr, virVcpuInfo[] info, int maxInfo, byte[] cpumaps, int maplen);
    public String virDomainGetXMLDesc(DomainPointer virDomainPtr, int flags);
    public int virDomainInterfaceStats(DomainPointer virDomainPtr, String path, virDomainInterfaceStats stats, int size);
    public DomainPointer virDomainLookupByID(ConnectionPointer virConnectPtr, int id);
    public DomainPointer virDomainLookupByName(ConnectionPointer virConnectPtr, String name);
    public DomainPointer virDomainLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public DomainPointer virDomainLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public DomainPointer virDomainMigrate(DomainPointer virDomainPtr, ConnectionPointer virConnectPtr, NativeLong flags, String dname, String uri, NativeLong bandwidth);
    public int virDomainPinVcpu(DomainPointer virDomainPtr, int vcpu, byte[] cpumap, int maplen);
    public int virDomainReboot(DomainPointer virDomainPtr, int flags);
    public int virDomainRestore(ConnectionPointer virConnectPtr, String from);
    public int virDomainResume(DomainPointer virDomainPtr);
    public int virDomainSave(DomainPointer virDomainPtr, String to);
    public int virDomainSetAutostart(DomainPointer virDomainPtr, int autoStart);
    public int virDomainSetMaxMemory(DomainPointer virDomainPtr, NativeLong maxMemory);
    public int virDomainSetMemory(DomainPointer virDomainPtr, NativeLong maxMemory);
    public int virDomainSetSchedulerParameters(DomainPointer virDomainPtr, virSchedParameter[] params, int nparams);
    public int virDomainSetVcpus(DomainPointer virDomainPtr, int nvcpus);
    public int virDomainShutdown(DomainPointer virDomainPtr);
    public int virDomainSuspend(DomainPointer virDomainPtr);
    public int virDomainUndefine(DomainPointer virDomainPtr);

    // Network functions
    public ConnectionPointer virNetworkGetConnect(NetworkPointer virnetworkPtr) ;    
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
    public NetworkPointer virNetworkLookupByName(ConnectionPointer virConnectPtr, String name);
    public NetworkPointer virNetworkLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);
    public NetworkPointer virNetworkLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);
    public int virNetworkSetAutostart(NetworkPointer virConnectPtr, int autoStart);
    public int virNetworkUndefine(NetworkPointer virConnectPtr);

    // Node functions
    public int virNodeGetInfo(ConnectionPointer virConnectPtr, virNodeInfo virNodeInfo);
    public int virNodeGetCellsFreeMemory(ConnectionPointer virConnectPtr, LongByReference freeMems, int startCell, int maxCells);
    public long virNodeGetFreeMemory(ConnectionPointer virConnectPtr) ;
    
    // Node/Device functions
    public int virNodeNumOfDevices(ConnectionPointer virConnectPtr, String capabilityName, int flags) ;
    public int virNodeListDevices(ConnectionPointer virConnectPtr, String capabilityName, String[] names, int maxnames, int flags) ;
    public DevicePointer virNodeDeviceLookupByName(ConnectionPointer virConnectPtr, String name) ;
    public String virNodeDeviceGetName(DevicePointer virDevicePointer) ;
    public String virNodeDeviceGetParent(DevicePointer virDevicePointer) ;
    public int virNodeDeviceNumOfCaps(DevicePointer virDevicePointer) ;
    public int virNodeDeviceListCaps(DevicePointer virDevicePointer, String[] names, int maxNames) ;
    public String virNodeDeviceGetXMLDesc(DevicePointer virDevicePointer) ;
    public int virNodeDeviceFree(DevicePointer virDevicePointer) ; 
    public int virNodeDeviceDettach(DevicePointer virDevicePointer) ; 
    public int virNodeDeviceReAttach(DevicePointer virDevicePointer) ;     
    public int virNodeDeviceReset(DevicePointer virDevicePointer) ;     

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
}
