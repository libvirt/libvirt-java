package org.libvirt;

import com.sun.jna.Pointer;
import org.libvirt.jna.callbacks.VirErrorCallback;
import org.libvirt.jna.structures.virError;

/**
 * Callback class to allow users of the API to handle the Error messages in a
 * more robust fashion.
 */
public class ErrorCallback implements VirErrorCallback {
    public void errorCallback(Pointer userData, virError error) {
        // By default, do nothing. This will silence the default
        // logging done by the C code. Other users can override this
        // and do more interesting things.
    }
}
