package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

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

    private static final List fields = Arrays.asList(
            "credtype", "ncredtype", "cb", "cbdata");

    @Override
    protected List getFieldOrder() {
        return fields;
    }
}
