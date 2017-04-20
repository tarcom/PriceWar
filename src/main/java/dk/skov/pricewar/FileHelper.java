package dk.skov.pricewar;

import sun.reflect.generics.tree.Tree;

import java.io.BufferedWriter;
import java.io.IOException;
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

    public static void writeCategoriesToFile(TreeMap<String, String> categories) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get("categoriesToFetch.txt"))) {
            for (String s : categories.keySet()) {
                writer.write(s + " - " + categories.get(s) + "\n");
            }
        }
    }


    public static TreeMap<String, String> readCategoriesFile() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("categoriesToFetch.txt"));

        TreeMap<String, String> categories = new TreeMap<>();

        for (String l : lines) {
            categories.put(l.split(" - ")[0], l.split(" - ")[1]);
        }

        return categories;
    }

    public static void printCategories(TreeMap<String, String> categories) {
        for (String s : categories.keySet()) {
            System.out.println(s + " - " + categories.get(s));
        }

    }

}
