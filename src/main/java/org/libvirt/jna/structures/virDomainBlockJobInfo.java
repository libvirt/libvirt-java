package org.libvirt.jna.structures;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class virDomainBlockJobInfo extends Structure {
    public int type;
    public long bandwidth;
    public long cur;
    public long end;

    private static final List<String> fields = Arrays.asList("type", "bandwidth", "cur", "end");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
