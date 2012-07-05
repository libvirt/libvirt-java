package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virError;

import com.sun.jna.Pointer;

/**
 * Callback class to allow users of the API to handle the Error messages in a
 * more robust fashion.
 *
 * @author bkearney
 *
 */
public class ErrorCallback implements Libvirt.VirErrorCallback {
    public void errorCallback(Pointer userData, virError error) {
        // By default, do nothing. This will silence the default
        // logging done by the C code. Other users can override this
        // and do more interesting things.
    }
}
