package org.libvirt.event.enums;

/**
 * Event IDs.
 */
public enum DomainEventID {
    /**
     * virConnectDomainEventCallback
     */
    VIR_DOMAIN_EVENT_ID_LIFECYCLE(0),
    /**
     * virConnectDomainEventGenericCallback
     */
    VIR_DOMAIN_EVENT_ID_REBOOT(1),
    /**
     * virConnectDomainEventRTCChangeCallback
     */
    VIR_DOMAIN_EVENT_ID_RTC_CHANGE(2),
    /**
     * virConnectDomainEventWatchdogCallback
     */
    VIR_DOMAIN_EVENT_ID_WATCHDOG(3),
    /**
     * virConnectDomainEventIOErrorCallback
     */
    VIR_DOMAIN_EVENT_ID_IO_ERROR(4),
    /**
     * virConnectDomainEventGraphicsCallback
     */
    VIR_DOMAIN_EVENT_ID_GRAPHICS(5),
    /**
     * virConnectDomainEventIOErrorReasonCallback
     */
    VIR_DOMAIN_EVENT_ID_IO_ERROR_REASON(6),
    /**
     * virConnectDomainEventGenericCallback
     */
    VIR_DOMAIN_EVENT_ID_CONTROL_ERROR(7),
    /**
     * virConnectDomainEventBlockJobCallback
     */
    VIR_DOMAIN_EVENT_ID_BLOCK_JOB(8),
    /**
     * virConnectDomainEventDiskChangeCallback
     */
    VIR_DOMAIN_EVENT_ID_DISK_CHANGE(9),
    /**
     * virConnectDomainEventTrayChangeCallback
     */
    VIR_DOMAIN_EVENT_ID_TRAY_CHANGE(10),
    /**
     * virConnectDomainEventPMWakeupCallback
     */
    VIR_DOMAIN_EVENT_ID_PMWAKEUP(11),
    /**
     * virConnectDomainEventPMSuspendCallback
     */
    VIR_DOMAIN_EVENT_ID_PMSUSPEND(12),
    /**
     * virConnectDomainEventBalloonChangeCallback
     */
    VIR_DOMAIN_EVENT_ID_BALLOON_CHANGE(13),
    /**
     * virConnectDomainEventPMSuspendDiskCallback
     */
    VIR_DOMAIN_EVENT_ID_PMSUSPEND_DISK(14),
    /**
     * virConnectDomainEventDeviceRemovedCallback
     */
    VIR_DOMAIN_EVENT_ID_DEVICE_REMOVED(15),
    /**
     * virConnectDomainEventBlockJobCallback
     */
    VIR_DOMAIN_EVENT_ID_BLOCK_JOB_2(16),
    /**
     * virConnectDomainEventTunableCallback
     */
    VIR_DOMAIN_EVENT_ID_TUNABLE(17),
    /**
     * virConnectDomainEventAgentLifecycleCallback
     */
    VIR_DOMAIN_EVENT_ID_AGENT_LIFECYCLE(18),
    /**
     * virConnectDomainEventDeviceAddedCallback
     */
    VIR_DOMAIN_EVENT_ID_DEVICE_ADDED(19),
    /**
     * virConnectDomainEventMigrationIterationCallback
     */
    VIR_DOMAIN_EVENT_ID_MIGRATION_ITERATION(20),
    /**
     * virConnectDomainEventJobCompletedCallback
     */
    VIR_DOMAIN_EVENT_ID_JOB_COMPLETED(21),
    /**
     * virConnectDomainEventDeviceRemovalFailedCallback
     */
    VIR_DOMAIN_EVENT_ID_DEVICE_REMOVAL_FAILED(22),
    /**
     * virConnectDomainEventMetadataChangeCallback
     */
    VIR_DOMAIN_EVENT_ID_METADATA_CHANGE(23),
    /**
     * virConnectDomainEventBlockThresholdCallback
     */
    VIR_DOMAIN_EVENT_ID_BLOCK_THRESHOLD(24);

    private int n;

    public static final int SIZE = values().length;

    DomainEventID(int n) {
        this.n = n;
    }

    public int getValue() {
        return n;
    }
}
