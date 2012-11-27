package org.libvirt.jna;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;

/**
 * JNA mapping for the virConnectAuth structure
 */
public class virConnectAuth extends Structure {
    public Pointer credtype;
    public int ncredtype;
    public Libvirt.VirConnectAuthCallback cb;
    public Pointer cbdata;

    protected java.util.List getFieldOrder() {
        return java.util.Arrays.asList(new String[] {
            "credtype", "ncredtype", "cb", "cbdata" });
    }
}
