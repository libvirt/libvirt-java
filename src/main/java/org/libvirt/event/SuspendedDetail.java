package org.libvirt.event;

public enum SuspendedDetail implements DomainEventDetail {
    /**
     * Normal suspend due to admin pause.
     */
    PAUSED,

    /**
     * Suspended for offline migration.
     */
    MIGRATED,

    /**
     * Suspended due to a disk I/O error.
     */
    IOERROR,

    /**
     * Suspended due to a watchdog firing.
     */
    WATCHDOG,

    /**
     * Restored from paused state file.
     */
    RESTORED,

    /**
     * Restored from paused snapshot.
     */
    FROM_SNAPSHOT,

    /**
     * Suspended after failure during libvirt API call.
     */
    API_ERROR,

    UNKNOWN
}
