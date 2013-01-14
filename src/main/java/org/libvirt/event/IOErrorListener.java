package org.libvirt.event;

import org.libvirt.Domain;

/**
 * Interface for receiving domain I/O error events.
 */
public interface IOErrorListener extends EventListener {
    /**
     * This method gets called upon a domain I/O error event.
     *
     * @param domain   the domain which got an I/O error
     * @param srcPath  the src of the block device with errors
     * @param devAlias the device alias of the block device with errors
     * @param action   the action that is to be taken due to the I/O error
     */
    void onIOError(Domain domain,
                   String srcPath,
                   String devAlias,
                   IOErrorAction action);
}
