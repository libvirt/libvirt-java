package org.libvirt;

import org.libvirt.jna.DomainCheckpointPointer;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

import static org.libvirt.Library.libvirt;

import org.libvirt.Domain.CheckpointListFlags;

import com.sun.jna.Native;


import static org.libvirt.ErrorHandler.processError;

public class DomainCheckpoint {

    private static int bit(final int i) {
        return 1 << i;
    }

    public static final class CheckpointDeleteFlags {
        /**
         * Also delete children (Since: 5.6.0)
         * 
         * @see <a href="change">
         *     Libvirt Documentation</a>
         */
        public static final int CHILDREN       = bit(0);

        /**
         * Delete just metadata (Since: 5.6.0)
         * 
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#VIR_DOMAIN_CHECKPOINT_DELETE_METADATA_ONLY">
         *     Libvirt Documentation</a>
         */
        public static final int METADATA_ONLY  = bit(1);

        /**
         * Delete just children (Since: 5.6.0)
         * 
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#VIR_DOMAIN_CHECKPOINT_DELETE_CHILDREN_ONLY">
         *     Libvirt Documentation</a>
         */
        public static final int CHILDREN_ONLY  = bit(2);
    }

    public static final class XMLFlags {
        /** Include sensitive data (Since: 5.6.0)
         * 
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#VIR_DOMAIN_CHECKPOINT_XML_SECURE">
         *     Libvirt Documentation</a>
        */
        public static final int SECURE      = bit(0);

        /** Supress <domain> subelement (Since: 5.6.0)
         * 
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#VIR_DOMAIN_CHECKPOINT_XML_NO_DOMAIN">
         *     Libvirt Documentation</a>
        */
        public static final int NO_DOMAIN   = bit(1);

        /** Include dynamic per-<disk> size (Since: 5.6.0)
         * 
         * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#VIR_DOMAIN_CHECKPOINT_XML_SIZE">
         *     Libvirt Documentation</a>
        */
        public static final int XML_SIZE    = bit(2); 
    }

    /**
     * the native virDomainCheckpointPtr.
     */
    DomainCheckpointPointer vdcp;

    /**
     * The Connect Object that represents the Hypervisor of this Domain Checkpoint
     */
    private final Connect virConnect;


    /**
     * Constructs a DomainCheckpoint object from a known native DomainCheckpointPointer, and a
     * Connect object.
     *
     * @param virConnect
     *            the Domain's hypervisor
     * @param vdcp
     *            the native virDomainCheckpointPtr
     */
    public DomainCheckpoint(final Connect virConnect,
                            final DomainCheckpointPointer vdcp) {
        this.vdcp = vdcp;
        this.virConnect = virConnect;
    }

    /**
     * Delete the domain checkpoint
     *
     * @see <a
     *      href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#virDomainCheckpointDelete">Libvirt
     *      Documentation</a>
     * @param flags see {@link CheckpointDeleteFlags}
     *              controls the deletion
     * @return <em>ignore</em> (always 0)
     * @throws LibvirtException
     */
    public int delete(final int flags) throws LibvirtException {
        int success = 0;
        if (vdcp != null) {
            success = processError(libvirt.virDomainCheckpointDelete(vdcp, flags));
        }

        return success;
    }

    @Override
    protected void finalize() throws LibvirtException {
        free();
    }

    /**
     * Frees this domaincheckpoint object. The running instance is kept alive. The data
     * structure is freed and should not be used thereafter.
     *
     * @throws LibvirtException
     * @return number of references left (>= 0)
     */
    public int free() throws LibvirtException {
        int success = 0;
        if (vdcp != null) {
            success = processError(libvirt.virDomainCheckpointFree(vdcp));
            vdcp = null;
        }

        return success;
    }

    /**
     * Fetches an XML document describing attributes of the domain checkpoint, without
     * security-sensitive data.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#virDomainCheckpointGetXMLDesc">
     *      Libvirt Documentation</a>
     * @return the XML document
     * @throws org.libvirt.LibvirtException
     */
    public String getXMLDesc() throws LibvirtException {
        return getXMLDesc(0);
    }

    /**
     * Fetches an XML document describing attributes of the domain checkpoint.
     *
     * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#virDomainCheckpointGetXMLDesc">
     *      Libvirt Documentation</a>
     * @param flags see {@link XMLFlags}
     *              controls the information
     * @return the XML document
     * @throws org.libvirt.LibvirtException
     */
    public String getXMLDesc(final int flags) throws LibvirtException {
        return processError(libvirt.virDomainCheckpointGetXMLDesc(vdcp, flags)).toString();
    }

    /**
     * Get the public name for that checkpoint
     *
     * @return the name, null if there is no name
     * @throws LibvirtException
     */
    public String getName() throws LibvirtException {
        return processError(libvirt.virDomainCheckpointGetName(vdcp));
    }

    /**
     * Array of domain checkpoints children for the given domain checkpoint.
     * 
     * @see <a 
     *      href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#virDomainCheckpointListAllChildren">Libvirt
     *      Documentation</a>
     * @param flags
     *            flags for list the checkpoints, see the {@link CheckpointListFlags} for the flag options
     * @return Array with children checkpoints of the given domain checkpoint
     * @throws LibvirtException
     */
    public DomainCheckpoint[] listAllChildren(int flags) throws LibvirtException {
        PointerByReference checkpoints = new PointerByReference();
        int count = libvirt.virDomainCheckpointListAllChildren(vdcp, checkpoints, flags);
        if (checkpoints.getValue() == null) {
            if (count != 0) {
                processError(count);
                throw new IllegalStateException("virDomainListAllCheckpoints returned " + count);
            }
            return new DomainCheckpoint[0];
        }
        DomainCheckpoint[] result = new DomainCheckpoint[count];
        try {
            if (count < 0) {
                processError(count);
                throw new IllegalStateException("virDomainListAllCheckpoints returned " + count);
            } 
            
            Pointer arrayPtr = checkpoints.getValue();
            for (int i = 0; i < count; i++) {
                Pointer p = arrayPtr.getPointer((long) i * Native.POINTER_SIZE);
                result[i] = new DomainCheckpoint(virConnect, new DomainCheckpointPointer(p));
            }

            return result;
        } finally {
            Library.free(checkpoints.getValue());
        }
    }

    /**
     * Get the parent checkpoint for checkpoint, if any.
     * 
     * @see <a
     *      href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#virDomainCheckpointGetParent">Libvirt
     *      Documentation</a>
     * @param flags
     *        extra flags
     * @return a domain checkpoint or null if the given domain checkpoint is root
     * @throws LibvirtException
     */
    public DomainCheckpoint getParent(int flags) throws LibvirtException {
        DomainCheckpointPointer parent = libvirt.virDomainCheckpointGetParent(vdcp, flags);
        if(parent == null)
            return null;
        return new DomainCheckpoint(virConnect, parent);
    }

    /**
     * Get the parent checkpoint for checkpoint, if any.
     * 
     * This is just a convenience method, it has the same effect
     * as calling {@code getParent(0);}.
     *
     * @see #getParent(int)
     * @see <a
     *      href="https://libvirt.org/html/libvirt-libvirt-domain-checkpoint.html#virDomainCheckpointGetParent">Libvirt
     *      Documentation</a>
     * @return a domain checkpoint or null if the given domain checkpoint is root
     * @throws LibvirtException
     */
    public DomainCheckpoint getParent() throws LibvirtException {
        return getParent(0);
    }

}
