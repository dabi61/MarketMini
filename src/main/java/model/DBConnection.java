package model;

import java.sql.*;

public class DBConnection {
    //Cuong se update 1 file database vao day -> Thiet ke cac bang tren SqlphpAdmin thoi
//    private static final String URL = "jdbc:mysql://localhost:3306/QLS";  // Ten db cua minh se la MaketMini -> Chu y dat giong ten nhau
//    private static final String USER = "root";
//    private static final String PASS = "";
//
//    public static Connection getConnection() throws SQLException {
//        return DriverManager.getConnection(URL, USER, PASS);
    public static Connection getConnection() throws SQLDataException {
        Connection conn = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/marketmini1";
            String user = "root";
            String password = "";
            conn = DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    } 
}
