package dk.skov.pricewar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.TreeMap;

/**
 * Created by aogj on 20-04-2017.
 */
public class ItemFinder {

    public Archivist archivist;

    public ItemFinder() throws ClassNotFoundException {
        archivist = new Archivist(false);
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Welcome. Have your wallet ready!");

        LocalDateTime initExecTimeStamp = LocalDateTime.now();

        ItemFinder itemFinder = new ItemFinder();

        TreeMap<String, String> categories = FileHelper.readCategoriesFile();
        FileHelper.printCategories(categories);

        for (String url : categories.values()) {
            System.out.println(url);
            itemFinder.doFetchData(url, initExecTimeStamp);
        }

        new HtmlGenerator().doGenerate();

        System.out.println("all done, bye");
    }


    public void doFetchData(String url, LocalDateTime initExecTimeStamp) throws IOException, ClassNotFoundException {

        url = "http://www.pricerunner.dk" + url + "?numberOfProducts=60";

        Document doc = Jsoup.connect(url).get();

        int maxPages = Integer.parseInt(doc.select("div.paginator p").text().replaceAll("Total antal sider:", "").trim());

        System.out.print("crawling " + maxPages + " pages");
        int count = 0;
        for (int i = 1; i <= maxPages; i++) {
            doc = Jsoup.connect(url + "&page=" + i).get();

            Elements elements = doc.select("div.product");
            for (Element e : elements) {
                String item = e.select("h3 a").text();
                String image = e.select("div div a img[src$=.png]").attr("src");
                String info = e.select("p.productdescription").text().replaceAll("Mere Info", "").trim();
                String category = doc.select("div #breadcrumbs").text().replaceAll("PriceRunner >", "").trim();
                String itemUrl = e.select("h3 a").attr("href");
                String itemSubPageUrl = doc.baseUri();
                LocalDateTime insertTimeStamp = LocalDateTime.now();

                String price = e.select("strong a").text().replaceAll("fra|kr|\\.", "").trim();

                if (price == null || price.equals("")) {
                    price = e.getElementsByClass("price-rang").text().replaceAll("fra|kr|\\.", "").trim();
                }

                count++;

                try {
                    int priceInt = Integer.parseInt(price);
                    archivist.executeUpdatePreparedStatement(item, String.valueOf(priceInt), category, info, image, itemUrl, itemSubPageUrl, insertTimeStamp, initExecTimeStamp);

                    //Random r = new Random();
                    //priceInt = priceInt - (priceInt / (r.nextInt(8) + 2)+ (priceInt / (r.nextInt(8) + 2)));
                    //insertTimeStamp = insertTimeStamp.minusDays(r.nextInt(10));
                    //archivist.executeUpdatePreparedStatement(item, String.valueOf(priceInt), category, info, image, itemUrl, itemSubPageUrl, insertTimeStamp, initExecTimeStamp);

                } catch (NumberFormatException nfe) {
                    System.out.println("cannot parse int:" + price + ", item=" + item + ", itemSubPageUrl=" + itemSubPageUrl);
                }

            }
            System.out.print(".");
        }

        System.out.println("I found " + count + " items on the page " + url);
    }

}
