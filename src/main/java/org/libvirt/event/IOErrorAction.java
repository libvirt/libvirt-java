package org.libvirt.event;

public enum IOErrorAction {
    /**
     * No action, I/O error ignored.
     */
    NONE(0),

    /**
     * Guest CPUs are paused.
     */
    PAUSE(1),

    /**
     * I/O error was reported to the guest OS.
     */
    REPORT(2),

    /**
     * An unknown action was taken.
     */
    UNKNOWN(3);

    private final int value;

    IOErrorAction(final int val) {
        this.value = val;
    }

    private static final IOErrorAction[] VALS = IOErrorAction.values();

    static {
        // must be the last constant
        assert UNKNOWN.value == VALS.length - 1;
    }
}
