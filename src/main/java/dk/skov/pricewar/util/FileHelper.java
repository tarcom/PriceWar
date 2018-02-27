package dk.skov.pricewar.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by aogj on 20-04-2017.
 */
public class FileHelper {

    public static void main(String[] args) throws IOException {
        printCategories(readCategoriesFile());
    }


    public static void writeToFile(String text, String fileName) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileName), StandardCharsets.UTF_16)) {
            writer.write(text);
            System.out.println("Wrote file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeCategoriesToFile(TreeMap<String, String> categories) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("categoriesToFetch.txt"))) {
            for (String s : categories.keySet()) {
                writer.write(s + " - " + categories.get(s) + "\n");
            }
        }
    }


    public static TreeMap<String, String> readCategoriesFile() throws IOException {
        List<String> lines = readFile("categoriesToFetch.txt");

        TreeMap<String, String> categories = new TreeMap<>();

        for (String l : lines) {
            categories.put(l.split(" - ")[0], l.split(" - ")[1]);
        }

        return categories;
    }

    public static String readFileStr(String fileName) throws IOException {

        List<String> fileList = readFile(fileName);

        StringBuffer output = new StringBuffer();
        for (String s : fileList) {
            output.append(s + "\n");
        }

        return output.toString();
    }


    public static List<String> readFile(String fileName) throws IOException {
        return Files.readAllLines(Paths.get(fileName));
    }

    public static void printCategories(TreeMap<String, String> categories) {
        for (String s : categories.keySet()) {
            System.out.println(s + " - " + categories.get(s));
        }

    }

}
