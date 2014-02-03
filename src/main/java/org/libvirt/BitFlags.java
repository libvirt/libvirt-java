package org.libvirt;

public interface BitFlags {
    int getBit();
}

final class BitFlagsHelper {
    // bitwise-OR
    static int OR(BitFlags[] flags) {
        int ret = 0;
        for (BitFlags f: flags) {
            ret |= f.getBit();
        }
        return ret;
    }

    final static BitFlags[] NONE = {};
}
