package org.libvirt.event;

public enum ResumedDetail implements DomainEventDetail {
    /**
     * Normal resume due to admin unpause.
     */
    UNPAUSED,

    /**
     * Resumed for completion of migration.
     */
    MIGRATED,

    /**
     * Resumed from snapshot.
     */
    FROM_SNAPSHOT,

    UNKNOWN
}
