package org.libvirt.event;

/**
 * Details about a CRASHED domain event
 *
 * @see DomainEvent
 */
public enum CrashedDetail implements DomainEventDetail {
    /**
     * Guest was panicked.
     */
    PANICKED,

    UNKNOWN
}
