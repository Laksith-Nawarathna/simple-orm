package lk.ijse.dep9.orm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class InitializeDB {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize(String host,
                                  String port,
                                  String database,
                                  String username,
                                  String password,
                                  String... packagesToScan) throws SQLException {

        String url = "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true";
        url = String.format(url,host, port, database);
        DriverManager.getConnection(url, username, password);
    }

}
