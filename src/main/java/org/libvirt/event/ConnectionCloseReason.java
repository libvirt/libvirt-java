package org.libvirt.event;

public enum ConnectionCloseReason {
    /** Misc I/O error */
    ERROR,

    /** End-of-file from server */
    EOF,

    /** Keepalive timer triggered */
    KEEPALIVE,

    /** Client requested it */
    CLIENT,

    UNKNOWN
}
