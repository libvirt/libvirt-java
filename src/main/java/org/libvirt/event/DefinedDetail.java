package org.libvirt.event;

import org.libvirt.Domain;

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
