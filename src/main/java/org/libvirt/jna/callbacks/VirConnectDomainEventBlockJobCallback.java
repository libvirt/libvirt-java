package org.libvirt.jna.callbacks;

import com.sun.jna.Pointer;
import org.libvirt.jna.pointers.ConnectionPointer;
import org.libvirt.jna.pointers.DomainPointer;

public interface VirConnectDomainEventBlockJobCallback extends VirDomainEventCallback {
    void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, String disk, int type, int status, Pointer opaque);
}
