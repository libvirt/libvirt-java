package org.libvirt.jna;

import java.nio.ByteBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import org.libvirt.jna.callbacks.VirConnectCloseFunc;
import org.libvirt.jna.callbacks.VirDomainEventCallback;
import org.libvirt.jna.callbacks.VirErrorCallback;
import org.libvirt.jna.callbacks.VirEventTimeoutCallback;
import org.libvirt.jna.callbacks.VirFreeCallback;
import org.libvirt.jna.callbacks.VirStreamEventCallback;
import org.libvirt.jna.callbacks.VirStreamSinkFunc;
import org.libvirt.jna.callbacks.VirStreamSourceFunc;
import org.libvirt.jna.pointers.ConnectionPointer;
import org.libvirt.jna.pointers.DevicePointer;
import org.libvirt.jna.pointers.DomainPointer;
import org.libvirt.jna.pointers.DomainSnapshotPointer;
import org.libvirt.jna.pointers.InterfacePointer;
import org.libvirt.jna.pointers.NetworkFilterPointer;
import org.libvirt.jna.pointers.NetworkPointer;
import org.libvirt.jna.pointers.SecretPointer;
import org.libvirt.jna.pointers.StoragePoolPointer;
import org.libvirt.jna.pointers.StorageVolPointer;
import org.libvirt.jna.pointers.StreamPointer;
import org.libvirt.jna.structures.virConnectAuth;
import org.libvirt.jna.structures.virDomainBlockInfo;
import org.libvirt.jna.structures.virDomainBlockJobInfo;
import org.libvirt.jna.structures.virDomainBlockStats;
import org.libvirt.jna.structures.virDomainInfo;
import org.libvirt.jna.structures.virDomainInterfaceStats;
import org.libvirt.jna.structures.virDomainJobInfo;
import org.libvirt.jna.structures.virDomainMemoryStats;
import org.libvirt.jna.structures.virError;
import org.libvirt.jna.structures.virNodeInfo;
import org.libvirt.jna.structures.virSecurityLabel;
import org.libvirt.jna.structures.virSecurityModel;
import org.libvirt.jna.structures.virStoragePoolInfo;
import org.libvirt.jna.structures.virStorageVolInfo;
import org.libvirt.jna.structures.virTypedParameter;
import org.libvirt.jna.structures.virVcpuInfo;
import org.libvirt.jna.types.CString;
import org.libvirt.jna.types.SizeT;
import org.libvirt.jna.types.SizeTByReference;

/**
 * The libvirt interface which is exposed via JNA.
 */
public interface Libvirt extends Library {

    Libvirt INSTANCE = (Libvirt) Native.loadLibrary(Platform.isWindows() ? "virt-0" : "virt", Libvirt.class);

    // Constants we need
    int VIR_UUID_BUFLEN = 16;
    int VIR_UUID_STRING_BUFLEN = (36 + 1);
    int VIR_TYPED_PARAM_FIELD_LENGTH = 80;

    ConnectionPointer virConnectOpen(String name);

    ConnectionPointer virConnectOpenAuth(String name, virConnectAuth auth, int flags);

    ConnectionPointer virConnectOpenReadOnly(String name);

    CString virConnectBaselineCPU(ConnectionPointer virConnectPtr, String[] xmlCPUs, int ncpus, int flags);

    CString virConnectDomainXMLFromNative(ConnectionPointer virConnectPtr, String nativeFormat, String nativeConfig, int flags);

    CString virConnectDomainXMLToNative(ConnectionPointer virConnectPtr, String nativeFormat, String domainXML, int flags);

    CString virConnectFindStoragePoolSources(ConnectionPointer virConnectPtr, String type, String srcSpec, int flags);

    CString virConnectGetCapabilities(ConnectionPointer virConnectPtr);

    CString virConnectGetHostname(ConnectionPointer virConnectPtr);

    CString virConnectGetSysinfo(ConnectionPointer virConnectPtr, int flags);

