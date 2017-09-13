package org.libvirt.jna.callbacks;

import com.sun.jna.Pointer;
import org.libvirt.jna.pointers.ConnectionPointer;
import org.libvirt.jna.pointers.DomainPointer;

public interface VirConnectDomainEventIOErrorCallback extends VirDomainEventCallback {
    void eventCallback(ConnectionPointer virConnectPtr, DomainPointer virDomainPointer, String srcPath, String devAlias, int action, Pointer opaque);
}
