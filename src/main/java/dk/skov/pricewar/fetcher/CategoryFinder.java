package dk.skov.pricewar.fetcher;

import dk.skov.pricewar.util.FileHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.TreeMap;

/**
 * Created by aogj on 20-04-2017.
 */
public class CategoryFinder {

    public static void main(String[] args) throws IOException {
        System.out.println("welcome...");
        CategoryFinder categoryFinder = new CategoryFinder();
        TreeMap<String, String> categories = categoryFinder.doSiteMap();
        System.out.println(categories);

        for (String s : categories.keySet()) {
            System.out.println(s + " - " + categories.get(s));
        }

        FileHelper.writeCategoriesToFile(categories);

    }


    public TreeMap<String, String> doSiteMap() throws IOException {

        Document doc = Jsoup.connect("http://www.pricerunner.dk/sm/sitemap").get();

        Elements elements = doc.select("a.sitemap-listing__item");

        TreeMap<String, String> output = new TreeMap<>();
        for (Element e : elements) {
            output.put(e.childNodes().get(0).toString(), e.attr("href"));
        }

        return output;

    }

}