    CString virConnectGetURI(ConnectionPointer virConnectPtr);

    CString virDomainGetOSType(DomainPointer virDomainPtr);

    CString virDomainGetSchedulerType(DomainPointer virDomainPtr, IntByReference nparams);

    CString virDomainGetXMLDesc(DomainPointer virDomainPtr, int flags);

    CString virDomainScreenshot(DomainPointer virDomainPtr, StreamPointer virStreamPtr, int screen, int flags);

    CString virDomainSnapshotGetXMLDesc(DomainSnapshotPointer virDomainSnapshotPtr, int flags);

    CString virInterfaceGetXMLDesc(InterfacePointer virInterfacePtr, int flags);

    CString virNetworkGetBridgeName(NetworkPointer virNetworkPtr);

    CString virNetworkGetXMLDesc(NetworkPointer virNetworkPtr, int flags);

    CString virNodeDeviceGetXMLDesc(DevicePointer virDevicePointer, int flags);

    CString virNWFilterGetXMLDesc(NetworkFilterPointer virNWFilterPtr, int flags);

    CString virSecretGetXMLDesc(SecretPointer virSecretPtr, int flags);

    CString virStoragePoolGetXMLDesc(StoragePoolPointer storagePoolPtr, int flags);

    CString virStorageVolGetPath(StorageVolPointer storageVolPtr);

    CString virStorageVolGetXMLDesc(StorageVolPointer storageVolPtr, int flags);

    DevicePointer virNodeDeviceCreateXML(ConnectionPointer virConnectPtr, String xml, int flags);

    DevicePointer virNodeDeviceLookupByName(ConnectionPointer virConnectPtr, String name);

    DomainPointer virDomainCreateLinux(ConnectionPointer virConnectPtr, String xmlDesc, int flags);

    DomainPointer virDomainCreateXML(ConnectionPointer virConnectPtr, String xmlDesc, int flags);

    DomainPointer virDomainDefineXML(ConnectionPointer virConnectPtr, String xmlDesc);

    DomainPointer virDomainLookupByID(ConnectionPointer virConnectPtr, int id);

    DomainPointer virDomainLookupByName(ConnectionPointer virConnectPtr, String name);

