package com.commons.common.utils;

public class CamelCaseUtils {

    private static final char SEPARATOR = '_';

    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i >= 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(s.length());
        boolean prevUpper = false;
        boolean splitToUpper = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (splitToUpper) {
                sb.append(Character.toUpperCase(c));
                splitToUpper = false;
                prevUpper = true;
                continue;
            }
            if (c == '_' || c == '-' || ((int) c) == 32) {
                if (i < s.length() - 1 && Character.isLowerCase(s.charAt(i + 1))) {
                    splitToUpper = true;
                }
                continue;
            }
            boolean upper = Character.isUpperCase(c);
            if (i == 0 && upper) {
                sb.append(Character.toLowerCase(c));
                prevUpper = true;
                continue;
            }
            if (upper && prevUpper) {
                if (i < s.length() - 1 && Character.isLowerCase(s.charAt(i + 1))) {
                    sb.append(Character.toUpperCase(c));
                    prevUpper = true;
                    continue;
                }
                sb.append(Character.toLowerCase(c));
                prevUpper = true;
                continue;
            }
            if (!upper) {
                sb.append(Character.toLowerCase(c));
                prevUpper = false;
                continue;
            }
            if (upper) {
                sb.append(Character.toUpperCase(c));
                prevUpper = true;
                continue;
            }
        }
        return sb.toString();
    }

    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void main(String[] args) {
        System.out.println(CamelCaseUtils.toUnderlineName("ISOCertifiedStaff"));
        System.out.println(CamelCaseUtils.toUnderlineName("CertifiedStaff"));
        System.out.println(CamelCaseUtils.toCamelCase("Anomaly ID".replaceAll(" ", "_")));
        System.out.println(CamelCaseUtils.toCamelCase("Sub-type"));
        System.out.println(CamelCaseUtils.toCamelCase("certified_staff"));
        System.out.println(CamelCaseUtils.toCamelCase("user_id"));
    }
}  