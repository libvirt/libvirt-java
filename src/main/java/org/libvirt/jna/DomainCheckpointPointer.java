package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.PointerType;

/**
 * Pointer class to provide type safety to the jna interface.
 */
public class DomainCheckpointPointer extends PointerType {
    
    public DomainCheckpointPointer() {
    }

    public DomainCheckpointPointer(Pointer p) {
        super(p);
    }
}

