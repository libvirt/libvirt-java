package org.libvirt;

/**
 */
public enum KeycodeSet {
    /**
     * Linux key code set.
     * <p>
     * Defined in the linux/input.h header, this set of key codes is able
     * to represent any scan code from any type of keyboard.
     */
    LINUX,

    /** IBM XT keyboard code set */
    XT,

    /** AT set 1 key codes */
    ATSET1,

    /** AT set 2 key codes */
    ATSET2,

    /** AT set 3 key codes */
    ATSET3,

    /** Apple OS-X virtual key codes */
    OSX,

    /** */
    XT_KBD,

    /** Key code set as defined by the USB HID specification */
    USB,

    /**
     * Microsoft Windows virtual key code set
     * <p>
     * Reference: <a target='blank'
     *  href='http://msdn.microsoft.com/en-us/library/windows/desktop/dd375731(v=vs.85).aspx'
     * >MSDN Virtual-Key Codes</a>.
     */
    WIN32,

    /** Key code set as used by GTK-VNC and QEMU */
    RFB;
}
