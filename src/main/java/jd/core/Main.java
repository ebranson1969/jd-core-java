package jd.core;

import jd.ide.intellij.config.JDPluginComponent;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class Main {

    private static final String HELP = "Usage: java -jar jd-core-java.jar [options] <compiled.jar> [<destination>]\n" +
            "Options:\n" +
            "    -z - save sources into a zip file\n" +
            "    -n - add line numbers into sources; (false by default)\n" +
            "    -r - not realign line numbers (true by default)\n";

    public static void main(String[] args) {
        try {
            if (args.length < 1) {
                System.err.println(HELP);
                return;
            }

            List<String> paths = new ArrayList<String>();
            for(String arg: args) {
                if(arg.startsWith("-")) {
                    processArgs(arg);
                } else {
                    paths.add(arg);
                }
            }

            if(paths.isEmpty()) {
                throw new IllegalArgumentException("Nothing is found to decomplile.");
            }

            String jarPath = paths.get(0);

            String outPath = paths.size() > 1 ? paths.get(1) : null;

            File folder = new File(jarPath);
            if(folder.isFile()) {
                int numDecompiled = new Decompiler().decompile(jarPath, outPath);
                System.err.println("Decompiled " + numDecompiled + " classes");
            }
            else {
                File[] listOfFiles = folder.listFiles();
                for (int i = 0; i < listOfFiles.length; i++) {
                    try {
                        if (listOfFiles[i].isFile()) {
                            int numDecompiled = new Decompiler().decompile(listOfFiles[i].getPath(), outPath);
                            System.out.println("File " + listOfFiles[i].getPath());
                            System.out.println(" - Directory " + outPath);
                        } else if (listOfFiles[i].isDirectory()) {
                            System.out.println("Directory " + listOfFiles[i].getName());
                        }
                    }
                    catch(Exception ex) {
                        System.out.println("File " + listOfFiles[i].getPath());
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static void processArgs(String arg) {
        if("-".equals(arg)) {
            throw new IllegalArgumentException("Illegal argument: '-'");
        }

        for(Character ch : arg.substring(1).toCharArray()) {
            switch(ch) {
                case 'z':
                    JDPluginComponent.CONF.setSaveToZip(true);
                    break;

                case 'n':
                    JDPluginComponent.CONF.setShowLineNumbersEnabled(true);
                    break;

                case 'r':
                    JDPluginComponent.CONF.setRealignLineNumbersEnabled(false);
                    break;

                default:
                    throw new IllegalArgumentException("Unknown argument: '" + ch + "'");
            }
        }
    }
}
