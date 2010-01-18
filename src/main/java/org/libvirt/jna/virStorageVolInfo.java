package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virStorageVolInfo structure
 */
public class virStorageVolInfo extends Structure {
    public int type;
    public long capacity; // this is a long long in the code, so a long mapping
    // is correct
    public long allocation; // this is a long long in the code, so a long
    // mapping is correct

}