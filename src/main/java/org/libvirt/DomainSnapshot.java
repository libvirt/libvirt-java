package org.libvirt;

import org.libvirt.jna.DomainSnapshotPointer;
import org.libvirt.jna.Libvirt;

public class DomainSnapshot {

    /**
     * the native virDomainSnapshotPtr.
     */
    DomainSnapshotPointer VDSP;

    /**
     * The Connect Object that represents the Hypervisor of this Domain Snapshot
     */
    private Connect virConnect;

    /**
     * The libvirt connection from the hypervisor
     */
    protected Libvirt libvirt;

    public DomainSnapshot(Connect virConnect, DomainSnapshotPointer VDSP) {
        this.VDSP = VDSP;
        this.virConnect = virConnect;
        libvirt = virConnect.libvirt;
    }

    /**
     * Delete the Snapshot
     *
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotDelete">Libvirt
     *      Documentation</a>
     * @param flags
     *            controls teh deletion
     * @return 0 if the selected snapshot(s) were successfully deleted, -1 on error.
     * @throws LibvirtException
     */
    public int delete(int flags) throws LibvirtException {
        int success = 0;
        if (VDSP != null) {
            success = libvirt.virDomainSnapshotDelete(VDSP, flags);
            processError();
            VDSP = null;
        }

        return success;
    }

    @Override
    public void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the domain snapshot handle. The underlying snapshot continues to
     * exist.
     *
     * @throws LibvirtException
     * @return 0 on success, or -1 on error.
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VDSP != null) {
            success = libvirt.virDomainSnapshotFree(VDSP);
            processError();
            VDSP = null;
        }

        return success;
    }

    /**
     * Fetches an XML document describing attributes of the snapshot.
     *
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotGetXMLDesc">Libvirt Documentation</a>
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        String returnValue = libvirt.virDomainSnapshotGetXMLDesc(VDSP, 0);
        processError();
        return returnValue;
    }

    /**
     * Error handling logic to throw errors. Must be called after every libvirt
     * call.
     */
    protected void processError() throws LibvirtException {
        virConnect.processError();
    }
}
