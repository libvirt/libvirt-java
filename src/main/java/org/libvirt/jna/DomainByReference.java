package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public class DomainByReference extends PointerByReference {

    public DomainByReference() {
        super(Pointer.NULL);
    }

}
