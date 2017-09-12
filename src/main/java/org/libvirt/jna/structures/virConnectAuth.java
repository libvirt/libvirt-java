package org.libvirt.jna.structures;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import org.libvirt.jna.callbacks.VirConnectAuthCallback;

/**
 * JNA mapping for the virConnectAuth structure
 */
public class virConnectAuth extends Structure {
    public Pointer credtype;
    public int ncredtype;
    public VirConnectAuthCallback cb;
    public Pointer cbdata;

    private static final List<String> fields = Arrays.asList(
            "credtype", "ncredtype", "cb", "cbdata");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
