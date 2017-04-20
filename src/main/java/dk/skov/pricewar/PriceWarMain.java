package dk.skov.pricewar;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by aogj on 19-04-2017.
 */
public class PriceWarMain {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Welcome. Have your wallet ready!");

        TreeMap<String, String> categories = null;

        //categories = new CategoryFinder().doSiteMap();

        categories = FileHelper.readCategoriesFile();
        FileHelper.printCategories(categories);

        for (String url : categories.values()) {
            System.out.println(url);
            new ItemFinder().doFetchData(url);
        }

        System.out.println("all done, bye");

//        new ItemFinder().doFetchData("/cl/1396/3D-briller");
        //new ItemFinder().doFetchData("/cl/1426/3D-Printere");

//        new Archivist().printDb();
    }

}
