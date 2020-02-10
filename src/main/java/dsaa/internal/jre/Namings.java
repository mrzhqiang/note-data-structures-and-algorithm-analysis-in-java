package dsaa.internal.jre;

import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import java.util.Objects;

public enum Namings {
  ;

  public static String ofCanonical(Class<?> clazz, String... more) {
    String first = clazz.getCanonicalName().toLowerCase().replaceAll("\\.", "-");
    StringBuilder builder = new StringBuilder(first);
    for (String postfix : more) {
      builder.append(Objects.requireNonNull(postfix));
    }
    return builder.toString();
  }

  public static String ofSimple(Class<?> clazz, String... more) {
    String first = ofCamel(clazz.getSimpleName());
    StringBuilder builder = new StringBuilder(first);
    for (String postfix : more) {
      builder.append(Objects.requireNonNull(postfix));
    }
    return builder.toString();
  }

  public static String ofCamel(String camel) {
    Preconditions.checkNotNull(camel, "camel == null");
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < camel.length(); i++) {
      char c = camel.charAt(i);
      if (Ascii.isUpperCase(c)) {
        if (i > 0) {
          builder.append("-");
        }
        builder.append(Ascii.toLowerCase(c));
      } else {
        builder.append(c);
      }
    }
    return builder.toString();
  }
}
