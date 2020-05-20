package org.libvirt;

import org.libvirt.jna.CString;
import org.libvirt.jna.Libvirt;
import org.libvirt.jna.StoragePoolPointer;
import org.libvirt.jna.StorageVolPointer;
import org.libvirt.jna.virStoragePoolInfo;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

/**
 * A collection of storage
 */
public class StoragePool {

    static final class BuildFlags {
        /**
         * Regular build from scratch
         */
        static final int VIR_STORAGE_POOL_BUILD_NEW = 0;
        /**
         * Repair / reinitialize
         */
        static final int VIR_STORAGE_POOL_BUILD_REPAIR = 1;
        /**
         * Extend existing pool
         */
        static final int VIR_STORAGE_POOL_BUILD_RESIZE = 2;
    }

    static final class DeleteFlags {
        /**
         * Delete metadata only (fast)
         */
        static final int VIR_STORAGE_POOL_DELETE_NORMAL = 0;
        /**
         * Clear all data to zeros (slow)
         */
        static final int VIR_STORAGE_POOL_DELETE_ZEROED = 1;
    }

    /**
     * the native virStoragePoolPtr.
     */
    protected StoragePoolPointer vspp;

    /**
     * The VirConnect Object that represents the Hypervisor of this Domain
     */
    protected Connect virConnect;

    /**
     * Constructs a VirStoragePool object from a known native virStoragePoolPtr,
     * and a VirConnect object. For use when native libvirt returns a
     * virStoragePoolPtr, i.e. error handling.
     *
     * @param virConnect
     *            the Domain's hypervisor
     * @param vspp
     *            the native virStoragePoolPtr
     */
    StoragePool(final Connect virConnect, final StoragePoolPointer vspp) {
        this.virConnect = virConnect;
        this.vspp = vspp;
    }

    /**
     * Build the underlying storage pool
     *
     * @param flags
     *            future flags, use 0 for now
     */
    public void build(final int flags) throws LibvirtException {
        processError(libvirt.virStoragePoolBuild(vspp, flags));
    }

    /**
     * Starts this inactive storage pool
     *
     * @param flags
     *            future flags, use 0 for now
     */
    public void create(final int flags) throws LibvirtException {
        processError(libvirt.virStoragePoolCreate(vspp, flags));
    }

    /**
     * Delete the underlying pool resources. This is a non-recoverable
     * operation. The virStoragePool object itself is not free'd.
     *
     * @param flags
     *            flags for obliteration process
     */
    public void delete(final int flags) throws LibvirtException {
        processError(libvirt.virStoragePoolDelete(vspp, flags));
    }

