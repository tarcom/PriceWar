package dk.skov.pricewar.fetcher;

import dk.skov.pricewar.db.ArchivistMySql;
import dk.skov.pricewar.presenter.HtmlDbStatisticsGenerator;
import dk.skov.pricewar.presenter.HtmlFancyGraphGenerator;
import dk.skov.pricewar.util.FileHelper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.TreeMap;

/**
 * Created by aogj on 20-04-2017.
 */
public class ItemFinder {

    public ArchivistMySql archivist;

    public ItemFinder() throws ClassNotFoundException {
        archivist = new ArchivistMySql();
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("Welcome. Have your wallet ready!");

        LocalDateTime initExecTimeStamp = LocalDateTime.now();

        ItemFinder itemFinder = new ItemFinder();

        TreeMap<String, String> categories = FileHelper.readCategoriesFile();
        FileHelper.printCategories(categories);

        for (String url : categories.values()) {
            //System.out.println(url);
            itemFinder.doFetchData(url, initExecTimeStamp);
        }

        new HtmlFancyGraphGenerator().doGenerate();
        new HtmlDbStatisticsGenerator().doGenerate();

        System.out.println("all done, bye");
    }


    public void doFetchData(String url, LocalDateTime initExecTimeStamp) throws IOException, ClassNotFoundException {

        url = "http://www.pricerunner.dk" + url;

        Document doc = Jsoup.connect(url).get();

        int numOfItems = Integer.parseInt(doc.select("span[class*=category-header__amount]").text().replaceAll("\\(|\\)", ""));
        int maxPages = (numOfItems / 21) + 1;

        System.out.println();
        System.out.print("crawling " + numOfItems + " items on " + maxPages + " (sub)pages starting on this URL=" + url);
        int count = 0;
        for (int i = 1; i <= maxPages; i++) {
            doc = Jsoup.connect(url + "?page=" + i).get();

            //doc.select("a[href]").addClass("structured-grid-product")
//            Elements elements = doc.select("a[class*=structured-list-product]");
            Elements elements = doc.select("a[class*=list-product]");
            if (elements.size() < 21 && i != maxPages) {
                System.out.println();
                System.out.print("[ WARN ] Did not find 21 items on subpage! Only found " + elements.size() + " elements on subpage " + doc.location() + ", continiuing to reach all " + maxPages + " subpages.");
            }
            for (Element e : elements) {
                String item = e.select("h3").text();
                String image = e.select("img").attr("src");
                String info = e.select("div[class*=product-description]").text();
                if (info.length() > 101) info = info.substring(0, 100);
                String category = doc.select("li[itemprop*=itemListElement]").text();
                String itemUrl = e.select("a[class*=structured]").attr("href");
                String itemSubPageUrl = doc.baseUri();
                LocalDateTime insertTimeStamp = LocalDateTime.now();

//                String price = e.select("strong a").text().replaceAll("fra|kr|\\.", "").trim();

//                if (price == null || price.equals("")) {
                String price = e.select("span[class*=price]").text().replaceAll("fra|kr|\\.", "").trim();
                price = price.split(" ")[0];
//  }

                count++;

                try {
                    int priceInt = Integer.parseInt(price);
                    archivist.executeUpdatePreparedStatement(item, String.valueOf(priceInt), category, info, image, itemUrl, itemSubPageUrl, insertTimeStamp, initExecTimeStamp);

                    //Random r = new Random();
                    //priceInt = priceInt - (priceInt / (r.nextInt(8) + 2)+ (priceInt / (r.nextInt(8) + 2)));
                    //insertTimeStamp = insertTimeStamp.minusDays(r.nextInt(10));
                    //archivist.executeUpdatePreparedStatement(item, String.valueOf(priceInt), category, info, image, itemUrl, itemSubPageUrl, insertTimeStamp, initExecTimeStamp);

                } catch (NumberFormatException nfe) {
                    System.out.println("[ ERROR ] cannot parse int:" + price + ", item=" + item + ", itemSubPageUrl=" + itemSubPageUrl);
                }

            }
            System.out.print(".");
        }

        System.out.println();
        if (count != numOfItems) {
            System.out.println();
            System.out.print("[ WARN ] I found " + count + " (suposted to be=" + numOfItems + ") items on " + maxPages + " subpages with this init page URL=" + url);
        }
    }

}
