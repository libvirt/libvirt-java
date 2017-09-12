package org.libvirt.jna.structures;

import com.sun.jna.Union;

/**
 * JNA mapping for the virTypedParameterValue structure
 */
public class virTypedParameterValue extends Union {
    public int i; /* data for integer case */
    public long l; /* data for long long integer case */
    public double d; /* data for double case */
    public byte b; /* data for char case */
    public String s; /* data for string case */
}
