package org.libvirt.event;

public enum StartedDetail implements DomainEventDetail {
    /**
     * Normal startup from boot.
     */
    BOOTED,

    /**
     * Incoming migration from another host.
     */
    MIGRATED,

    /**
     * Restored from a state file.
     */
    RESTORED,

    /**
     * Restored from snapshot
     */
    FROM_SNAPSHOT,

    /**
     * Started due to wakeup event.
     */
    WAKEUP,

    UNKNOWN
}
