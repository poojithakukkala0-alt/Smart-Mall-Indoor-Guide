import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = "jdbc:mysql://localhost:3306/mall_db"; // your DB
                String username = "root"; // DB username
                String password = "root"; // DB password

                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(url, username, password);
                System.out.println("Database connected successfully!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}

