package org.libvirt.event;

public enum StoppedDetail implements DomainEventDetail {
    /**
     * Normal shutdown.
     */
    SHUTDOWN,

    /**
     * Forced poweroff from host.
     */
    DESTROYED,

    /**
     * Guest crashed.
     */
    CRASHED,

    /**
     * Migrated off to another host.
     */
    MIGRATED,

    /**
     * Saved to a state file.
     */
    SAVED,

    /**
     * Host emulator/mgmt failed.
     */
    FAILED,

    /**
     * Offline snapshot was loaded.
     */
    FROM_SNAPSHOT,

    UNKNOWN
}
