package org.libvirt.jna.callbacks;

import com.sun.jna.Pointer;
import org.libvirt.jna.pointers.ConnectionPointer;
import org.libvirt.jna.pointers.DomainPointer;

public interface VirConnectDomainEventCallback extends VirDomainEventCallback {
    int eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, int event, int detail, Pointer opaque);
}
