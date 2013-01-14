package org.libvirt.event;

import org.libvirt.Domain;

/**
 * Interface for receiving PMSuspend events on a domain.
 */
public interface PMSuspendListener extends EventListener {

    /**
     * This method gets called when a domain is suspended.
     *
     * @param domain  the domain that was suspended
     * @param reason  the reason why that event happened
     */
    void onPMSuspend(Domain domain, PMSuspendReason reason);
}
