package org.libvirt.event;

public enum DefinedDetail implements DomainEventDetail {
    /**
     * Newly created config file.
     */
    ADDED,

    /**
     * Changed config file.
     */
    UPDATED,

    UNKNOWN
}
