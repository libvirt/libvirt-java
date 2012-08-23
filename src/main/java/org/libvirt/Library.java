package org.libvirt;

import org.libvirt.jna.Libvirt;

/**
 * This class represents an instance of the JNA mapped libvirt
 * library.
 *
 * The library will get loaded when first accessing this class.
 */
final class Library {
    final static Libvirt libvirt;

    // Load the native part
    static {
        Libvirt.INSTANCE.virInitialize();
        libvirt = Libvirt.INSTANCE;
        try {
            ErrorHandler.processError(Libvirt.INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Library() {}
}
