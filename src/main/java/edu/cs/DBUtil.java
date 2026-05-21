package edu.cs;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBUtil {

    private static final String DB_URL =
        "jdbc:mysql://localhost:3306/qc_lost_found?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    	
    private static final String DB_USER = "root";

    private static final String DB_PASSWORD = "Heatman@123";

    //"jdbc:mysql://qc-lost-found.c8bw88u8aofa.us-east-1.rds.amazonaws.com:3306/qc_lost_found?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
   // private static final String DB_USER = "admin";

   // private static final String DB_PASSWORD = "Admin123admin";

    public static Connection getConnection() throws Exception {

        Class.forName("com.mysql.cj.jdbc.Driver");

        return DriverManager.getConnection(
            DB_URL,
            DB_USER,
            DB_PASSWORD
        );
    }
}