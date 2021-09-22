package org.libvirt.event;

import org.libvirt.Domain;

/**
 * Interface for receiving events occurring on a guest agent livecycle change.
 *
 * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virConnectDomainEventAgentLifecycleCallback">
virConnectDomainEventAgentLifecycleCallback</a>
 */
public interface AgentLifecycleListener extends EventListener {
    void onAgentLifecycleChange(Domain domain, int state, int reason);
}
