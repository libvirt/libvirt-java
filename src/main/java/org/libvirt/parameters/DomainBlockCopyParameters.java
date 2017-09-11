package org.libvirt.parameters;

import org.libvirt.parameters.typed.TypedParameter;
import org.libvirt.parameters.typed.TypedUintParameter;
import org.libvirt.parameters.typed.TypedUlongParameter;

public class DomainBlockCopyParameters extends Parameters {
    private final TypedUlongParameter domainBlockCopyBandwidth = new TypedUlongParameter(0L, "bandwidth");
    private final TypedUintParameter domainBlockCopyGranularity = new TypedUintParameter(0, "granularity");
    private final TypedUlongParameter domainBlockCopyBufSize = new TypedUlongParameter(0L, "buf-size");

    public DomainBlockCopyParameters() {
        super(new TypedParameter[3]);

        this.getTypedParameters()[0] = domainBlockCopyBandwidth;
        this.getTypedParameters()[1] = domainBlockCopyGranularity;
        this.getTypedParameters()[2] = domainBlockCopyBufSize;
    }

    public TypedUlongParameter getDomainBlockCopyBandwidth() {
        return domainBlockCopyBandwidth;
    }

    public TypedUintParameter getDomainBlockCopyGranularity() {
        return domainBlockCopyGranularity;
    }

    public TypedUlongParameter getDomainBlockCopyBufSize() {
        return domainBlockCopyBufSize;
    }
}
