package org.libvirt;

/**
 * A single record returned by
 * {@link Connect#getAllDomainStats(int, int)} or
 * {@link Connect#getDomainListStats(Domain[], int, int)}.
 *
 * <p>Carries the {@link Domain} the stats describe and the typed parameters
 * for that domain, in the order returned by libvirt (for example
 * {@code "state.state"}, {@code "cpu.time"}, {@code "block.0.rd.bytes"}).
 *
 * <p>The {@code Domain} reference is owned by the caller and must be freed
 * (e.g. via {@link Domain#free()}) once the record is no longer needed.
 */
public class DomainStatsRecord {
    public Domain domain;
    public TypedParameter[] params;

    public DomainStatsRecord(Domain domain, TypedParameter[] params) {
        this.domain = domain;
        this.params = params;
    }
}
