package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

import java.util.Arrays;
import java.util.List;

/**
 * JNA mapping for the virDomainStatsRecord structure.
 *
 * Used with virConnectGetAllDomainStats and virDomainListGetStats.
 */
public class virDomainStatsRecord extends Structure {
    public DomainPointer dom;
    public Pointer params;
    public int nparams;

    public virDomainStatsRecord() {
        super();
    }

    public virDomainStatsRecord(Pointer p) {
        super(p);
        read();
    }

    private static final List<String> FIELDS = Arrays.asList(
        "dom", "params", "nparams");

    @Override
    protected List<String> getFieldOrder() {
        return FIELDS;
    }
}
