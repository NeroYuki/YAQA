package com.example.yaqa.helper;

import android.graphics.Bitmap;

import java.io.*;

public class FileHelper {
    public static String readFromInputStream(InputStream is) throws IOException {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return text.toString();
    }

    public static String readFromFile(String filename) throws IOException {
        File file = new File(filename);

        //Read text from file
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return text.toString();
    }

    public static void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;

            System.out.println("Copied file to " + outputPath);

        } catch (FileNotFoundException fnfe1) {
            fnfe1.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean writePNGtoFile(String filepath, Bitmap data) {
        File file = new File(filepath);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            data.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean writeToFile(String filename, String data) throws IOException {
        //TODO: implement
        File file = new File(filename);

        //Read text from file
        StringBuilder text = new StringBuilder(data);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(file));
            String line;

            bw.write(data);
            bw.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        return false;
    }
}
