package lk.ijse.dep9.orm;

import lk.ijse.dep9.orm.annotation.Table;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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
                                  String... packagesToScan){

        String url = "jdbc:mysql://%s:%s/%s?createDatabaseIfNotExist=true";
        url = String.format(url,host, port, database);
        Connection connection;
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> classNames = new ArrayList<>();

        for (String packageToScan : packagesToScan) {
            var packageName = packageToScan;
            packageToScan = packageToScan.replaceAll("[.]", "/");
            URL packageUrl = InitializeDB.class.getResource("/" + packageToScan);
            try {
                File file = new File(packageUrl.toURI());
                classNames.addAll(Arrays.asList(file.list()).stream()
                        .map(name -> packageName + "." + name.replace(".class",""))
                        .collect(Collectors.toList()));
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        for (String className : classNames) {
            try {
                Class<?> loadedClass = Class.forName(className);
                Table tableAnnotation = loadedClass.getDeclaredAnnotation(Table.class);
                if(tableAnnotation != null){
                    createTable(loadedClass, connection);
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
