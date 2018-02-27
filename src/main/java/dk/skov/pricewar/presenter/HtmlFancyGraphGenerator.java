package dk.skov.pricewar.presenter;

import dk.skov.pricewar.db.ArchivistMySql;
import dk.skov.pricewar.util.FileHelper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Random;
import java.util.TreeMap;

/**
 * Place description here.
 */

public class HtmlFancyGraphGenerator {

//    private ArchivistSqlite archivistMysql = new ArchivistSqlite();
    private ArchivistMySql archivistMysql = new ArchivistMySql();

    public HtmlFancyGraphGenerator() throws ClassNotFoundException {
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        new HtmlFancyGraphGenerator().doGenerate();
    }


    public void doGenerate() throws IOException, ClassNotFoundException {
        String htmlChartTemplate = FileHelper.readFileStr("htmlTemplates/HtmlFileTemplate.html");
        String chartTemplate = FileHelper.readFileStr("htmlTemplates/ChartTemplate.js");

        String containers = "";
        String charts = "";

        ArrayList<ArrayList<String>> distinctItemList = archivistMysql.executeQuery("select distinct itemUrl, category, item, info from pricewar where price != ''", false);

        int containerCount = 1;
        for (ArrayList<String> item : distinctItemList) {

            String itemUrl = item.get(0);

            ArrayList<ArrayList<String>> itemPrices = archivistMysql.executeQuery("select price, insertTimeStamp from pricewar where itemUrl = '" + itemUrl + "'", false);

            //TreeMap<LocalDateTime, Integer> localDateTimeIntegerTreeMap = generateTestDataSet();
            //String dataSet = dataSetGenerator(localDateTimeIntegerTreeMap);
            String dataSet = dataSetGenerator(itemPrices);

            charts += chartTemplate
                .replaceAll("<!-- CONTAINER_SEARCH_TOKEN -->", "container" + containerCount)
                .replaceAll("<!-- TITLE_SEARCH_TOKEN -->", item.get(1) + ", " + item.get(2) + ", " + item.get(3))
                .replaceAll("<!-- DATA_SET_SEARCH_TOKEN -->", dataSet);


            containers += "<div id=\"container" + containerCount + "\" style=\"width:100%; height:300px;\"></div>\n";

            containerCount++;

            if (containerCount == 100) break; //TODO !!!
        }


        htmlChartTemplate = htmlChartTemplate.replaceAll("<!-- CONTAINER_PLACEHOLDER_SEARCH_TOKEN -->", containers);
        htmlChartTemplate = htmlChartTemplate.replaceAll("<!-- SCRIPT_PLACEHOLDER_SEARCH_TOKEN -->", charts);

        FileHelper.writeToFile(htmlChartTemplate, "htmlOutput/PriceWarFancyGraph.html");

    }

    public String dataSetGenerator(ArrayList<ArrayList<String>> itemPrices) {
        StringBuffer output = new StringBuffer();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu, M, d");
        DateTimeFormatter dateTimeDbParser = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

        for (ArrayList<String> item : itemPrices) {



            LocalDateTime dateTime = LocalDateTime.parse(item.get(1), dateTimeDbParser);

            //[Date.UTC(1971, 5, 3), 0]
            String data = "[Date.UTC(" + dateTime.format(dateTimeFormatter) + "), " + item.get(0) + "],\n";
            output.append(data);
        }

        String s = output.toString();
        s = s.substring(0, s.length() - 2);
        return s;
    }

    public String dataSetGenerator(TreeMap<LocalDateTime, Integer> dataSet) {
        StringBuffer output = new StringBuffer();

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("uuuu, M, d");

        for (LocalDateTime localTimeDate : dataSet.keySet()) {
            //[Date.UTC(1971, 5, 3), 0]
            String data = "[Date.UTC(" + localTimeDate.format(dateTimeFormatter) + "), " + dataSet.get(localTimeDate) + ", 223],\n";
            output.append(data);
        }

        String s = output.toString();
        s = s.substring(0, s.length() - 2);
        return s;
    }

    public TreeMap<LocalDateTime, Integer> generateTestDataSet() {
        TreeMap<LocalDateTime, Integer> dataSet = new TreeMap<>();

        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            dataSet.put(LocalDateTime.now().minusDays(random.nextInt(100)), random.nextInt(100));
        }

        return dataSet;
    }

}
