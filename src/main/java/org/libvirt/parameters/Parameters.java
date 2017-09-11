package org.libvirt.parameters;

import org.libvirt.jna.virTypedParameter;
import org.libvirt.parameters.typed.TypedParameter;

public abstract class Parameters {
    private final TypedParameter[] typedParameters;

    protected Parameters(final TypedParameter[] typedParameters) {
        this.typedParameters = typedParameters;
    }

    protected TypedParameter[] getTypedParameters() {
        return this.typedParameters;
    }

    public virTypedParameter[] getVirTypedParameters() {
        virTypedParameter[] ret = new virTypedParameter[typedParameters.length];
        for (int x = 0; x < typedParameters.length; x++) {
            ret[x] = TypedParameter.toNative(typedParameters[x]);
        }

        return ret;
    }

    public int getVirTypedParametersLength() {
        return typedParameters.length;
    }
}
