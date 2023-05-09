package internal.jre;

import com.google.common.base.CaseFormat;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.util.stream.Collectors;

/**
 * 命名工具。
 */
public final class Namings {
    private Namings() {
        // no instances.
    }

    /**
     * 点符号。
     */
    public static final char DOT = '.';
    /**
     * 下划线。
     */
    public static final char UNDERSCORE = '_';
    /**
     * 空字符串。
     */
    public static final String EMPTY_STRING = "";
    /**
     * 点符号分割器。
     */
    public static final Splitter DOT_SPLITTER = Splitter.on(DOT).omitEmptyStrings().trimResults();

    /**
     * 驼峰转连字符命名。
     * <p>
     * 注意：如果首字母不是驼峰，比如 path.to.Camel 字符串，会转为 path.to.-camel 字符串。
     * <p>
     * 此时建议按 '.' 字符分割为字符串数组，再遍历调用当前方法。
     *
     * @param camel 驼峰字符串。
     * @return 带连字符的名称。
     */
    public static String ofCamelToHyphen(String camel) {
        if (camel == null) {
            return EMPTY_STRING;
        }

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, camel);
    }

    /**
     * 规范命名。
     * <p>
     * 即类的全限定规范。
     * <p>
     * 其中驼峰命名会被替换为 '-' 连字符。
     * <p>
     * 包层级符号 '.' 会被替换为 '_' 连字符。
     * <p>
     * 以上替换是为了适应文件命名格式。
     *
     * @param clazz 类对象。
     * @param more  更多。表示需要追加的后缀字符串。
     * @return 类的全限定名称。
     */
    public static String ofCanonical(Class<?> clazz, String... more) {
        if (clazz == null) {
            return EMPTY_STRING;
        }

        //noinspection UnstableApiUsage
        String name = DOT_SPLITTER.splitToStream(clazz.getCanonicalName())
                .map(Namings::ofCamelToHyphen)
                .collect(Collectors.joining(String.valueOf(UNDERSCORE)));
        return appendMore(name, more);
    }

    /**
     * 简单命名。
     *
     * @param clazz 类对象。
     * @param more  更多。表示需要追加的后缀字符串。
     * @return 类的简单名称。
     */
    public static String ofSimple(Class<?> clazz, String... more) {
        if (clazz == null) {
            return EMPTY_STRING;
        }

        String first = ofCamelToHyphen(clazz.getSimpleName());
        return appendMore(first, more);
    }

    private static String appendMore(String first, String... more) {
        if (more == null || more.length == 0) {
            return first;
        }

        StringBuilder builder = new StringBuilder(first);
        for (String postfix : more) {
            if (!Strings.isNullOrEmpty(postfix)
                    && CharMatcher.whitespace().matchesNoneOf(postfix)
                    && CharMatcher.breakingWhitespace().matchesNoneOf(postfix)) {
                builder.append(postfix);
            }
        }
        return builder.toString();
    }

}
