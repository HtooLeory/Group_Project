package edu.cs;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBUtil {

    private static final String PROPERTIES_FILE = "db.properties";

    public static Connection getConnection() throws Exception {

        Properties props = new Properties();

        try (InputStream input =
                 DBUtil.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE)) {

            if (input == null) {
                throw new Exception("db.properties file not found in classpath.");
            }

            props.load(input);
        }

        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");

        if (dbUrl == null || dbUser == null || dbPassword == null) {
            throw new Exception("Missing database configuration in db.properties.");
        }

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}