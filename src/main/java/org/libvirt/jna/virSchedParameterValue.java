package org.libvirt.jna;

import com.sun.jna.Union;

/**
 * JNA mapping for the virSchedParameterValue structure
 */
public class virSchedParameterValue extends Union {
    public int i; /* data for integer case */
    public int ui; /* data for unsigned integer case */
    public long l; /* data for long long integer case */
    public long ul; /* data for unsigned long long integer case */
    public double d; /* data for double case */
    public byte b; /* data for char case */
}
