package org.libvirt.event;

/**
 * Contains information about a life cycle change of a domain.
 * <p>
 * This includes the domain event that occurred together with
 * further details about that event.
 * <p>
 * Usage:
 * <pre>{@code
 * int onLifecycleChange(Domain dom, DomainEvent info) {
 *     switch (info.getType()) {
 *         case STARTED:
 *             StartedDetail detail = info.getDetail();
 *
 *             switch (detail) {
 *                 case BOOTED:
 *                     ...
 *                     break;
 *                 ...
 *             }
 *         }
 *     }
 * }}</pre>
 * <p>
 * Note, that a ClassCastException will be thrown at runtime when
 * assigning to the wrong detail enumeration type:
 * <pre>{@code
 * DomainEvent info;
 *
 * if (info.getType() == DomainEventType.STARTED) {
 *     // info.getDetails() returns a StartedDetail enum
 *     StoppedDetail detail = info.getDetail(); // throws ClassCastException
 * }}</pre>
 *
 * @see LifecycleListener
 * @since 1.5.2
 */
public final class DomainEvent {
    private final DomainEventType type;
    private final int detail;

    public DomainEvent(final DomainEventType type, final int code) {
        this.type = type;
        this.detail = code;
    }

    /**
     * Returns the type of event which occurred.
     */
    public DomainEventType getType() {
        return this.type;
    }

    /**
     * Returns the corresponding domain event detail in regard to
     * the DomainEventType of this instance.
     *
     * @return a constant of one of the enums implementing the
     *         {@link DomainEventDetail} interface
     */
    public <T extends Enum<T> & DomainEventDetail> T getDetail() {
        @SuppressWarnings("unchecked") T detail = (T) this.type.safeAt(this.detail);

        return detail;
    }

    @Override
    public String toString() {
        Object d = this.type.safeAt(this.detail);
        return this.type + " (" + d + ")";
    }
}
