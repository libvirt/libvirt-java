package org.libvirt.event;

import org.libvirt.Domain;

/**
 * Interface for receiving domain reboot events.
 */
public interface RebootListener extends EventListener {
    /**
     * This method gets called upon a domain reboot event.
     *
     * @param domain   the domain which was rebooted
     */
    void onReboot(Domain domain);
}
