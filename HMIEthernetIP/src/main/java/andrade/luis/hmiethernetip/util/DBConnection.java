package andrade.luis.hmiethernetip.util;

import andrade.luis.hmiethernetip.models.Tag;

import java.io.*;
import java.sql.*;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.jdbc.ScriptRunner;

public class DBConnection {
    private static final Map<String, String> queries = Map.of("Entero", "select valor from entero where nombreTag=", "Flotante", "select valor from flotante where nombreTag=", "Bool", "select valor from boolean where nombreTag=");
    private final String bdDriverEIPScriptLocation = getClass().getResource("BD_DRIVER_LINUX.sql").toExternalForm().substring(5);
    private static final String BD_DRIVER_EIP_NAME = "bd_driver_eip";

    public static Connection createConnection(String schema) throws SQLException, IOException {
        Properties properties = readPropertiesFile();
        Connection con = null;
        if (!schema.isEmpty()) schema = "/".concat(schema);
        String url = "jdbc:mysql://" + properties.getProperty("hostname") + ":" + properties.getProperty("port") + schema; //MySQL URL and followed by the database name
        String username = properties.getProperty("username"); //MySQL username
        String password = properties.getProperty("password"); //MySQL password

        con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database

        return con;
    }

    public static String getWorkingDirectory() {
        String homeDirectory = System.getProperty("user.home");
        String directoryName = homeDirectory.concat(File.separator+"HMIEthernetIP");
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directoryName;
    }

    public static String getPropertiesFileName() throws IOException {
        return getWorkingDirectory() + File.separator + "HMIEthernetIP.properties";
    }

    public static void writePropertiesFile(String username, String password, String hostname, String port, boolean boilerFurnace, boolean conveyorBelts, boolean motorPumps, boolean others,boolean pipesValues,boolean tanks) throws IOException {
        Properties properties = new Properties();

        properties.setProperty("username", username);
        properties.setProperty("password", password);
        properties.setProperty("hostname", hostname);
        properties.setProperty("port", port);
        properties.setProperty("BoilerFurnace", String.valueOf(boilerFurnace));
        properties.setProperty("ConveyorBelts", String.valueOf(conveyorBelts));
        properties.setProperty("MotorsPumps", String.valueOf(motorPumps));
        properties.setProperty("Others", String.valueOf(others));
        properties.setProperty("PipesValves", String.valueOf(pipesValues));
        properties.setProperty("Tanks", String.valueOf(tanks));

        FileOutputStream out = new FileOutputStream(getPropertiesFileName());
        properties.store(out, "HMIEthernetIP properties file");
        out.close();
    }

    public static Properties readPropertiesFile() throws IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream(getPropertiesFileName());
        properties.load(in);
        in.close();
        return properties;
    }

    public static Connection createConnectionToBDDriverEIP() throws SQLException, IOException {
        return createConnection(BD_DRIVER_EIP_NAME);
    }

    public static Connection createConnectionToHMIUsers() throws SQLException, IOException {
        return createConnection("HMIUsers");
    }

    public static boolean schemaExistsInDB(String schemaName) throws SQLException, IOException {
        Connection con = createConnection("");
        String query = "SELECT schema_name from information_schema.schemata where schema_name = '" + schemaName + "';";
        Statement statement = con.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        boolean res = false;
        if (resultSet.next()) {
            res = (schemaName.equals(resultSet.getString(1)));
        }
        con.close();
        return res;
    }

    public static boolean checkIfTablesFromSchemaBDDriverAreReady() throws SQLException, IOException {
        return !(!tableExistsInSchema("plcs", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("tags", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("intermedia", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("boolean", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("entero", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("flotante", BD_DRIVER_EIP_NAME));
    }

    public static boolean tableExistsInSchema(String tableName, String schemaName) throws SQLException, IOException {
        if (schemaExistsInDB(schemaName)) {
            Connection con = createConnection(schemaName);
            String query = "select table_name from information_schema.tables where table_name='" + tableName + "' and table_schema='" + schemaName + "';";
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            boolean res = false;
            if (resultSet.next()) {
                res = (tableName.equals(resultSet.getString(1)));
            }
            con.close();
            return res;
        } else {
            return false;
        }
    }

    public static boolean createUsersTable() {
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
            if (con != null)
                st = con.createStatement();

            // execute SQL query
            if (st != null)
                result = st.executeUpdate(query);

            // process the result
            if (result == 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String readTagValueFromDatabase(Tag tag) throws SQLException, IOException {

            Connection con = DBConnection.createConnectionToBDDriverEIP();
            Statement statement = con.createStatement();
            if (tag.getType() != null && tag.getName() != null) {
                String query = queries.get(tag.getType()) + "'" + tag.getName() + "'";
                ResultSet resultSet = statement.executeQuery(query);
                while (resultSet.next()) {
                    if (!resultSet.getString("valor").isEmpty()) {
                        String result = resultSet.getString("valor");
                        con.close();
                        return result;
                    }
                }
            }
            con.close();

        return "";
    }

    public static void createSchemaIfNotExists(String schemaName) throws SQLException, IOException {
        if (!schemaExistsInDB(schemaName)) {
            Connection con = createConnection("");
            String query = "CREATE database " + schemaName;
            Statement statement = con.createStatement();
            statement.execute(query);
            con.close();
        }
    }

    public void generateSchemaBDDriverEIP() throws SQLException, IOException {
        createSchemaIfNotExists(BD_DRIVER_EIP_NAME);
        Connection con = createConnectionToBDDriverEIP();
        ScriptRunner scriptRunner = new ScriptRunner(con);
        try {
            Reader reader = new BufferedReader(new FileReader(bdDriverEIPScriptLocation));
            scriptRunner.runScript(reader);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void generateSchemaHMIUsers() throws SQLException, IOException {
        createSchemaIfNotExists("HMIUsers");
        createUsersTable();
    }
}
