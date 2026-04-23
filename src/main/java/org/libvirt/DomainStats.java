package org.libvirt;

/**
 * Bit flags selecting which stats groups {@link
 * Connect#getAllDomainStats(int, int)} (and {@link
 * Connect#getDomainListStats(Domain[], int, int)}) should return.
 *
 * <p>Matches the {@code VIR_DOMAIN_STATS_*} constants from libvirt.
 *
 * @see <a href="https://libvirt.org/html/libvirt-libvirt-domain.html#virDomainStatsTypes">
 *     virDomainStatsTypes</a>
 */
public final class DomainStats {

    /** Domain state. */
    public static final int STATE     = 1 << 0;

    /** CPU totals. */
    public static final int CPU_TOTAL = 1 << 1;

    /** Balloon memory. */
    public static final int BALLOON   = 1 << 2;

    /** Per-vCPU stats. */
    public static final int VCPU      = 1 << 3;

    /** Network interface stats. */
    public static final int INTERFACE = 1 << 4;

    /** Block device stats. */
    public static final int BLOCK     = 1 << 5;

    /** Performance event stats. */
    public static final int PERF      = 1 << 6;

    /** IOThread stats. */
    public static final int IOTHREAD  = 1 << 7;

    /** Memory stats. */
    public static final int MEMORY    = 1 << 8;

    /** Dirty rate stats. */
    public static final int DIRTYRATE = 1 << 9;

    /** Hypervisor-specific VM stats. */
    public static final int VM        = 1 << 10;

    private DomainStats() {
    }
}
