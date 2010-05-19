package org.libvirt.jna;

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
}
