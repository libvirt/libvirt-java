package org.libvirt.jna;

import com.sun.jna.Native;
import com.sun.jna.IntegerType;

/**
 * Represents the native {@code size_t} data type.
 */
public final class SizeT extends IntegerType {
    public SizeT() {
        this(0);
    }

    public SizeT(final long value) {
        /* The third argument determines whether this class represents
         * an unsigned integer type. When extracting a value into a
         * larger-sized container (e.g. 4 byte native type into Java
         * long), the value is properly converted as unsigned.
         */
        super(Native.SIZE_T_SIZE, value, true);
    }
}
