package de.adihubba;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class ObjectUtils {

    private static final int      DEFAULT_DOUBLE_PRECISION = 8;
    private static final double[] POWER_OF_TEN             = new double[15];

    static {
        for (int i = 0; i < POWER_OF_TEN.length; i++) {
            POWER_OF_TEN[i] = Math.pow(10, i);
        }
    }

    public static boolean smallerOrEqualsDoublePrecision(double double1, double double2) {
        return smallerOrEqualsDoublePrecision(double1, double2, DEFAULT_DOUBLE_PRECISION);
    }

    public static boolean smallerOrEqualsDoublePrecision(double double1, double double2, int precision) {
        // try to save the POWER operation
        double factor = (precision >= 0 && precision < POWER_OF_TEN.length) ? POWER_OF_TEN[precision] : Math.pow(10, precision);
        long result = Math.round((double1 - double2) * factor);
        if (result <= 0) {
            return true;
        }
        return false;
    }

    public static boolean equalsDoublePrecision(double double1, double double2) {
        return equalsDoublePrecision(double1, double2, DEFAULT_DOUBLE_PRECISION);
    }

    public static boolean equalsDoublePrecision(double double1, double double2, int precision) {
        double absDifference = Math.abs(double1 - double2);

        if (absDifference == 0.0) {
            // don't calculate, if result is already zero
            return true;
        }

        if (absDifference >= 1) {
            return false;
        }
        // try to save the POWER operation
        double factor = (precision >= 0 && precision < POWER_OF_TEN.length) ? POWER_OF_TEN[precision] : Math.pow(10, precision);
        return (absDifference * factor < 1);
    }

    public static boolean smallerDoublePrecision(double double1, double double2) {
        return smallerDoublePrecision(double1, double2, DEFAULT_DOUBLE_PRECISION);
    }

    public static boolean smallerDoublePrecision(double double1, double double2, int precision) {
        // try to save the POWER operation
        double factor = (precision >= 0 && precision < POWER_OF_TEN.length) ? POWER_OF_TEN[precision] : Math.pow(10, precision);
        long result = Math.round((double1 - double2) * factor);
        if (result < 0) {
            return true;
        }
        return false;
    }

    public static <O> List<O> asReadonlyList(O... objects) {
        return objects == null ? Collections.EMPTY_LIST : Arrays.asList(objects);
    }

    public static boolean equalsObject(Object obj1, Object obj2) {
        if (obj1 == obj2) {
            // both objects are identical or null
            return true;
        }

        if (obj1 == null || obj2 == null) {
            // only one side is null
            return false;
        }

        // from here both of them are not null
        // don't compare the classes as it is done in the equals routine and should be up
        // to objects we are comparing
        return obj1.equals(obj2);
    }

    public static boolean arrayHasElements(Object[] obj) {
        if (obj != null && obj.length != 0) {
            for (Object object : obj) {
                if (object != null) {
                    return true;
                }
            }
        }
        return false;
    }

}
