package org.libvirt;

import org.libvirt.jna.DomainSnapshotPointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

public class DomainSnapshot {

    /**
     * the native virDomainSnapshotPtr.
     */
    DomainSnapshotPointer vdsp;

    /**
     * The Connect Object that represents the Hypervisor of this Domain Snapshot
     */
    private final Connect virConnect;

    public DomainSnapshot(final Connect virConnect,
                          final DomainSnapshotPointer vdsp) {
        this.vdsp = vdsp;
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
    public int delete(final int flags) throws LibvirtException {
        int success = 0;
        if (vdsp != null) {
            success = processError(libvirt.virDomainSnapshotDelete(vdsp, flags));
            vdsp = null;
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
        if (vdsp != null) {
            success = processError(libvirt.virDomainSnapshotFree(vdsp));
            vdsp = null;
        }

        return success;
    }

    /**
     * Fetches an XML document describing attributes of the snapshot.
     *
     * @throws org.libvirt.LibvirtException
     * @see <a href="http://www.libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotGetXMLDesc">
        Libvirt Documentation</a>
     * @return the XML document
     */
    public String getXMLDesc() throws LibvirtException {
        return processError(libvirt.virDomainSnapshotGetXMLDesc(vdsp, 0)).toString();
    }
}
