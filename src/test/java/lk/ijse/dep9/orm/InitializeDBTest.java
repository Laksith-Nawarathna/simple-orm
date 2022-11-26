package lk.ijse.dep9.orm;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.*;

class InitializeDBTest {

    @BeforeAll
    static void beforeAll() throws ClassNotFoundException {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    @Test
    void initialize() {
        assertDoesNotThrow(() -> {
            InitializeDB.initialize("localhost",
                    "3306",
                    "simple-orm",
                    "root",
                    "3/0tril2NB",);
        });
        assertDoesNotThrow(() -> {
            DriverManager.
                    getConnection("jdbc:mysql://localhost:3306/simple_orm", "root", "3/0tril2NB")
                    .close();
        });
    }
}