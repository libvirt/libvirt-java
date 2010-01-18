package org.libvirt;

import java.util.Arrays;

import org.libvirt.jna.Libvirt;
import org.libvirt.jna.virSchedParameter;
import org.libvirt.jna.virSchedParameterValue;

import com.sun.jna.Native;

/**
 * The abstract parent of the actual Schedparameter classes
 * 
 * @author stoty
 * 
 */
public abstract class SchedParameter {

    public static SchedParameter create(virSchedParameter vParam) {
        SchedParameter returnValue = null;
        if (vParam != null) {
            switch (vParam.type) {
                case (1):
                    returnValue = new SchedIntParameter(vParam.value.i);
                    break;
                case (2):
                    returnValue = new SchedUintParameter(vParam.value.ui);
                    break;
                case (3):
                    returnValue = new SchedLongParameter(vParam.value.l);
                    break;
                case (4):
                    returnValue = new SchedUlongParameter(vParam.value.ul);
                    break;
                case (5):
                    returnValue = new SchedDoubleParameter(vParam.value.d);
                    break;
                case (6):
                    returnValue = new SchedBooleanParameter(vParam.value.b);
                    break;
            }
            returnValue.field = Native.toString(vParam.field);
        }
        return returnValue;
    }

    public static virSchedParameter toNative(SchedParameter param) {
        virSchedParameter returnValue = new virSchedParameter();
        returnValue.value = new virSchedParameterValue();
        returnValue.field = Arrays.copyOf(param.field.getBytes(), Libvirt.VIR_DOMAIN_SCHED_FIELD_LENGTH);
        returnValue.type = param.getType();
        switch (param.getType()) {
            case (1):
                returnValue.value.i = ((SchedIntParameter) param).value;
                break;
            case (2):
                returnValue.value.ui = ((SchedUintParameter) param).value;
                break;
            case (3):
                returnValue.value.l = ((SchedLongParameter) param).value;
                break;
            case (4):
                returnValue.value.ul = ((SchedUlongParameter) param).value;
                break;
            case (5):
                returnValue.value.d = ((SchedDoubleParameter) param).value;
                break;
            case (6):
                returnValue.value.b = (byte) (((SchedBooleanParameter) param).value ? 1 : 0);
                break;

        }
        return returnValue;
    }

    /**
     * Parameter name
     */
    public String field;

    /**
     * The type of the parameter
     * 
     * @return the Type of the parameter
     */
    public abstract int getType();

    /**
     * Utility function for displaying the type
     * 
     * @return the Type of the parameter as string
     */
    public abstract String getTypeAsString();

    /**
     * Utility function for displaying the value
     * 
     * @return the value of the parameter in String form
     */
    public abstract String getValueAsString();
}
