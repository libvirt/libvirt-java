package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.libvirt.jna.structures.virError;

/**
 * Error callback
 */
public interface VirErrorCallback extends Callback {
    void errorCallback(Pointer userData, virError error);
}