    DomainPointer virDomainLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);

    DomainPointer virDomainLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);

    DomainPointer virDomainMigrate(DomainPointer virDomainPtr, ConnectionPointer virConnectPtr, NativeLong flags, String dname, String uri, NativeLong bandwidth);

    DomainPointer virDomainMigrate2(DomainPointer virDomainPtr, ConnectionPointer virConnectPtr, String dxml, NativeLong flags, String dname, String uri, NativeLong bandwidth);

    DomainSnapshotPointer virDomainSnapshotCreateXML(DomainPointer virDomainPtr, String xmlDesc, int flags);

    DomainSnapshotPointer virDomainSnapshotCurrent(DomainPointer virDomainPtr, int flags);

    DomainSnapshotPointer virDomainSnapshotLookupByName(DomainPointer virDomainPtr, String name, int flags);

    int virConnectClose(ConnectionPointer virConnectPtr);

    int virConnectCompareCPU(ConnectionPointer virConnectPtr, String xmlDesc, int flags);

    int virConnectDomainEventDeregisterAny(ConnectionPointer virConnectPtr, int callbackID);

    int virConnectDomainEventRegisterAny(ConnectionPointer virConnectPtr, DomainPointer virDomainPtr, int eventID, VirDomainEventCallback cb, Pointer opaque, VirFreeCallback freecb);

    int virConnectGetLibVersion(ConnectionPointer virConnectPtr, LongByReference libVer);

    int virConnectGetMaxVcpus(ConnectionPointer virConnectPtr, String type);

    int virConnectGetVersion(ConnectionPointer virConnectPtr, LongByReference hvVer);

    int virConnectIsAlive(ConnectionPointer virConnectPtr);

    int virConnectIsEncrypted(ConnectionPointer virConnectPtr);

    int virConnectIsSecure(ConnectionPointer virConnectPtr);

    int virConnectListDefinedDomains(ConnectionPointer virConnectPtr, CString[] name, int maxnames);

    int virConnectListDefinedInterfaces(ConnectionPointer virConnectPtr, CString[] name, int maxNames);

    int virConnectListDefinedNetworks(ConnectionPointer virConnectPtr, CString[] name, int maxnames);

    int virConnectListDefinedStoragePools(ConnectionPointer virConnectPtr, CString[] names, int maxnames);

    int virConnectListDomains(ConnectionPointer virConnectPtr, int[] ids, int maxnames);

    int virConnectListInterfaces(ConnectionPointer virConnectPtr, CString[] name, int maxNames);

    int virConnectListNetworks(ConnectionPointer virConnectPtr, CString[] name, int maxnames);

    int virConnectListNWFilters(ConnectionPointer virConnectPtr, CString[] name, int maxnames);

    int virConnectListSecrets(ConnectionPointer virConnectPtr, CString[] uids, int maxUids);

    int virConnectListStoragePools(ConnectionPointer virConnectPtr, CString[] names, int maxnames);

    int virConnectNumOfDefinedDomains(ConnectionPointer virConnectPtr);

    int virConnectNumOfDefinedInterfaces(ConnectionPointer virConnectPtr);

    int virConnectNumOfDefinedNetworks(ConnectionPointer virConnectPtr);

    int virConnectNumOfDefinedStoragePools(ConnectionPointer virConnectPtr);

    int virConnectNumOfDomains(ConnectionPointer virConnectPtr);

    int virConnectNumOfInterfaces(ConnectionPointer virConnectPtr);

    int virConnectNumOfNetworks(ConnectionPointer virConnectPtr);

    int virConnectNumOfNWFilters(ConnectionPointer virConnectPtr);

    int virConnectNumOfSecrets(ConnectionPointer virConnectPtr);

    int virConnectNumOfStoragePools(ConnectionPointer virConnectPtr);

    int virConnectRegisterCloseCallback(ConnectionPointer virConnectPtr, VirConnectCloseFunc cb, Pointer opaque, Pointer freeOpaque);

    int virConnectSetKeepAlive(ConnectionPointer virConnectPtr, int interval, int count);

    int virConnectUnregisterCloseCallback(ConnectionPointer virConnectPtr, VirConnectCloseFunc cb);

    int virCopyLastError(virError error);

    int virDomainAbortJob(DomainPointer virDomainPtr);

    int virDomainAttachDevice(DomainPointer virDomainPtr, String deviceXML);

    int virDomainAttachDeviceFlags(DomainPointer virDomainPtr, String deviceXML, int flags);

    int virDomainBlockCopy(DomainPointer virDomainPtr, String disk, String destxml, virTypedParameter[] params, int nparams, int flags);

    int virDomainBlockJobAbort(DomainPointer virDomainPtr, String disk, int flags);

    int virDomainBlockPeek(DomainPointer virDomainPtr, String disk, long offset, SizeT size, ByteBuffer buffer, int flags);

    int virDomainBlockResize(DomainPointer virDomainPtr, String disk, long size, int flags);

    int virDomainBlockStats(DomainPointer virDomainPtr, String path, virDomainBlockStats stats, SizeT size);

    int virDomainCoreDump(DomainPointer virDomainPtr, String to, int flags);

    int virDomainCreate(DomainPointer virDomainPtr);

    int virDomainCreateWithFlags(DomainPointer virDomainPtr, int flags);

    int virDomainDestroy(DomainPointer virDomainPtr);

    int virDomainDetachDevice(DomainPointer virDomainPtr, String deviceXML);

    int virDomainDetachDeviceFlags(DomainPointer virDomainPtr, String deviceXML, int flags);

    int virDomainFree(DomainPointer virDomainPtr);

    int virDomainGetAutostart(DomainPointer virDomainPtr, IntByReference value);

    int virDomainGetBlockInfo(DomainPointer virDomainPtr, String path, virDomainBlockInfo info, int flags);

    int virDomainGetBlockJobInfo(DomainPointer virDomainPtr, String disk, virDomainBlockJobInfo info, int flags);

    int virDomainGetID(DomainPointer virDomainPtr);

    int virDomainGetInfo(DomainPointer virDomainPtr, virDomainInfo vInfo);

    int virDomainGetJobInfo(DomainPointer virDomainPtr, virDomainJobInfo vInfo);

    int virDomainGetMaxVcpus(DomainPointer virDomainPtr);

    int virDomainGetSchedulerParameters(DomainPointer virDomainPtr, virTypedParameter[] params, IntByReference nparams);

    int virDomainGetSecurityLabel(DomainPointer virDomainPtr, virSecurityLabel seclabel);

    int virDomainGetUUID(DomainPointer virDomainPtr, byte[] uuidString);

    int virDomainGetUUIDString(DomainPointer virDomainPtr, byte[] uuidString);

    int virDomainGetVcpus(DomainPointer virDomainPtr, virVcpuInfo[] info, int maxInfo, byte[] cpumaps, int maplen);

    int virDomainHasCurrentSnapshot(DomainPointer virDomainPtr, int flags);

    int virDomainHasManagedSaveImage(DomainPointer virDomainPtr, int flags);

    int virDomainInterfaceStats(DomainPointer virDomainPtr, String path, virDomainInterfaceStats stats, SizeT size);

    int virDomainIsActive(DomainPointer virDomainPtr);

    int virDomainIsPersistent(DomainPointer virDomainPtr);

    int virDomainIsUpdated(DomainPointer virDomainPtr);

    int virDomainManagedSave(DomainPointer virDomainPtr, int flags);

    int virDomainManagedSaveRemove(DomainPointer virDomainPtr, int flags);

    int virDomainMemoryPeek(DomainPointer virDomainPtr, long start, SizeT size, ByteBuffer buffer, int flags);

    int virDomainMemoryStats(DomainPointer virDomainPtr, virDomainMemoryStats[] stats, int nr_stats, int flags);

    int virDomainMigrateSetMaxDowntime(DomainPointer virDomainPtr, long downtime, int flags);

    int virDomainMigrateToURI(DomainPointer virDomainPtr, String duri, NativeLong flags, String dname, NativeLong bandwidth);

    int virDomainMigrateToURI2(DomainPointer virDomainPtr, String dconnuri, String miguri, String dxml, NativeLong flags, String dname, NativeLong bandwidth);

    int virDomainPinVcpu(DomainPointer virDomainPtr, int vcpu, byte[] cpumap, int maplen);

    int virDomainPMSuspendForDuration(DomainPointer virDomainPtr, int target, long duration, int flags);

    int virDomainPMWakeup(DomainPointer virDomainPtr, int flags);

    int virDomainReboot(DomainPointer virDomainPtr, int flags);

    int virDomainRef(DomainPointer virDomainPtr);

    int virDomainReset(DomainPointer virDomainPtr, int flags);

    int virDomainRestore(ConnectionPointer virConnectPtr, String from);

    int virDomainResume(DomainPointer virDomainPtr);

    int virDomainRevertToSnapshot(DomainSnapshotPointer virDomainSnapshotPtr, int flags);

    int virDomainSave(DomainPointer virDomainPtr, String to);

    int virDomainSendKey(DomainPointer virDomainPtr, int codeset, int holdtime, int[] keycodes, int nkeycodes, int flags);

    int virDomainSetAutostart(DomainPointer virDomainPtr, int autoStart);

    int virDomainSetMaxMemory(DomainPointer virDomainPtr, NativeLong maxMemory);

    int virDomainSetMemory(DomainPointer virDomainPtr, NativeLong maxMemory);

    int virDomainSetSchedulerParameters(DomainPointer virDomainPtr, virTypedParameter[] params, int nparams);

    int virDomainSetVcpus(DomainPointer virDomainPtr, int nvcpus);

    int virDomainShutdown(DomainPointer virDomainPtr);

    int virDomainSnapshotDelete(DomainSnapshotPointer virDomainSnapshotPtr, int flags);

    int virDomainSnapshotFree(DomainSnapshotPointer virDomainSnapshotPtr);

    int virDomainSnapshotListNames(DomainPointer virDomainPtr, CString[] names, int nameslen, int flags);

    int virDomainSnapshotNum(DomainPointer virDomainPtr, int flags);

    int virDomainSuspend(DomainPointer virDomainPtr);

    int virDomainUndefine(DomainPointer virDomainPtr);

    int virDomainUndefineFlags(DomainPointer virDomainPtr, int flags);

    int virDomainUpdateDeviceFlags(DomainPointer virDomainPtr, String xml, int flags);

    int virEventAddTimeout(int milliSeconds, VirEventTimeoutCallback cb, Pointer opaque, Pointer ff);

    int virEventRegisterDefaultImpl();

    int virEventRemoveTimeout(int timer);

    int virEventRunDefaultImpl();

    int virGetVersion(LongByReference libVer, String type, LongByReference typeVer);

    int virInitialize();

    int virInterfaceCreate(InterfacePointer virDevicePointer, int flags);

    int virInterfaceDestroy(InterfacePointer virDevicePointer, int flags);

    int virInterfaceFree(InterfacePointer virDevicePointer);

    int virInterfaceIsActive(InterfacePointer virDevicePointer);

    int virInterfaceUndefine(InterfacePointer virDevicePointer);

    int virNetworkCreate(NetworkPointer virConnectPtr);

    int virNetworkDestroy(NetworkPointer virConnectPtr);

    int virNetworkFree(NetworkPointer virConnectPtr);

    int virNetworkGetAutostart(NetworkPointer virNetworkPtr, IntByReference value);

    int virNetworkGetUUID(NetworkPointer virNetworkPtr, byte[] uuidString);

    int virNetworkGetUUIDString(NetworkPointer virNetworkPtr, byte[] uuidString);

    int virNetworkIsActive(NetworkPointer virNetworkPtr);

    int virNetworkIsPersistent(NetworkPointer virNetworkPtr);

    int virNetworkSetAutostart(NetworkPointer virConnectPtr, int autoStart);

    int virNetworkUndefine(NetworkPointer virConnectPtr);

    int virNodeDeviceDestroy(DevicePointer virDevicePointer);

    int virNodeDeviceDettach(DevicePointer virDevicePointer);

    int virNodeDeviceFree(DevicePointer virDevicePointer);

    int virNodeDeviceListCaps(DevicePointer virDevicePointer, CString[] names, int maxNames);

    int virNodeDeviceNumOfCaps(DevicePointer virDevicePointer);

    int virNodeDeviceReAttach(DevicePointer virDevicePointer);

    int virNodeDeviceReset(DevicePointer virDevicePointer);

    int virNodeGetCellsFreeMemory(ConnectionPointer virConnectPtr, LongByReference freeMems, int startCell, int maxCells);

    int virNodeGetInfo(ConnectionPointer virConnectPtr, virNodeInfo virNodeInfo);

    int virNodeGetSecurityModel(ConnectionPointer virConnectPtr, virSecurityModel secmodel);

    int virNodeListDevices(ConnectionPointer virConnectPtr, String capabilityName, CString[] names, int maxnames, int flags);

    int virNodeNumOfDevices(ConnectionPointer virConnectPtr, String capabilityName, int flags);

    int virNWFilterFree(NetworkFilterPointer virNWFilterPtr);

    int virNWFilterGetUUID(NetworkFilterPointer virNWFilterPtr, byte[] uuidString);

    int virNWFilterGetUUIDString(NetworkFilterPointer virNWFilterPtr, byte[] uuidString);

    int virNWFilterUndefine(NetworkFilterPointer virNWFilterPtr);

    int virSecretFree(SecretPointer virSecretPtr);

    int virSecretGetUsageType(SecretPointer virSecretPtr);

    int virSecretGetUUID(SecretPointer virSecretPtr, byte[] uuidString);

    int virSecretGetUUIDString(SecretPointer virSecretPtr, byte[] uuidString);

    int virSecretSetValue(SecretPointer virSecretPtr, byte[] value, SizeT value_size, int flags);

    int virSecretSetValue(SecretPointer virSecretPtr, String value, SizeT value_size, int flags);

    int virSecretUndefine(SecretPointer virSecretPtr);

    int virStoragePoolBuild(StoragePoolPointer storagePoolPtr, int flags);

    int virStoragePoolCreate(StoragePoolPointer storagePoolPtr, int flags);

    int virStoragePoolDelete(StoragePoolPointer storagePoolPtr, int flags);

    int virStoragePoolDestroy(StoragePoolPointer storagePoolPtr);

    int virStoragePoolFree(StoragePoolPointer storagePoolPtr);

    int virStoragePoolGetAutostart(StoragePoolPointer storagePoolPtr, IntByReference value);

    int virStoragePoolGetInfo(StoragePoolPointer storagePoolPtr, virStoragePoolInfo info);

    int virStoragePoolGetUUID(StoragePoolPointer storagePoolPtr, byte[] uuidString);

    int virStoragePoolGetUUIDString(StoragePoolPointer storagePoolPtr, byte[] uuidString);

    int virStoragePoolIsActive(StoragePoolPointer storagePoolPtr);

    int virStoragePoolIsPersistent(StoragePoolPointer storagePoolPtr);

    int virStoragePoolListVolumes(StoragePoolPointer storagePoolPtr, CString[] names, int maxnames);

    int virStoragePoolNumOfVolumes(StoragePoolPointer storagePoolPtr);

    int virStoragePoolRefresh(StoragePoolPointer storagePoolPtr, int flags);

    int virStoragePoolSetAutostart(StoragePoolPointer storagePoolPtr, int autostart);

    int virStoragePoolUndefine(StoragePoolPointer storagePoolPtr);

    int virStorageVolDelete(StorageVolPointer storageVolPtr, int flags);

    int virStorageVolFree(StorageVolPointer storageVolPtr);

    int virStorageVolGetInfo(StorageVolPointer storageVolPtr, virStorageVolInfo info);

    int virStorageVolResize(StorageVolPointer storageVolPtr, long capacity, int flags);

    int virStorageVolWipe(StorageVolPointer storageVolPtr, int flags);

    int virStreamAbort(StreamPointer virStreamPtr);

    int virStreamEventAddCallback(StreamPointer virStreamPtr, int events, VirStreamEventCallback cb, Pointer opaque, VirFreeCallback ff);

    int virStreamEventRemoveCallback(StreamPointer virStreamPtr);

    int virStreamEventUpdateCallback(StreamPointer virStreamPtr, int events);

    int virStreamFinish(StreamPointer virStreamPtr);

    int virStreamFree(StreamPointer virStreamPtr);

    int virStreamRecv(StreamPointer virStreamPtr, ByteBuffer data, SizeT length);

    int virStreamRecvAll(StreamPointer virStreamPtr, VirStreamSinkFunc handler, Pointer opaque);

    int virStreamSend(StreamPointer virStreamPtr, ByteBuffer data, SizeT size);

    int virStreamSendAll(StreamPointer virStreamPtr, VirStreamSourceFunc handler, Pointer opaque);

    InterfacePointer virInterfaceDefineXML(ConnectionPointer virConnectPtr, String xml, int flags);

    InterfacePointer virInterfaceLookupByMACString(ConnectionPointer virConnectPtr, String mac);

    InterfacePointer virInterfaceLookupByName(ConnectionPointer virConnectPtr, String name);

    long virNodeGetFreeMemory(ConnectionPointer virConnectPtr);

    NativeLong virDomainGetMaxMemory(DomainPointer virDomainPtr);

    NetworkFilterPointer virNWFilterDefineXML(ConnectionPointer virConnectPtr, String xml);

    NetworkFilterPointer virNWFilterLookupByName(ConnectionPointer virConnectPtr, String name);

    NetworkFilterPointer virNWFilterLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);

    NetworkFilterPointer virNWFilterLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);

    NetworkPointer virNetworkCreateXML(ConnectionPointer virConnectPtr, String xmlDesc);

    NetworkPointer virNetworkDefineXML(ConnectionPointer virConnectPtr, String xmlDesc);

    NetworkPointer virNetworkLookupByName(ConnectionPointer virConnectPtr, String name);

    NetworkPointer virNetworkLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);

    NetworkPointer virNetworkLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);

    Pointer virSecretGetValue(SecretPointer virSecretPtr, SizeTByReference value_size, int flags);

    SecretPointer virSecretDefineXML(ConnectionPointer virConnectPtr, String xml, int flags);

    SecretPointer virSecretLookupByUsage(ConnectionPointer virConnectPtr, int usageType, String usageID);

    SecretPointer virSecretLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);

    SecretPointer virSecretLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);

    StoragePoolPointer virStoragePoolCreateXML(ConnectionPointer virConnectPtr, String xml, int flags);

    StoragePoolPointer virStoragePoolDefineXML(ConnectionPointer virConnectPtr, String xml, int flags);

    StoragePoolPointer virStoragePoolLookupByName(ConnectionPointer virConnectPtr, String name);

    StoragePoolPointer virStoragePoolLookupByUUID(ConnectionPointer virConnectPtr, byte[] uuidBytes);

    StoragePoolPointer virStoragePoolLookupByUUIDString(ConnectionPointer virConnectPtr, String uuidstr);

    StoragePoolPointer virStoragePoolLookupByVolume(StorageVolPointer storageVolPtr);

    StorageVolPointer virStorageVolCreateXML(StoragePoolPointer storagePoolPtr, String xml, int flags);

    StorageVolPointer virStorageVolCreateXMLFrom(StoragePoolPointer storagePoolPtr, String xml, StorageVolPointer cloneVolume, int flags);

    StorageVolPointer virStorageVolLookupByKey(ConnectionPointer virConnectPtr, String name);

    StorageVolPointer virStorageVolLookupByName(StoragePoolPointer storagePoolPtr, String name);

    StorageVolPointer virStorageVolLookupByPath(ConnectionPointer virConnectPtr, String path);

    StreamPointer virStreamNew(ConnectionPointer virConnectPtr, int flags);

    String virConnectGetType(ConnectionPointer virConnectPtr);

    String virDomainGetName(DomainPointer virDomainPtr);

    String virInterfaceGetMACString(InterfacePointer virInterfacePtr);

    String virInterfaceGetName(InterfacePointer virInterfacePtr);

    String virNetworkGetName(NetworkPointer virNetworkPtr);

    String virNodeDeviceGetName(DevicePointer virDevicePointer);

    String virNodeDeviceGetParent(DevicePointer virDevicePointer);

    String virNWFilterGetName(NetworkFilterPointer virNWFilterPtr);

    String virSecretGetUsageID(SecretPointer virSecretPtr);

    String virStoragePoolGetName(StoragePoolPointer storagePoolPtr);

    String virStorageVolGetKey(StorageVolPointer storageVolPtr);

    String virStorageVolGetName(StorageVolPointer storageVolPtr);

    virError virGetLastError();

    void virConnResetLastError(ConnectionPointer virConnectPtr);

    void virConnSetErrorFunc(ConnectionPointer virConnectPtr, Pointer userData, VirErrorCallback callback);

    void virEventUpdateTimeout(int timer, int timeout);

    void virResetLastError();

    void virSetErrorFunc(Pointer userData, VirErrorCallback callback);
}