    /**
     * Destroy an active storage pool. This will deactivate the pool on the
     * host, but keep any persistent config associated with it. If it has a
     * persistent config it can later be restarted with virStoragePoolCreate().
     * This does not free the associated virStoragePoolPtr object.
     */
    public void destroy() throws LibvirtException {
        processError(libvirt.virStoragePoolDestroy(vspp));
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Free a storage pool object, releasing all memory associated with it. Does
     * not change the state of the pool on the host.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vspp != null) {
            success = processError(libvirt.virStoragePoolFree(vspp));
            vspp = null;
        }
        return success;
    }

    /**
     * Fetches the value of the autostart flag, which determines whether the
     * pool is automatically started at boot time
     *
     * @return the result
     * @throws LibvirtException
     */
    public boolean getAutostart() throws LibvirtException {
        IntByReference autoStart = new IntByReference();
        processError(libvirt.virStoragePoolGetAutostart(vspp, autoStart));
        return autoStart.getValue() != 0 ? true : false;
    }

    /**
     * Provides the connection pointer associated with a storage pool.
     *
     * @return the Connect object
     */
    public Connect getConnect() {
        return virConnect;
    }

    /**
     * Get volatile information about the storage pool such as free space /
     * usage summary
     *
     * @return a StoragePoolInfo object describing this storage pool
     * @throws LibvirtException
     */
    public StoragePoolInfo getInfo() throws LibvirtException {
        virStoragePoolInfo vInfo = new virStoragePoolInfo();
        processError(libvirt.virStoragePoolGetInfo(vspp, vInfo));
        return new StoragePoolInfo(vInfo);
    }

    /**
     * Fetch the locally unique name of the storage pool
     *
     * @return the name
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virStoragePoolGetName(vspp));
    }

    /**
     * Fetch the globally unique ID of this storage pool
     *
     * @return the UUID as an unpacked int array
     * @throws LibvirtException
     */
    public int[] getUUID() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_BUFLEN];
        processError(libvirt.virStoragePoolGetUUID(vspp, bytes));
        return Connect.convertUUIDBytes(bytes);
    }

    /**
     * Fetch the globally unique ID of the storage pool as a string
     *
     * @return the UUID in canonical String format
     * @throws LibvirtException
     */
    public String getUUIDString() throws LibvirtException {
        byte[] bytes = new byte[Libvirt.VIR_UUID_STRING_BUFLEN];
        processError(libvirt.virStoragePoolGetUUIDString(vspp, bytes));
        return Native.toString(bytes);
    }

    /**
     * Fetch an XML document describing all aspects of the storage pool. This is
     * suitable for later feeding back into the virStoragePoolCreateXML method.
     *
     * @param flags
     *            flags for XML format options (set of virDomainXMLFlags)
     * @return a XML document -java @throws LibvirtException
     */
    public String getXMLDesc(final int flags) throws LibvirtException {
        return processError(libvirt.virStoragePoolGetXMLDesc(vspp, flags)).toString();
    }

    /**
     * Determine if the storage pool is currently running
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStoragePoolIsActive">Libvirt
     *      Documentation</a>
     * @return 1 if running, 0 if inactive
     * @throws LibvirtException
     */
    public int isActive() throws LibvirtException {
        return processError(libvirt.virStoragePoolIsActive(vspp));
    }

    /**
     * Determine if the storage pool has a persistent configuration which means
     * it will still exist after shutting down
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virStoragePoolIsPersistent">
        Libvirt Documentation</a>
     * @return 1 if persistent, 0 if transient
     * @throws LibvirtException
     */
    public int isPersistent() throws LibvirtException {
        return processError(libvirt.virStoragePoolIsPersistent(vspp));
    }

    /**
     * Fetch list of storage volume names
     *
     * @return an Array of Strings that contains the names of the storage
     *         volumes
     * @throws LibvirtException
     */
    public String[] listVolumes() throws LibvirtException {
        int num = numOfVolumes();
        if (num > 0) {
            CString[] names = new CString[num];

            int got = processError(libvirt.virStoragePoolListVolumes(vspp, names, num));

            return Library.toStringArray(names, got);
        } else {
            return Library.NO_STRINGS;
        }
    }

    /**
     * Fetch the number of storage volumes within a pool
     *
     * @return the number of storage pools
     * @throws LibvirtException
     */
    public int numOfVolumes() throws LibvirtException {
        return processError(libvirt.virStoragePoolNumOfVolumes(vspp));
    }

    /**
     * Request that the pool refresh its list of volumes. This may involve
     * communicating with a remote server, and/or initializing new devices at
     * the OS layer
     *
     * @param flags
     *            flags to control refresh behaviour (currently unused, use 0)
     * @throws LibvirtException
     */
    public void refresh(final int flags) throws LibvirtException {
        processError(libvirt.virStoragePoolRefresh(vspp, flags));
    }

    /**
     * Sets the autostart flag
     *
     * @param autostart
     *            new flag setting
     * @throws LibvirtException
     */
    public void setAutostart(final int autostart) throws LibvirtException {
        libvirt.virStoragePoolSetAutostart(vspp, autostart);
    }

    /**
     * Create a storage volume within a pool based on an XML description. Not
     * all pools support creation of volumes
     *
     * @param xmlDesc
     *            description of volume to create
     * @param flags
     *            flags for creation (unused, pass 0)
     * @return the storage volume
     * @throws LibvirtException
     */
    public StorageVol storageVolCreateXML(final String xmlDesc, final int flags)
            throws LibvirtException {
        StorageVolPointer sPtr =
                processError(libvirt.virStorageVolCreateXML(vspp, xmlDesc, flags));
        return new StorageVol(virConnect, sPtr);
    }

    /**
     * Create a storage volume in the parent pool, using the 'clonevol' volume
     * as input. Information for the new volume (name, perms) are passed via a
     * typical volume XML description.
     *
     * @return The storage volume
     * @throws LibvirtException
     */
    public StorageVol storageVolCreateXMLFrom(final String xmlDesc,
                                              final StorageVol cloneVolume,
                                              final int flags)
            throws LibvirtException {
        StorageVolPointer sPtr =
                processError(libvirt.virStorageVolCreateXMLFrom(vspp, xmlDesc,
                                                                cloneVolume.vsvp,
                                                                flags));
        return new StorageVol(virConnect, sPtr);
    }

    /**
     * Fetch an object representing to a storage volume based on its name within
     * a pool
     *
     * @param name
     *            name of storage volume
     * @return a StorageVol object, or {@code null} if not found.
     * @throws LibvirtException
     */
    public StorageVol storageVolLookupByName(final String name)
            throws LibvirtException {
        StorageVolPointer sPtr = processError(libvirt.virStorageVolLookupByName(vspp, name));

        return (sPtr == null) ? null : new StorageVol(virConnect, sPtr);
    }

    /**
     * Undefine an inactive storage pool
     *
     * @throws LibvirtException
     */
    public void undefine() throws LibvirtException {
        processError(libvirt.virStoragePoolUndefine(vspp));
    }

}
