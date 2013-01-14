package org.libvirt.event;

public enum IOErrorAction {
    /**
     * No action, I/O error ignored.
     */
    NONE,

    /**
     * Guest CPUs are paused.
     */
    PAUSE,

    /**
     * I/O error was reported to the guest OS.
     */
    REPORT,

    /**
     * An unknown action was taken.
     */
    UNKNOWN;

    private static final IOErrorAction vals[] = IOErrorAction.values();

    static {
        // make sure that the enum constants have the correct
        // ordinal number assigned in correspondence to the
        // values of the virDomainEventIOErrorAction enum
        // members

        assert NONE.ordinal() == 0;
        assert PAUSE.ordinal() == 1;
        assert REPORT.ordinal() == 2;

        // must be the last constant
        assert UNKNOWN.ordinal() == vals.length - 1;
    }
}
