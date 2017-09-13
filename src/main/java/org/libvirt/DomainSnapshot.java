package org.libvirt;

import static org.libvirt.ErrorHandler.processError;
import static org.libvirt.Library.libvirt;

import org.libvirt.jna.pointers.DomainSnapshotPointer;

public class DomainSnapshot {

    /**
     * the native virDomainSnapshotPtr.
     */
    DomainSnapshotPointer VDSP;

    /**
     * The Connect Object that represents the Hypervisor of this Domain Snapshot
     */
    private Connect virConnect;

    public DomainSnapshot(Connect virConnect, DomainSnapshotPointer VDSP) {
        this.VDSP = VDSP;
        this.virConnect = virConnect;
    }

    /**
     * Delete the Snapshot
     *
     * @param flags controls the deletion
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int delete(int flags) throws LibvirtException {
        int success = 0;
        if (VDSP != null) {
            success = processError(libvirt.virDomainSnapshotDelete(VDSP, flags));
            VDSP = null;
        }

        return success;
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Release the domain snapshot handle. The underlying snapshot continues to
     * exist.
     *
     * @return 0 on success
     * @throws LibvirtException
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (VDSP != null) {
            success = processError(libvirt.virDomainSnapshotFree(VDSP));
            VDSP = null;
        }

        return success;
    }

    /**
     * Fetches an XML document describing attributes of the snapshot.
     *
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        return processError(libvirt.virDomainSnapshotGetXMLDesc(VDSP, 0)).toString();
    }
}
