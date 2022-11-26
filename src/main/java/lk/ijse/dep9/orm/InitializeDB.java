package lk.ijse.dep9.orm;

import lk.ijse.dep9.orm.annotation.Id;
import lk.ijse.dep9.orm.annotation.Table;

import java.io.File;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    private static void createTable(Class<?> classObj, Connection connection) {
        StringBuilder ddlBuilder = new StringBuilder();
        HashMap<Class<?>, String> supportedTypes = new HashMap<>();
        supportedTypes.put(String .class, "VARCHAR(256");
        supportedTypes.put(int.class, "INT");
        supportedTypes.put(Integer.class, "INT");
        supportedTypes.put(double.class, "DOUBLE(10,2)");
        supportedTypes.put(Double.class, "DOUBLE(10,2)");
        supportedTypes.put(BigDecimal.class, "DECIMAL(10,2)");
        supportedTypes.put(Date.class, "DATE");
        supportedTypes.put(Time.class, "TIME");
        supportedTypes.put(Timestamp.class, "DATETIME");

        ddlBuilder.append("CREATE TABLE IF NOT EXISTS `")
                .append(classObj.getSimpleName()).append("`(");
        Field[] fields = classObj.getDeclaredFields();
        for (Field field : fields) {
            String name = field.getName();
            Class<?> dataType = field.getType();
            Id primaryKey = field.getDeclaredAnnotation(Id.class);

            if (!supportedTypes.containsKey(dataType))
                throw new RuntimeException("We don't support " + dataType + " yet." );

            ddlBuilder.append("`").append(name).append("`").append(" ")
                    .append(supportedTypes.get(dataType));

            ddlBuilder = (primaryKey != null) ? ddlBuilder.append(" PRIMARY KEY,"):ddlBuilder.append(",");
        }

        ddlBuilder.deleteCharAt(ddlBuilder.length() - 1).append(")");
        try {
            System.out.println(ddlBuilder);
            connection.createStatement().execute(ddlBuilder.toString());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
