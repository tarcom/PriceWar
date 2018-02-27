package dk.skov.pricewar.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by aogj on 27-02-2018.
 */
public class ArchivistMySql {

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArchivistMySql() {
    }

    private static Connection getConnection() {
        Connection con = null;
        try {
            String url = "jdbc:mysql://localhost:3306/pricewar";
            String username = "root";
            String passwd = "";
            con = DriverManager.getConnection(url, username, passwd);
        } catch (Exception e) {
            System.out.println("FATAL. Cannot get a db connection!" + e);
        }
        return con;
    }

    public static void main(String[] args) {
        ArchivistMySql archivistMySql = new ArchivistMySql();

        //archivist.executeUpdatePreparedStatement("super dims", "6000", "cat1 -> cat2 -> cat3", "info info info is fino", "fancy_image_url");

        archivistMySql.printDb("select price, item, info from pricewar order by price desc");

    }

    public void printDb(String sql) {
        ArrayList<ArrayList<String>> out = executeQuery(sql);

        for (int i = 1; i < out.size(); i++) {
            for (int j = 0; j < out.get(i).size(); j++) {
                System.out.print(out.get(0).get(j) + ": " + out.get(i).get(j) + ", ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("I found " + (out.size() - 1) + " rows in my db.");

    }

    public void executeUpdatePreparedStatement(String item, String price, String category, String info, String image, String itemUrl, String itemSubPageUrl, LocalDateTime insertTimeStamp, LocalDateTime initExecTimeStamp) throws ClassNotFoundException {


        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try {

            connection = getConnection();

            preparedStatement = connection.prepareStatement("insert into pricewar values(?, ?, ?, ?, ?, ?, ?, ?, ? )");
            preparedStatement.setQueryTimeout(3);
            preparedStatement.setString(1, item);
            preparedStatement.setString(2, price);
            preparedStatement.setString(3, category);
            preparedStatement.setString(4, info);
            preparedStatement.setString(5, image);
            preparedStatement.setString(6, itemUrl);
            preparedStatement.setString(7, itemSubPageUrl);
            preparedStatement.setString(8, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(insertTimeStamp));
            preparedStatement.setString(9, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(initExecTimeStamp));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (!preparedStatement.isClosed()) {
                    preparedStatement.close();
                }
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public ArrayList<ArrayList<String>> executeQuery(String sql) {
        return executeQuery(sql, true);
    }

    public ArrayList<ArrayList<String>> executeQuery(String sql, boolean includeColumnNames) {

        ArrayList<ArrayList<String>> output = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.setQueryTimeout(30);

            ResultSet rs = statement.executeQuery(sql);

            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            ArrayList<String> dbRow = new ArrayList<>();
            if (includeColumnNames) {
                for (int i = 1; i <= columnCount; i++) {
                    dbRow.add(metadata.getColumnName(i));
                }
                output.add(dbRow);
            }

            while (rs.next()) {
                dbRow = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    dbRow.add(rs.getString(i));
                }
                output.add(dbRow);

            }

            return output;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        } finally {
            try {
                if (!statement.isClosed()) {
                    statement.close();
                }
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println(e);
                return null;
            }
        }


    }


}
