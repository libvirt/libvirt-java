package org.libvirt;

public enum MemoryAddressMode {
    /** addresses are virtual addresses */
    VIRTUAL(1),

    /** addresses are physical addresses */
    PHYSICAL(2);

    private final int value;

    MemoryAddressMode(int v) {
        this.value = v;
    }

    int getValue() {
        return value;
    }
}
