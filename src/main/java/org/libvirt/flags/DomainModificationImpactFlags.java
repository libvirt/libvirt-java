package org.libvirt.flags;

public class DomainModificationImpactFlags {
    /**
     * Affect current domain state.
     */
    public static final int VIR_DOMAIN_AFFECT_CURRENT = 0;
    /**
     * Affect running domain state.
     */
    public static final int VIR_DOMAIN_AFFECT_LIVE = 1;
    /**
     * Affect persistent domain state. 1 << 2 is reserved for virTypedParameterFlags
     */
    public static final int VIR_DOMAIN_AFFECT_CONFIG = 2;
}
