import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        // Oracle XE standard connection URL
        String url = "jdbc:oracle:thin:@localhost:1521:xe";
        String user = "u24051061"; // Change this if your username is different
        String password = "u24051061"; // Put your actual password here

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("SUCCESS! Java is connected to your Oracle Database.");
            conn.close();
        } catch (Exception e) {
            System.out.println("CONNECTION FAILED! Here is the error:");
            System.out.println(e.getMessage());
        }
    }
}
