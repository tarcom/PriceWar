package dk.skov.pricewar;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.TreeMap;

/**
 * Place description here.
 */

public class HtmlGenerator {


    public static void main(String[] args) throws IOException {
        new HtmlGenerator().doGenerate();
    }


    public void doGenerate() throws IOException {
        String htmlChartTemplate = FileHelper.readFileStr("htmlTemplates/HtmlFileTemplate.html");
        String chartTemplate = FileHelper.readFileStr("htmlTemplates/ChartTemplate.js");

        String containers = "";
        String charts = "";

        for (int i = 0; i < 1; i++) {
            containers += "<div id=\"container" + i + "\" style=\"width:100%; height:300px;\"></div>";

            TreeMap<LocalDateTime, Integer> localDateTimeIntegerTreeMap = generateTestDataSet();
            String dataSet = dataSetGenerator(localDateTimeIntegerTreeMap);

            charts += chartTemplate
                .replaceAll("<!-- CONTAINER_SEARCH_TOKEN -->", "container" + i)
                .replaceAll("<!-- TITLE_SEARCH_TOKEN -->", "title" + i)
                .replaceAll("<!-- DATA_SET_SEARCH_TOKEN -->", dataSet);
        }

        htmlChartTemplate = htmlChartTemplate.replaceAll("<!-- CONTAINER_PLACEHOLDER_SEARCH_TOKEN -->", containers);
        htmlChartTemplate = htmlChartTemplate.replaceAll("<!-- SCRIPT_PLACEHOLDER_SEARCH_TOKEN -->", charts);

        FileHelper.writeToFile(htmlChartTemplate, "htmlOutput/PriceWar.html");

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
