package org.libvirt.flags;

public enum DomainMetadataFlags {
    /**
     * Operate on <title>
     */
    VIR_DOMAIN_METADATA_TITLE(1),
    /**
     * Operate on <description>
     */
    VIR_DOMAIN_METADATA_DESCRIPTION(0),
    /**
     * Operate on <metadata>
     */
    VIR_DOMAIN_METADATA_ELEMENT(2);

    private final int flag;

    DomainMetadataFlags(int flag) {
        this.flag = flag;
    }

    public int getValue() {
        return flag;
    }
}
