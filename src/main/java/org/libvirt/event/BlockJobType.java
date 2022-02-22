package org.libvirt.event;

public enum BlockJobType {
    UNKNOWN,
    PULL,
    COPY,
    COMMIT,
    ACTIVE_COMMIT,
    BACKUP,
    LAST
}
