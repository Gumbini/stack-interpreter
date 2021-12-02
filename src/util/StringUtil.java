package util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class StringUtil {
    /**
     * Dummy constructor: This is a utility class which should not be instantiated.
     */
    private StringUtil() {
    }

    public static String[] tokenize(final String line) throws IllegalArgumentException {
        if (countChar(line, '\'') % 2 != 0)
            throw new IllegalArgumentException("The string \"" + line + "\" contains an odd number of single-quotes!");
        if (countChar(line, '"') % 2 != 0)
            throw new IllegalArgumentException("The string \"" + line + "\" contains an odd number of double-quotes!");

        final ArrayList<String> list = new ArrayList<>();
        final Matcher matcher = Pattern.compile("\"([^\"]*)\"|'([^']*)'|(\\S+)").matcher(line);

        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // "Quoted" argument
                list.add(matcher.group(1));
                continue;
            }
            if (matcher.group(2) != null) {
                // 'Quoted' argument
                list.add(matcher.group(2));
                continue;
            }
            // Plain argument without whitespace
            list.add(matcher.group(3));
        }

        return list.toArray(new String[0]);
    }

    /**
     * Calculate the number of occurrences of a particular character in the given String
     *
     * @param str a String
     * @param chr a char
     * @return the count
     */
    public static long countChar(String str, char chr) {
        return str.chars().filter(c -> c == chr).count();
    }
}
