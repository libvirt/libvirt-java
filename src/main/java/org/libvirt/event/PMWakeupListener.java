package org.libvirt.event;

import org.libvirt.Domain;

/**
 * Interface for receiving PMWakeup events on a domain.
 */
public interface PMWakeupListener extends EventListener {

    /**
     * This method gets called when a domain is woken up.
     *
     * @param domain  the domain that was woken up
     * @param reason  the reason why that event happened
     */
    void onPMWakeup(Domain domain, PMWakeupReason reason);
}
