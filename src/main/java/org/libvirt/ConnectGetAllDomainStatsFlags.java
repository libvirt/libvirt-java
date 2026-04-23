package org.libvirt;

/**
 * Bit flags for {@link Connect#getAllDomainStats(int, int)} and
 * {@link Connect#getDomainListStats(Domain[], int, int)}.
 *
 * <p>Matches the {@code VIR_CONNECT_GET_ALL_DOMAINS_STATS_*} constants
 * from libvirt.
 *
 * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virConnectGetAllDomainStatsFlags">
 *     virConnectGetAllDomainStatsFlags</a>
 */
public final class ConnectGetAllDomainStatsFlags {

    /** Include active domains. */
    public static final int ACTIVE        = 1 << 0;

    /** Include inactive domains. */
    public static final int INACTIVE      = 1 << 1;

    /** Include persistent domains. */
    public static final int PERSISTENT    = 1 << 2;

    /** Include transient domains. */
    public static final int TRANSIENT     = 1 << 3;

    /** Include running domains. */
    public static final int RUNNING       = 1 << 4;

    /** Include paused domains. */
    public static final int PAUSED        = 1 << 5;

    /** Include shutoff domains. */
    public static final int SHUTOFF       = 1 << 6;

    /** Include domains in other states. */
    public static final int OTHER         = 1 << 7;

    /** Skip domains whose lock cannot be acquired immediately. */
    public static final int NOWAIT        = 1 << 29;

    /** Expand block stats to cover backing chains. */
    public static final int BACKING       = 1 << 30;

    /** Fail if any requested stats group is not supported by the daemon. */
    public static final int ENFORCE_STATS = 1 << 31;

    private ConnectGetAllDomainStatsFlags() {
    }
}
