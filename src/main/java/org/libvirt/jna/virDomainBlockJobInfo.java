package org.libvirt.jna;

import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

public class virDomainBlockJobInfo extends Structure {
    public int type;
    public long bandwidth;
    public long cur;
    public long end;


    private static final List<String> FIELDS = Arrays.asList(
            "type", "bandwidth", "cur", "end");

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
