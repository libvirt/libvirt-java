package org.libvirt;

public class DomainJobStats {
    public int type;
    public TypedParameter[] stats;

    public DomainJobStats(int type, TypedParameter[] stats) {
        this.type = type;
        this.stats = stats;
    }
}
