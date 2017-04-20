package dk.skov.pricewar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by aogj on 20-04-2017.
 */
public class ItemFinder {

    public Archivist archivist;

    public ItemFinder() throws ClassNotFoundException {
        archivist = new Archivist(true);
    }

    public void doFetchData(String url) throws IOException, ClassNotFoundException {

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
                String price = e.select("strong a").text().replaceAll("fra|kr|\\.", "").trim();
                String image = e.select("div div a img[src$=.png]").attr("src");
                String info = e.select("p.productdescription").text().replaceAll("Mere Info", "").trim();
                String category = doc.select("div #breadcrumbs").text().replaceAll("PriceRunner >", "").trim();

                archivist.executeUpdatePreparedStatement(item , price, category, info, image);
                count++;
            }
            System.out.print(".");
        }


        System.out.println("I found " + count + " items on the page " + url);
    }

}
