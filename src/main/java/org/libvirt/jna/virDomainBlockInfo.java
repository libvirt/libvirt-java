package org.libvirt.jna;

import com.sun.jna.Structure;

public class virDomainBlockInfo extends Structure {
    public long capacity;
    public long allocation;
    public long physical;

    protected java.util.List getFieldOrder() {
        return java.util.Arrays.asList(new String[] {
            "capacity", "allocation", "physical" });
    }
}
