package org.libvirt.jna;

import java.util.Arrays;
import java.util.List;

import com.sun.jna.Structure;

/**
 * JNA mapping for the virError structure
 */
public class virError extends Structure {
    public int code;
    public int domain;
    public String message;
    public int level;
    public ConnectionPointer conn;
    public DomainPointer dom;
    public String str1;
    public String str2;
    public String str3;
    public int int1;
    public int int2;
    public NetworkPointer net;

    private static final List<String> fields = Arrays.asList(
            "code", "domain", "message", "level", "conn", "dom",
            "str1", "str2", "str3", "int1", "int2", "net");

    @Override
    protected List<String> getFieldOrder() {
        return fields;
    }
}
