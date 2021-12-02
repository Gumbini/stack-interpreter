package util;

import java.io.*;
import java.util.ArrayList;

public final class FileUtil {
    /**
     * Dummy constructor: This is a utility class which should not be instantiated.
     */
    private FileUtil() {
    }

    public static String[] parseFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        ArrayList<String> stringList = new ArrayList<>();

        String line;
        while ((line = reader.readLine()) != null)
            stringList.add(line);

        reader.close();

        return stringList.toArray(new String[0]);
    }
}
