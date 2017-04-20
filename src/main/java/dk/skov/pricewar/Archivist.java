package dk.skov.pricewar;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by aogj on 19-04-2017.
 */
public class Archivist {

    public Archivist() throws ClassNotFoundException {
        this(false);
    }

    public Archivist(boolean flushDBOnInit) throws ClassNotFoundException {
        if (flushDBOnInit) {
            executeUpdateOld("drop table if exists pricewar");
        }
        executeUpdateOld("create table if not exists pricewar (item string, price string, category string, info string, image string, LastModifiedTime timestamp)");
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Archivist archivist = new Archivist(false);

        //archivist.executeUpdatePreparedStatement("super dims", "6000", "cat1 -> cat2 -> cat3", "info info info is fino", "fancy_image_url");

        archivist.printDb();
    }

    public void printDb() throws ClassNotFoundException {
        ArrayList<ArrayList<String>> out = executeQuery("select price, item, info from pricewar where item like '%worx%' order by price desc");

        for (int i = 1; i < out.size(); i++) {
            for (int j = 0; j < out.get(i).size(); j++) {
                System.out.print(out.get(0).get(j) + ": " + out.get(i).get(j) + ", ");
            }
            System.out.println("");
        }
        System.out.println("");
        System.out.println("I found " + (out.size() - 1) + " rows in my db.");

    }

    public void executeUpdatePreparedStatement(String item, String price, String category, String info, String image) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:pricewar.db");

            PreparedStatement preparedStatement = connection.prepareStatement("insert into pricewar values(?, ?, ?, ?, ?, CURRENT_TIMESTAMP )");
            preparedStatement.setQueryTimeout(3);
            preparedStatement.setString(1, item);
            preparedStatement.setString(2, price);
            preparedStatement.setString(3, category);
            preparedStatement.setString(4, info);
            preparedStatement.setString(5, image);


            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public void executeUpdateOld(String sql) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:pricewar.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);

            statement.executeUpdate(sql);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e);
            }
        }
    }

    public ArrayList<ArrayList<String>> executeQuery(String sql) throws ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");

        ArrayList<ArrayList<String>> output = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:pricewar.db");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(3);

            ResultSet rs = statement.executeQuery(sql);

            ResultSetMetaData metadata = rs.getMetaData();
            int columnCount = metadata.getColumnCount();
            ArrayList<String> dbRow = new ArrayList<>();
            for (int i = 1; i <= columnCount; i++) {
                dbRow.add(metadata.getColumnName(i));
            }
            output.add(dbRow);

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
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e);
                return null;
            }
        }


    }

}