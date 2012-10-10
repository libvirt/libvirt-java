package org.libvirt;

import org.libvirt.jna.Libvirt;

import com.sun.jna.Native;
import com.sun.jna.Pointer;

/**
 * This class represents an instance of the JNA mapped libvirt
 * library.
 *
 * The library will get loaded when first accessing this class.
 *
 * Additionally, this class contains internal methods to ease
 * implementing the public API.
 */
final class Library {
    final static Libvirt libvirt;

    // an empty string array constant
    // prefer this over creating empty arrays dynamically.
    final static String[] NO_STRINGS = {};

    // Load the native part
    static {
        Libvirt.INSTANCE.virInitialize();
        libvirt = Libvirt.INSTANCE;
        try {
            ErrorHandler.processError(Libvirt.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Library() {}

    /**
     * Free memory pointed to by ptr.
     */
    static void free(Pointer ptr) {
        Native.free(Pointer.nativeValue(ptr));
        Pointer.nativeValue(ptr, 0L);
    }

    /**
     * Convert the data pointed to by {@code ptr} to a String.
     */
    static String getString(Pointer ptr) {
        final long len = ptr.indexOf(0, (byte)0);
        assert (len != -1): "C-Strings must be \\0 terminated.";

        final byte[] data = ptr.getByteArray(0, (int)len);
        try {
            return new String(data, "utf-8");
        } catch (java.io.UnsupportedEncodingException e) {
            throw new RuntimeException("Libvirt problem: UTF-8 decoding error.", e);
        }
    }

    /**
     * Calls {@link #toStringArray(Pointer[], int)}.
     */
    static String[] toStringArray(Pointer[] ptrArr) {
        return toStringArray(ptrArr, ptrArr.length);
    }

    /**
     * Convert the given array of native pointers to "char" in
     * UTF-8 encoding to an array of Strings.
     *
     * \note The memory used by the elements of the original array
     *       is freed and ptrArr is modified.
     */
    static String[] toStringArray(Pointer[] ptrArr, final int size) {
        try {
            final String[] result = new String[size];
            for (int i = 0; i < size; ++i) {
                result[i] = Library.getString(ptrArr[i]);
            }
            return result;
        } finally {
            for (int i = 0; i < size; ++i) {
                Library.free(ptrArr[i]);
                ptrArr[i] = null;
            }
        }
    }
}
