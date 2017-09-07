package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface VirFreeCallback extends Callback {
    void freeCallback(Pointer opaque);
}
