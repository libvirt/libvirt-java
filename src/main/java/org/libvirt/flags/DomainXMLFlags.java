package org.libvirt.flags;

public final class DomainXMLFlags {
    /**
     * dump security sensitive information too
     */
    static final int VIR_DOMAIN_XML_SECURE = 1;
    /**
     * dump inactive domain information
     */
    static final int VIR_DOMAIN_XML_INACTIVE = 2;
    /**
     * update guest CPU requirements according to host CPU
     */
    static final int VIR_DOMAIN_XML_UPDATE_CPU = (1 << 2);
}
