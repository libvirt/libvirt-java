package org.libvirt;

import com.sun.jna.Pointer;
import org.libvirt.jna.virError;

public class DummyErrorCallback extends ErrorCallback {
    public boolean error = false;

    @Override
    public void errorCallback(Pointer userData, virError error) {
        this.error = true;
    }

}
