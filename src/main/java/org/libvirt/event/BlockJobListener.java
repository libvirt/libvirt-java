package org.libvirt.event;

import org.libvirt.Domain;

public interface BlockJobListener extends EventListener {
    /**
     * This method gets called upon a Block Job error event.
     *
     * @param domain the domain which got an event
     * @param diskPath the src of the block device that had an event
     * @param type the type of the block job
     * @param status the status of the block job
     */
    void onEvent(Domain domain,
                 String diskPath,
                 BlockJobType type,
                 BlockJobStatus status);
}
