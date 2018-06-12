package org.libvirt.flags;

public final class DomainDeviceModifyFlags {

    public static final int VIR_DOMAIN_DEVICE_MODIFY_CONFIG = DomainModificationImpactFlags.VIR_DOMAIN_AFFECT_CONFIG;

    public static final int VIR_DOMAIN_DEVICE_MODIFY_CURRENT = DomainModificationImpactFlags.VIR_DOMAIN_AFFECT_CURRENT;

    public static final int VIR_DOMAIN_DEVICE_MODIFY_LIVE = DomainModificationImpactFlags.VIR_DOMAIN_AFFECT_LIVE;

    public static final int VIR_DOMAIN_DEVICE_MODIFY_FORCE = 4;
}
