package dk.skov.pricewar.presenter;

import dk.skov.pricewar.db.ArchivistMySql;
import dk.skov.pricewar.util.FileHelper;

import java.util.ArrayList;

/**
 * Created by aogj on 27-02-2018.
 */
public class HtmlDbStatisticsGenerator {

    private ArchivistMySql archivist = new ArchivistMySql();

    public static void main(String[] args) {
        new HtmlDbStatisticsGenerator().doGenerate();
    }

    public void doGenerate() {

        String sql = "SELECT category, item, info, price, AVG(price), MIN(price), MAX(price), STDDEV(price), COUNT(*), itemUrl, itemSubPageUrl \n" +
                "FROM `pricewar`\n" +
                "WHERE 1\n" +
                "group by category, item, info, price\n" +
                "order by category, item, info, price desc";

        ArrayList<ArrayList<String>> out = archivist.executeQuery(sql, true);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body>\n");
        sb.append("<h1>item statistics</h1>\n");

        sb.append("<table border=1 cellpadding=0 cellspacing=0 style='border-collapse:collapse;'>\n");

        for (int i = 0; i < out.size(); i++) {
            sb.append("<tr>");
            for (int j = 0; j < out.get(i).size() - 2; j++) {
                sb.append("<td>" + out.get(i).get(j) + "</td>");
            }
            sb.append("<td><a href='http://www.pricerunner.dk" + out.get(i).get(out.get(i).size() - 2) + "'>link1</a></td>");
            sb.append("<td><a href='" + out.get(i).get(out.get(i).size() - 1) + "'>link2</a></td>");
            sb.append("</tr>\n");
        }

        sb.append("</table>\n");


        sb.append("\n");
        sb.append("I found " + (out.size() - 1) + " rows in my db.<br>\n");
        sb.append("sql=" + sql + "<br>\n");
        sb.append("</body></html><br>\n");

        System.out.println(sb.toString());

        FileHelper.writeToFile(sb.toString(), "htmlOutput/ItemStatistics.html");

    }

}
