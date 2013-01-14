package org.libvirt.event;

public enum ShutdownDetail implements DomainEventDetail {
    /**
     * Guest finished shutdown sequence.
     */
    FINISHED,

    UNKNOWN
}
