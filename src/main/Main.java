package main;

import computation.StackMachine;
import util.FileUtil;

import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0)
            throw new IllegalArgumentException("Please specify a path! (e.g. \".\")");

        String[] lines = FileUtil.parseFile(new File(args[0]));

        new StackMachine(lines).execute();
    }
}
