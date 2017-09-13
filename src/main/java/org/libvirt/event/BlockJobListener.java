package org.libvirt.event;

import org.libvirt.Domain;
import org.libvirt.LibvirtException;

/**
 * Interface for receiving domain blok job events.
 */
public interface BlockJobListener extends EventListener {
    /**
     * This method gets called upon a domain block job completed event.
     *
     * @param domain the domain which has a block job event
     */
    void onBlockJobCompleted(Domain domain, String disk, int type) throws LibvirtException;

    /**
     * This method gets called upon a domain block job failed event.
     *
     * @param domain the domain which has a block job event
     */

    void onBlockJobFailed(Domain domain, String disk, int type) throws LibvirtException;

    /**
     * This method gets called upon a domain block job canceled event.
     *
     * @param domain the domain which has a block job event
     */

    void onBlockJobCanceled(Domain domain, String disk, int type) throws LibvirtException;

    /**
     * This method gets called upon a domain block job ready event.
     *
     * @param domain the domain which has a block job event
     */

    void onBlockJobReady(Domain domain, String disk, int type) throws LibvirtException;
}
