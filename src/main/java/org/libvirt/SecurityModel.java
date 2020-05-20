package org.libvirt;

import org.libvirt.jna.Libvirt;
import com.sun.jna.Native;

/**
 * A security model used for mandatory access control.
 *
 * @see Connect#getSecurityModel
 */
public final class SecurityModel {
    private final String model;
    private final String doi;

    SecurityModel(final Libvirt.SecurityModel secmodel) {
        model = Native.toString(secmodel.model, "UTF-8");
        doi = Native.toString(secmodel.doi, "UTF-8");
    }

    /**
     * Returns the model of this SecurityModel.
     *
     * @return the model string
     */
    public String getModel() {
        return model;
    }

    /**
     * Returns the DOI, domain of interpretation of this security model.
     *
     * @return the DOI
     */
    public String getDomainOfInterpretation() {
        return doi;
    }
}
