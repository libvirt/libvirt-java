package org.libvirt.jna.callbacks;

import com.sun.jna.Pointer;
import org.libvirt.jna.ConnectionPointer;
import org.libvirt.jna.DomainPointer;

public interface VirConnectDomainEventGenericCallback extends VirDomainEventCallback {
    void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, Pointer opaque);
}
