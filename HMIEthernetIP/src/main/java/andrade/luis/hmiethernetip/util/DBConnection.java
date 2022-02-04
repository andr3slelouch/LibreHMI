package andrade.luis.hmiethernetip.util;

import andrade.luis.hmiethernetip.models.Tag;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;

public class DBConnection {
    private static final Map<String, String> queries = Map.of("Entero", "select valor from ENTERO where nombreTag=", "Flotante", "select valor from FLOTANTE where nombreTag=", "Bool", "select valor from BOOLEAN where nombreTag=");

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

    public static String readTagValueFromDatabase(Tag tag) {
        try {
            Connection con = DBConnection.createConnection();
            Statement statement = con.createStatement();
            if(tag.getTagType() != null && tag.getTagName() != null){
                String query = queries.get(tag.getTagType()) + "'" + tag.getTagName() + "'";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    if(!resultSet.getString("valor").isEmpty()) {
                        return resultSet.getString("valor");
                    }
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
