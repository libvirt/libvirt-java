package org.libvirt;

import org.libvirt.jna.Libvirt;
import com.sun.jna.Native;

/**
 * Represents a security label used for mandatory access control.
 *
 * @see Domain#getSecurityLabel
 */
public final class SecurityLabel {
    private final String label;
    private final boolean enforced;
    private static final byte NUL = 0;

    SecurityLabel(final Libvirt.SecurityLabel seclabel) {
        label = Native.toString(seclabel.label, "UTF-8");
        enforced = seclabel.enforcing == 1;
    }

    /**
     * Returns the label of this SecurityLabel.
     *
     * @return the security label string
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns true if the security policy is being enforced.
     *
     * @return true if the policy is enforced, false otherwise
     */
    public boolean isEnforced() {
        return enforced;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("(label=")
            .append(label)
            .append(", enforced=")
            .append(enforced)
            .append(")")
            .toString();
    }
}
