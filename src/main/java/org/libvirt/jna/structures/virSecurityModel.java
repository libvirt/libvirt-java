package org.libvirt.jna.structures;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

public class virSecurityModel extends Structure {
    private static final int VIR_SECURITY_MODEL_BUFLEN = 256 + 1;
    private static final int VIR_SECURITY_DOI_BUFLEN = 256 + 1;

    private static final List<String> fields = Arrays.asList("model", "doi");

    public byte model[] = new byte[VIR_SECURITY_MODEL_BUFLEN];
    public byte doi[] = new byte[VIR_SECURITY_DOI_BUFLEN];

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
