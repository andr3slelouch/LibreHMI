package andrade.luis.hmiethernetip.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    public static Connection createConnection()
    {
        Connection con = null;
        String url = "jdbc:mysql://localhost:3306/bd_driver_eip"; //MySQL URL and followed by the database name
        String username = "root"; //MySQL username
        String password = "kakaroto"; //MySQL password
        try
        {
            con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return con;
    }
}
