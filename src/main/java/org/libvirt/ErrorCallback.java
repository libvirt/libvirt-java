package org.libvirt;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virError;

import com.sun.jna.Pointer;

/**
 * We diverge from the C implementation There is no explicit cbdata field, you
 * should just add any extra data to the child class's instance.
 * 
 * @author stoty
 * 
 */
public class ErrorCallback implements Libvirt.VirErrorCallback {
    public void errorCallback(Pointer userData, virError error) {
        // By default, do nothing. This will silence the default
        // logging done by the C code. Other users can override this
        // and do more interesting things.
    }
}
