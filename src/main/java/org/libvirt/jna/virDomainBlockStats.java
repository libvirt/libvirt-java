package org.libvirt.jna;

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
}
