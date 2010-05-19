package org.libvirt.jna;

import com.sun.jna.Structure;

public class virDomainBlockInfo extends Structure {
    public long capacity;
    public long allocation;
    public long physical;

}
