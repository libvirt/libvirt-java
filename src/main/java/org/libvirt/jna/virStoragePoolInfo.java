package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

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

    private static final List<String> fields = Arrays.asList(
            "state", "capacity", "allocation", "available");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
