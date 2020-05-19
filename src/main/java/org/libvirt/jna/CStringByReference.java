package org.libvirt.jna;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByReference;

public class CStringByReference extends ByReference{

    public CStringByReference() {
        super(Native.POINTER_SIZE);
        Pointer p = getPointer();
        p.setPointer(0, Pointer.NULL);
    }

    public CString getValue() {
        Pointer p = getPointer();
        return new CString(p.getPointer(0));
    }
}
