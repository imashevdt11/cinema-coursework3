package Other;
import java.sql.*;
public class MyConnection {
    public static final String USER_NAME = "root";
    public static final String PASSWORD = "";
    public static final String URL = "jdbc:mysql://localhost:3306/coursework_cinema_3";
    public static Statement statement;
    public static Connection connection;
    static {
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException();
        }
    }
    static {
        try {
            statement = connection.createStatement();
        } catch (SQLException exception) {
            exception.printStackTrace();
            throw new RuntimeException();
        }
    }
}