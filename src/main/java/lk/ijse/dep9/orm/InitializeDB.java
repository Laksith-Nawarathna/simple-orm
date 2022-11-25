package lk.ijse.dep9.orm;

import java.sql.Connection;
import java.sql.SQLException;

public class InitializeDB {

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize(String database) throws SQLException {




    }

}
