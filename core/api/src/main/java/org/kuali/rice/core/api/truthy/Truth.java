package org.kuali.rice.core.api.truthy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public enum Truth {
    TRUE("true", "yes", "Y", "on", "1", "t", "enabled"),
    FALSE("false", "no", "N", "off", "0", "f", "disabled");

    private final Collection<String> truthStrings;

    private Truth(String... vals) {
        truthStrings = Collections.unmodifiableCollection(Arrays.asList(vals));
    }

    public Collection<String> getTruthStrings() {
        return truthStrings;
    }

    /**
     * Returns the Boolean value of a string ignoring case.
     *
     * If the string is not a recognized boolean value, then this method
     * will return null.  If the str is null, this method will return null.
     *
     * @param str the string.
     * @return the boolean value or null.
     */
    public static Boolean strToBooleanIgnoreCase(String str) {
        return strToBooleanIgnoreCase(str, null);
    }

    /**
     * Returns the Boolean value of a string ignoring case.
     *
     * If the string is not a recognized boolean value, then this method
     * will return null.  If the str is null, this method will return null.
     *
     * @param str the string.
     * @param the default value to use when the str is not a recognized as a boolean value.
     * @return the boolean value or the specified default value.
     */
    public static Boolean strToBooleanIgnoreCase(String str, Boolean defaultValue) {
        if (str == null) {
            return defaultValue;
        }

        for (String s : TRUE.getTruthStrings()) {
            if (s.equalsIgnoreCase(str)) {
                return Boolean.TRUE;
            }
        }

        for (String s : FALSE.getTruthStrings()) {
            if (s.equalsIgnoreCase(str)) {
                return Boolean.FALSE;
            }
        }

        return defaultValue;
    }
}
