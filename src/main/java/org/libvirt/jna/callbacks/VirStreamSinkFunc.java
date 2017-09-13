package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import org.libvirt.jna.pointers.StreamPointer;

public interface VirStreamSinkFunc extends Callback {
    int sinkCallback(StreamPointer virStreamPtr, String data, NativeLong nbytes, Pointer opaque);
}
