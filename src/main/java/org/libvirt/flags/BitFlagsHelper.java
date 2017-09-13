package org.libvirt.flags;

import org.libvirt.BitFlags;

public final class BitFlagsHelper {
    // bitwise-OR
    public static int OR(BitFlags... flags) {
        int ret = 0;
        for (BitFlags f : flags) {
            ret |= f.getBit();
        }
        return ret;
    }

    final static BitFlags[] NONE = {};
}
