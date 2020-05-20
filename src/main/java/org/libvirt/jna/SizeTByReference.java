package org.libvirt.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

/**
 * Represents a native (call-by-reference) pointer to {@code size_t} data type.
 */
public final class SizeTByReference extends ByReference {
    public SizeTByReference() {
        this(0);
    }

    public SizeTByReference(final long value) {
        super(Native.SIZE_T_SIZE);
        setValue(value);
    }

    public void setValue(final long value) {
        Pointer p = getPointer();
        switch (Native.SIZE_T_SIZE) {
        case 2:
            p.setShort(0, (short) value);
            break;
        case 4:
            p.setInt(0, (int) value);
            break;
        case 8:
            p.setLong(0, value);
            break;
        default:
            throw new RuntimeException("Unsupported size: " + Native.SIZE_T_SIZE);
        }
    }

    public long getValue() {
        Pointer p = getPointer();
        switch (Native.SIZE_T_SIZE) {
        case 2:
            return p.getShort(0) & 0xFFFFL;
        case 4:
            return p.getInt(0) & 0xFFFFFFFFL;
        case 8:
            return p.getLong(0);
        default:
            throw new RuntimeException("Unsupported size: " + Native.SIZE_T_SIZE);
        }
    }
}
