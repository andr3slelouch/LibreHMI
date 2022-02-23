package andrade.luis.hmiethernetip.util;

import andrade.luis.hmiethernetip.models.Tag;

import java.io.*;
import java.sql.*;
import java.util.Map;
import java.util.Objects;

import javafx.scene.image.Image;
import org.apache.ibatis.jdbc.ScriptRunner;

public class DBConnection {
    private static final Map<String, String> queries = Map.of("Entero", "select valor from entero where nombreTag=", "Flotante", "select valor from flotante where nombreTag=", "Bool", "select valor from boolean where nombreTag=");
    private final String bdDriverEIPScriptLocation = getClass().getResource("BD_DRIVER_LINUX.sql").toExternalForm().substring(5);

    public static Connection createConnection(String schema) throws SQLException {
        Connection con = null;
        if(!schema.isEmpty()) schema = "/".concat(schema);
        String url = "jdbc:mysql://localhost:3306"+schema; //MySQL URL and followed by the database name
        String username = "root"; //MySQL username
        String password = "12345"; //MySQL password

            con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database

        return con;
    }

    public static Connection createConnectionToBDDriverEIP() throws SQLException {
        return createConnection("bd_driver_eip");
    }

    public static Connection createConnectionToHMIUsers() throws SQLException {
        return createConnection("HMIUsers");
    }

    public static boolean schemaExistsInDB(String schemaName) throws SQLException {
        Connection con = createConnection("");
        String query = "SELECT schema_name from information_schema.schemata where schema_name = '"+schemaName+"';";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        boolean res = false;
        if(resultSet.next()){
            res = (schemaName.equals(resultSet.getString(1)));
        }
        con.close();
        return res;
    }

    public static boolean checkIfTablesFromSchemaBDDriverAreReady() throws SQLException {
        return !(!tableExistsInSchema("plcs","bd_driver_eip") || !tableExistsInSchema("tags","bd_driver_eip") || !tableExistsInSchema("intermedia","bd_driver_eip") || !tableExistsInSchema("boolean","bd_driver_eip") || !tableExistsInSchema("entero","bd_driver_eip") || !tableExistsInSchema("flotante","bd_driver_eip"));
    }

    public static boolean tableExistsInSchema(String tableName, String schemaName) throws SQLException {
        if(schemaExistsInDB(schemaName)){
            Connection con = createConnection(schemaName);
            String query = "select table_name from information_schema.tables where table_name='"+tableName+"' and table_schema='"+schemaName+"';";
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            boolean res = false;
            if(resultSet.next()){
                res = (tableName.equals(resultSet.getString(1)));
            }
            con.close();
            return res;
        }else{
            return false;
        }
    }

    public static boolean createUsersTable(){
        // prepare query to create database
        String query = "CREATE TABLE Users " +
                "(id INTEGER not NULL AUTO_INCREMENT, " +
                " first VARCHAR(255) NOT NULL, " +
                " last VARCHAR(255) NOT NULL, " +
                " email VARCHAR(255) NOT NULL UNIQUE, " +
                " username VARCHAR(255) NOT NULL UNIQUE, " +
                " role VARCHAR(255) NOT NULL, " +
                " salt VARCHAR(255) NOT NULL, " +
                " saltedHashPassword VARCHAR(255) NOT NULL, " +
                " PRIMARY KEY ( id ))";
        Statement st = null;
        int result = 0;

        // establish the connection
        Connection con = null;
        try {
            con = createConnectionToHMIUsers();
            // place the password for the root user
            // create statement object
            if(con != null)
                st = con.createStatement();

            // execute SQL query
            if(st != null)
                result = st.executeUpdate(query);

            // process the result
            if(result == 0){
                return true;
            } else{
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readTagValueFromDatabase(Tag tag) {
        try {
            Connection con = DBConnection.createConnectionToBDDriverEIP();
            Statement statement = con.createStatement();
            if(tag.getTagType() != null && tag.getTagName() != null){
                String query = queries.get(tag.getTagType()) + "'" + tag.getTagName() + "'";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    if(!resultSet.getString("valor").isEmpty()) {
                        String result = resultSet.getString("valor");
                        con.close();
                        return result;
                    }
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void createSchemaIfNotExists(String schemaName) throws SQLException {
        if(!schemaExistsInDB(schemaName)){
            Connection con = createConnection("");
            String query="CREATE database "+schemaName;
            Statement statement = con.createStatement();
            statement.execute(query);
            con.close();
        }
    }

    public void generateSchemaBDDriverEIP() throws SQLException {
        createSchemaIfNotExists("bd_driver_eip");
        Connection con = createConnectionToBDDriverEIP();
        ScriptRunner scriptRunner = new ScriptRunner(con);
        try {
            Reader reader = new BufferedReader(new FileReader(bdDriverEIPScriptLocation));
            scriptRunner.runScript(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void generateSchemaHMIUsers() throws SQLException {
        createSchemaIfNotExists("HMIUsers");
        createUsersTable();
    }
}
