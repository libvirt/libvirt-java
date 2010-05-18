package org.libvirt;

import java.util.EnumSet;
import java.util.HashMap;

public enum CPUCompareResult {
    VIR_CPU_COMPARE_ERROR(-1), VIR_CPU_COMPARE_INCOMPATIBLE(0), VIR_CPU_COMPARE_IDENTICAL(1), VIR_CPU_COMPARE_SUPERSET(
            2);

    static HashMap<Integer, CPUCompareResult> lookup = new HashMap<Integer, CPUCompareResult>();

    static {
        for (CPUCompareResult s : EnumSet.allOf(CPUCompareResult.class)) {
            lookup.put(s.getReturnCode(), s);
        }
    }

    public static CPUCompareResult get(int value) {
        return lookup.get(value);
    }

    private final int returnCode;

    CPUCompareResult(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getReturnCode() {
        return returnCode;
    }

}
