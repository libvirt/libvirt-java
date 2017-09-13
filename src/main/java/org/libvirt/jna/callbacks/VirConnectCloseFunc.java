package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.libvirt.jna.pointers.ConnectionPointer;

/**
 * Callback interface for connection closed events.
 */
public interface VirConnectCloseFunc extends Callback {
    void callback(ConnectionPointer VCP, int reason, Pointer opaque);
}
