package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static Connection initializeDatabase()
            throws SQLException, ClassNotFoundException
    {
        String dbDriver = "com.mysql.jdbc.Driver";
        String dbURL = "jdbc:mysql:// localhost:3306/";
        String dbName = "employee";
        String dbUsername = "root";
        String dbPassword = "password";

        Class.forName(dbDriver);
        return DriverManager.getConnection(dbURL + dbName,dbUsername, dbPassword);
    }
}
