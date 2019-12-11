package org.libvirt.parameters;

import org.libvirt.LibvirtException;
import org.libvirt.parameters.typed.TypedParameter;
import org.libvirt.parameters.typed.TypedUlongParameter;

public class DomainBlockCopyParameters {
    private long domainBlockCopyBandwidth = 0L;
    private boolean domainBlockCopyBytes = false;

    public DomainBlockCopyParameters() {
    }

    public TypedParameter[] getTypedParameters() throws LibvirtException {
        if (!isDomainBlockCopyBytes()) {
            long limit = Long.MAX_VALUE >> 20;
            if (domainBlockCopyBandwidth > limit) {
                throw new LibvirtException("Bandwidth must be less than " + limit);
            }
            domainBlockCopyBandwidth<<=20;
        }

        TypedParameter[] blockCopyParameters = new TypedParameter[1];
        blockCopyParameters[0] = new TypedUlongParameter(domainBlockCopyBandwidth, "bandwidth");
        return blockCopyParameters;
    }

    public long getDomainBlockCopyBandwidth() {
        return domainBlockCopyBandwidth;
    }

    public boolean isDomainBlockCopyBytes() {
        return domainBlockCopyBytes;
    }

    public void setDomainBlockCopyBytes(final boolean domainBlockCopyBytes) {
        this.domainBlockCopyBytes = domainBlockCopyBytes;
    }

    public void setDomainBlockCopyBandwidth(final long bandwidth) {
        domainBlockCopyBandwidth = bandwidth;
    }

}
