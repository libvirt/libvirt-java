package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virDomainBlockStats structure
 */
public class virDomainBlockStats extends Structure {
    public long rd_req; // this is a long long in the code, so a long mapping is
    // correct
    public long rd_bytes;// this is a long long in the code, so a long mapping
    // is correct
    public long wr_req; // this is a long long in the code, so a long mapping is
    // correct
    public long wr_bytes;// this is a long long in the code, so a long mapping
    // is correct
    public long errs; // this is a long long in the code, so a long mapping is
    // correct

    private static final List<String> fields = Arrays.asList(
            "rd_req", "rd_bytes", "wr_req", "wr_bytes", "errs");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
