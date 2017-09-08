package org.libvirt.jna.callbacks;

import com.sun.jna.Pointer;
import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.DomainPointer;

// PMWakeup and PMSuspend have the same callback interface.
public interface VirConnectDomainEventPMChangeCallback extends VirDomainEventCallback {
    void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, int reason, Pointer opaque);
}
