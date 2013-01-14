package org.libvirt.event;

/**
 * Enum constants representing the type of event occurred on
 * a domain
 */
public enum DomainEventType {
    /** A domain was defined */
    DEFINED(DefinedDetail.values()),

    /** A domain was undefined */
    UNDEFINED(UndefinedDetail.values()),

    /** A domain was started */
    STARTED(StartedDetail.values()),

    /** A domain was suspended */
    SUSPENDED(SuspendedDetail.values()),

    /** A domain was resumed */
    RESUMED(ResumedDetail.values()),

    /** A domain was stopped */
    STOPPED(StoppedDetail.values()),

    /** A domain was shut down */
    SHUTDOWN(ShutdownDetail.values()),

    /** A domain was PM suspended */
    PMSUSPENDED(PMSuspendedDetail.values()),

    /** A domain crashed */
    CRASHED(CrashedDetail.values()),

    /**
     * An unknown event occured
     *
     * This can happen if upstream libvirt adds more event types
     * that this library does not yet know about.
     */
    UNKNOWN(null);

    private final Object[] details;

    DomainEventType(Object[] d) {
        details = d;
    }

    @SuppressWarnings("unchecked")
    <T extends Enum<T>> T obtain(final int detail) {
        return (T)safeAt(detail);
    }

    // this method is only necessary for OpenJDK 6 which does not
    // compile calls to `obtain(d)` in some circumstances
    Object safeAt(final int detail) {
        final int index = Math.min(this.details.length - 1, detail);
        return this.details[index];
    }
}
