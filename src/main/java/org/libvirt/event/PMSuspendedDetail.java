package org.libvirt.event;

public enum PMSuspendedDetail implements DomainEventDetail {
    /**
     * Guest was PM suspended to memory.
     */
    MEMORY,

    /**
     * Guest was PM suspended to disk.
     */
    DISK,

    UNKNOWN
}
