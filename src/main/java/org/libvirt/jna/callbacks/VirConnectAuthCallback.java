package org.libvirt.jna.callbacks;

import com.sun.jna.Callback;
import com.sun.jna.Pointer;
import org.libvirt.jna.structures.virConnectCredential;

/**
 * Callback interface for authorization
 */
public interface VirConnectAuthCallback extends Callback {
    int authCallback(virConnectCredential cred, int ncred, Pointer cbdata);
}
