package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

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

    private static final List<String> fields = Arrays.asList(
            "type", "capacity", "allocation");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
