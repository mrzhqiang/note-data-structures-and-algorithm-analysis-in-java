package internal.jre;

import com.google.common.base.Ascii;
import com.google.common.base.Strings;

public enum Namings {
    ; // no instance

    public static final String SYMBOL = "-";

    public static String ofCanonical(Class<?> clazz, String... more) {
        String canonicalName = clazz.getCanonicalName();
        String first = Strings.isNullOrEmpty(canonicalName) ? ""
                : canonicalName.toLowerCase().replaceAll("\\.", SYMBOL);
        StringBuilder builder = new StringBuilder(first);
        for (String postfix : more) {
            builder.append(Strings.nullToEmpty(postfix));
        }
        return builder.toString();
    }

    public static String ofSimple(Class<?> clazz, String... more) {
        String first = ofCamel(clazz.getSimpleName());
        StringBuilder builder = new StringBuilder(first);
        for (String postfix : more) {
            builder.append(Strings.nullToEmpty(postfix));
        }
        return builder.toString();
    }

    public static String ofCamel(String camel) {
        if (Strings.isNullOrEmpty(camel)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < camel.length(); i++) {
            char c = camel.charAt(i);
            if (Ascii.isUpperCase(c)) {
                if (i > 0) {
                    builder.append(SYMBOL);
                }
                builder.append(Ascii.toLowerCase(c));
            } else {
                builder.append(c);
            }
        }
        return builder.toString();
    }

}
