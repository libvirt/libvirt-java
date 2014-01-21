package org.libvirt;

/**
 * An enumeration of constants identifying the type of
 * the owner of a secret.
 *
 * @see Secret#getUsageType
 */
public enum SecretUsageType {
    /** No one uses the secret */
    NONE,

    /** A volume uses the secret */
    VOLUME,

    /** A CEPH object uses the secret */
    CEPH,

    /** A ISCSI object uses the secret */
    ISCSI,

    UNKNOWN
}
