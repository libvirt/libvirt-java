package org.libvirt;

/**
 * Power management suspension target levels
 */
public enum SuspendTarget {
    /** Suspend-to-RAM */
    MEMORY,

    /** Suspend-to-Disk */
    DISK,

    /** Hybrid-Suspend */
    HYBRID;
}
