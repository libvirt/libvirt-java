package org.libvirt.jna;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virConnectCredential structure
 */
public class virConnectCredential extends Structure implements Structure.ByReference {
    public int type;
    public String prompt;
    public String challenge;
    public String defresult;
    // public Pointer result;
    public String result;
    public int resultlen;
}
