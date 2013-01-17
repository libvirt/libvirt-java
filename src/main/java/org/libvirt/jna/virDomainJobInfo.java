package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class virDomainJobInfo extends Structure {
    public int type;
    public long timeElapsed;
    public long timeRemaining;
    public long dataTotal;
    public long dataProcessed;
    public long dataRemaining;
    public long memTotal;
    public long memProcessed;
    public long memRemaining;
    public long fileTotal;
    public long fileProcessed;
    public long fileRemaining;

    private static final List fields = Arrays.asList(
            "type", "timeElapsed", "timeRemaining", "dataTotal",
            "dataProcessed", "dataRemaining", "memTotal", "memProcessed",
            "memRemaining", "fileTotal", "fileProcessed", "fileRemaining");

    @Override
    protected List getFieldOrder() {
        return fields;
    }
}
