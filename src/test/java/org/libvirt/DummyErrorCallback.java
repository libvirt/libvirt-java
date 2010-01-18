package org.libvirt;

import org.libvirt.jna.virError;

import com.sun.jna.Pointer;

public class DummyErrorCallback extends ErrorCallback {
    public boolean error = false;

    @Override
    public void errorCallback(Pointer userData, virError error) {
        this.error = true;
    }

}
