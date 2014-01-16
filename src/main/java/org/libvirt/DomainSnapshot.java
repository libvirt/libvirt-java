package org.libvirt;

import org.libvirt.jna.DomainSnapshotPointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

import com.sun.jna.Pointer;

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
     * @see <a
     *      href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotDelete">Libvirt
     *      Documentation</a>
     * @param flags
     *            controls the deletion
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
     * @throws LibvirtException
     * @return 0 on success
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
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotGetXMLDesc">Libvirt Documentation</a>
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        Pointer p = processError(libvirt.virDomainSnapshotGetXMLDesc(VDSP, 0));

        try {
            return Library.getString(p);
        } finally {
            Library.free(p);
        }
    }
}
