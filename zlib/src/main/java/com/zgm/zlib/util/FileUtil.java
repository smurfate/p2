package com.zgm.zlib.util;


import java.io.*;

/**
 * File utility methods.
 */
public class FileUtil {

    /**
     * Reads file into a string using default encoding.
     */
    public static String toString(File file) throws IOException {
        char[] buffer = new char[0x1000]; // 4k
        int read;
        Reader in = new FileReader(file);
        StringBuilder builder = new StringBuilder();
        while ((read = in.read(buffer)) > -1) {
            builder.append(buffer, 0, read);
        }
        in.close();
        return builder.toString();
    }


    /**
     * Writes a string to a file using default encoding.
     */
    public static void toFile(String contents, File file) throws IOException {
        FileWriter out = new FileWriter(file);
        out.write(contents);
        out.close();
    }

    /**
     * Save text to a file
     * @param contents the data to save
     * @param path where to save the file, in the form /sdcard/test.txt
     * @throws IOException
     */
    public static void toFile(String contents, String path) throws IOException {

        FileWriter out = new FileWriter(path);
        out.write(contents);
        out.close();
    }

    /**
     * The main different is the that it creates the path if not found
     * @param contents the data to save
     * @param path where to save the file, in the form /sdcard/directory/
     * @param name the name of the file, in the form test.txt
     * @throws IOException
     */
    public static void toFile(String contents, String path,String name) throws IOException {
        final File file = new File(path,name);
        File tmp = new File(path);
        if(!file.exists())
            tmp.mkdir();

        FileWriter out = new FileWriter(path+name);
        out.write(contents);
        out.close();
    }

    static void toFile(byte[] contents, File file) throws IOException {
        OutputStream out = new FileOutputStream(file);
        out.write(contents);
        out.close();
    }

    public static void toFile(byte[] contents, String path) throws IOException {
        OutputStream out = new FileOutputStream(path);
        out.write(contents);
        out.close();
    }
    public static void toFile(byte[] contents, String path,String name) throws IOException {
        File file = new File(path,name);
        File tmp = new File(path);
        if(!file.exists())
            tmp.mkdir();

        OutputStream out = new FileOutputStream(file);
        out.write(contents);
        out.close();
    }


}