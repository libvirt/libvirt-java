package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virStoragePoolInfo structure
 */
public class virStoragePoolInfo extends Structure {
    public int state;
    public long capacity; // this is a long long in the code, so a long mapping
    // is correct
    public long allocation; // this is a long long in the code, so a long
    // mapping is correct
    public long available; // this is a long long in the code, so a long mapping
    // is correct
}
