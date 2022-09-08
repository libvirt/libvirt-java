package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * Pointer class to provide type safety to the jna interface.
 */
public class DomainPointer extends PointerType {

    public DomainPointer() {
    }

    public DomainPointer(Pointer p) {
        super(p);
    }

}
