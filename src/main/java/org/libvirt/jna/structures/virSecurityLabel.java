package org.libvirt.jna.structures;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class virSecurityLabel extends Structure {
    private static final int VIR_SECURITY_LABEL_BUFLEN = 4096 + 1;
    private static final List<String> fields = Arrays.asList("label", "enforcing");

    public byte label[] = new byte[VIR_SECURITY_LABEL_BUFLEN];
    public int enforcing;

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
