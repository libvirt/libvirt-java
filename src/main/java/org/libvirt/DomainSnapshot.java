package org.libvirt;

import org.libvirt.jna.DomainSnapshotPointer;
import static org.libvirt.Library.libvirt;
import static org.libvirt.ErrorHandler.processError;

public class DomainSnapshot {

    private static int bit(final int i) {
        return 1 << i;
    }

    public static final class SnapshotDeleteFlags {
        public static final int CHILDREN      = bit(0); /* Also delete children */
        public static final int METADATA_ONLY = bit(1); /* Delete just metadata */
        public static final int CHILDREN_ONLY = bit(2); /* Delete just children */
    }

    public static final class XMLFlags {
        public static final int SECURE = bit(0); /* Dump security sensitive information too */
    }

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
     *      href="https://libvirt.org/html/libvirt-libvirt.html#virDomainSnapshotDelete">Libvirt
     *      Documentation</a>
     * @param flags see {@link SnapshotDeleteFlags}
     *            controls the deletion
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int delete(final int flags) throws LibvirtException {
        int success = 0;
        if (vdsp != null) {
            success = processError(libvirt.virDomainSnapshotDelete(vdsp, flags));
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
     * Fetches an XML document describing attributes of the snapshot, without security-sensitive data.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-snapshot.html#virDomainSnapshotGetXMLDesc">
     *      Libvirt Documentation</a>
     * @return the XML document
     * @throws org.libvirt.LibvirtException
     */
    public String getXMLDesc() throws LibvirtException {
        return getXMLDesc(0);
    }

    /**
     * Fetches an XML document describing attributes of the snapshot.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-snapshot.html#virDomainSnapshotGetXMLDesc">
     *      Libvirt Documentation</a>
     * @param flags bitwise-OR of {@link XMLFlags}
     * @return the XML document
     * @throws org.libvirt.LibvirtException
     */
    public String getXMLDesc(final int flags) throws LibvirtException {
        return processError(libvirt.virDomainSnapshotGetXMLDesc(vdsp, flags)).toString();
    }
}
