package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;

public interface VirEventTimeoutCallback extends Callback {
    void tick(int timerID, Pointer opaque);
}
