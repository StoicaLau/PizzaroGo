package com.pizzaro_go.common.utils;

/**
 * Utility class providing common String manipulation methods.
 */
public final class StringUtils {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This class currently contains only static utility methods for String
     * manipulation.
     */
    private StringUtils() {
        // Utility class — prevent instantiation
    }

    /**
     * Capitalizes a string: first letter uppercase, the rest lowercase.
     * Returns the original value if it is null or empty.
     *
     * @param value the string to capitalize
     * @return the capitalized string, or the original value if null or empty
     */
    public static String capitalize(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.substring(0, 1).toUpperCase() +
                value.substring(1).toLowerCase();
    }
}
