package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.libvirt.jna.pointers.StreamPointer;

public interface VirStreamEventCallback extends Callback {
    void eventCallback(StreamPointer virStreamPointer, int events, Pointer opaque);
}
