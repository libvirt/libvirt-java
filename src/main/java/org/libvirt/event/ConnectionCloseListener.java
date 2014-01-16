package org.libvirt.event;

import org.libvirt.Connect;

/**
 * Interface for receiving events when a connection is closed.
 */
public interface ConnectionCloseListener {
    void onClose(Connect conn, ConnectionCloseReason reason);
}
