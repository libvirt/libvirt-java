package org.libvirt;

public interface BitFlags {
    int getBit();
}

final class BitFlagsHelper {
    // bitwise-OR
    final static int OR(final BitFlags[] flags) {
        int ret = 0;
        for (BitFlags f: flags) {
            ret |= f.getBit();
        }
        return ret;
    }

    final static BitFlags[] NONE = {};
}
