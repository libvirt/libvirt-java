package org.libvirt.event.enums;

import java.util.HashMap;
import java.util.Map;

public enum ConnectDomainEventBlockJobStatus {
    VIR_DOMAIN_BLOCK_JOB_COMPLETED(0),
    VIR_DOMAIN_BLOCK_JOB_FAILED(1),
    VIR_DOMAIN_BLOCK_JOB_CANCELED(2),
    VIR_DOMAIN_BLOCK_JOB_READY(3);

    private int n;

    private static Map<Integer, ConnectDomainEventBlockJobStatus> map = new HashMap<>();

    static {
        for (ConnectDomainEventBlockJobStatus legEnum : ConnectDomainEventBlockJobStatus.values()) {
            map.put(legEnum.n, legEnum);
        }
    }

    ConnectDomainEventBlockJobStatus(final int leg) {
        n = leg;
    }

    public static ConnectDomainEventBlockJobStatus valueOf(int legNo) {
        return map.get(legNo);
    }
}
