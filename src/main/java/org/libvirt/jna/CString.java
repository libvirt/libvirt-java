package org.libvirt.jna;

import java.nio.charset.Charset;

import com.sun.jna.FromNativeContext;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * Represents an allocated C-String.
 * <p>
 * Either call {@link #toString} or {@link #free()}. Both methods make
 * sure to reclaim the memory allocated for the string by calling
 * Native.free.
 */
public class CString extends PointerType {
    // all strings in libvirt are UTF-8 encoded
    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final byte NUL = 0;
    private String string = null;

    public CString() {
        super();
    }

    public CString(final Pointer p) {
        super(p);
    }

    /**
     * Returns a String representing the value of this C-String
     * <p>
     * Side-effect: frees the memory of the C-String.
     * @return String represented by C-String or "(null)"
     */
    @Override
    public String toString() {
        if (string == null) {
            final Pointer ptr = getPointer();

            if (ptr == null) {
                return "(null)";
            }

            try {
                // N.B.  could be replaced with Pointer.getString(0L, "UTF-8")
                //       available in JNA >= 4.x
                final long len = ptr.indexOf(0, NUL);
                assert len != -1 : "C-Strings must be \\0 terminated.";
                assert len <= Integer.MAX_VALUE : "string length exceeded " + Integer.MAX_VALUE;

                if (len == 0) {
                    string = "";
                } else {
                    final byte[] data = ptr.getByteArray(0, (int) len);

                    string = new String(data, UTF8);
                }
            } finally {
                free(ptr);
            }
        }
        return string;
    }

    @Override
    public CString fromNative(final Object nativeValue,
                              final FromNativeContext context) {
        if (nativeValue == null) {
            return null;
        }

        return new CString((Pointer) nativeValue);
    }

    private void free(final Pointer ptr) {
        assert ptr != null;

        Native.free(Pointer.nativeValue(ptr));
        setPointer(null);
    }

    /**
     * Free the memory used by this C-String
     */
    public void free() {
        final Pointer ptr = getPointer();
        if (ptr != null) {
            free(ptr);
        }
    }
}
