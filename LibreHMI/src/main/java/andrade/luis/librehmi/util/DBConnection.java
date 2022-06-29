package andrade.luis.librehmi.util;

import java.io.*;
import java.sql.*;
import java.util.Properties;

import andrade.luis.librehmi.models.users.HMIUser;
import org.apache.ibatis.jdbc.ScriptRunner;

/**
 * Clase que contiene los métodos estáticos requeridos para conectarse a la base de datos
 * de forma general por la aplicación
 */
public class DBConnection {
    public static final String ADMIN = "admin";
    private final String bdDriverEIPScriptLocation = getClass().getResource("BD_DRIVER_LINUX.sql").toExternalForm().substring(5);
    private static final String BD_DRIVER_EIP_NAME = "bd_driver_eip";

    /**
     * Permite crear una conexión a un esquema de la base de datos
     * @param schema Nombre del esquema
     * @return Conexión al esquema de base de datos
     * @throws SQLException
     * @throws IOException
     */
    public static Connection createConnection(String schema) throws SQLException, IOException {
        Properties properties = readPropertiesFile();
        Connection con;

        if (!schema.isEmpty()) schema = "/".concat(schema);
        String url = "jdbc:mysql://" + properties.getProperty("hostname") + ":" + properties.getProperty("port") + schema; //MySQL URL and followed by the database name
        String username = properties.getProperty("username"); //MySQL username
        String password = properties.getProperty("password"); //MySQL password

        con = DriverManager.getConnection(url, username, password); //attempting to connect to MySQL database

        return con;
    }

    /**
     * Permite obtener el directorio de trabajo donde la aplicación guardará y leerá los archivos necesarios
     * para su funcionamiento
     * @return El path del directorio de trabajo
     */
    public static String getWorkingDirectory() {
        String homeDirectory = System.getProperty("user.home");
        String directoryName = homeDirectory.concat(File.separator + "LibreHMI");
        File directory = new File(directoryName);
        if (!directory.exists()) {
            directory.mkdir();
        }
        return directoryName;
    }

    /**
     * Permite obtener el path con el nombre del archivo de propiedades de la aplicación
     * @return El path del archivo de propiedades
     */
    public static String getPropertiesFileName() {
        return getWorkingDirectory() + File.separator + "LibreHMI.properties";
    }

    /**
     * Permite escribir el archivo de propiedades
     * @param username Nombre de usuario para la conexión a la base de datos
     * @param password Contraseña del usuario para la conexión a la base de datos
     * @param hostname Dirección IP de la base de datos
     * @param port Puerto de la base de datos
     * @param properties Objeto de propiedades
     * @throws IOException
     */
    public static void writePropertiesFile(String username, String password, String hostname, String port, Properties properties) throws IOException {
        properties.setProperty("username", username);
        properties.setProperty("password", password);
        properties.setProperty("hostname", hostname);
        properties.setProperty("port", port);

        try (FileOutputStream out = new FileOutputStream(getPropertiesFileName())) {
            properties.store(out, "LibreHMI setProperties file");
        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    /**
     * Permite añadir las propiedades que permiten verificar si las categorías se han habilitado
     * @param boilerFurnace Categoría de Hornos, calderas, etc
     * @param conveyorBelts Categoría de Cintas transportadoras
     * @param motorPumps Categoría de Motores y bombas
     * @param others Categoría de Otros
     * @param pipesValues Categoría de Tuberías
     * @param tanks Categoría de Tanques
     * @param properties Objeto de propiedades
     * @return Objeto de propiedades con las categorías agregadas
     */
    public static Properties prepareCategoriesProperties(boolean boilerFurnace, boolean conveyorBelts, boolean motorPumps, boolean others, boolean pipesValues, boolean tanks, Properties properties) {
        properties.setProperty("BoilerFurnace", String.valueOf(boilerFurnace));
        properties.setProperty("ConveyorBelts", String.valueOf(conveyorBelts));
        properties.setProperty("MotorsPumps", String.valueOf(motorPumps));
        properties.setProperty("Others", String.valueOf(others));
        properties.setProperty("PipesValves", String.valueOf(pipesValues));
        properties.setProperty("Tanks", String.valueOf(tanks));
        return properties;
    }

    /**
     * Permite leer el archivo de propiedades
     * @return Objeto de propiedades de la aplicación
     * @throws IOException
     */
    public static Properties readPropertiesFile() throws IOException {
        Properties properties = new Properties();
        try (FileInputStream in = new FileInputStream(getPropertiesFileName())) {
            properties.load(in);
            return properties;
        }
    }

    /**
     * Permite crear una conexión hacia el esquema BD_DRIVER_EIP
     * @return Conexión hacia el esquema requerido
     * @throws SQLException
     * @throws IOException
     */
    public static Connection createConnectionToBDDriverEIP() throws SQLException, IOException {
        return createConnection(BD_DRIVER_EIP_NAME);
    }

    /**
     * Permite crear una conexión hacia el esquema de usuarios
     * @return Conexión hacia el esquema requerido
     * @throws SQLException
     * @throws IOException
     */
    public static Connection createConnectionToHMIUsers() throws SQLException, IOException {
        return createConnection("HMIUsers");
    }

    /**
     * Permite verificar si un esquema requerido existe en la base de datosw
     * @param schemaName Nombre del esquema a verificarse
     * @return true si el esquema requerido existe
     * @throws SQLException
     * @throws IOException
     */
    public static boolean schemaExistsInDB(String schemaName) throws SQLException, IOException {
        try (Connection con = createConnection("")) {
            String query = "SELECT schema_name from information_schema.schemata where schema_name = ?";
            try(PreparedStatement preparedStatement = con.prepareStatement(query)){
                preparedStatement.setString(1,schemaName);
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean res = false;
                if (resultSet.next()) {
                    res = (schemaName.equals(resultSet.getString(1)));
                }
                return res;
            }
        } catch (SQLException e) {
            throw new SQLException(e);
        } catch (IOException e) {
            throw new IOException(e);
        }

    }

    /**
     * Permite verificar si las tablas esperadas de los esquemas existen
     * @return true si las tablas esperadas existen
     * @throws SQLException
     * @throws IOException
     */
    public static boolean checkIfTablesFromSchemaBDDriverAreReady() throws SQLException, IOException {
        return !(!tableExistsInSchema("plcs", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("tags", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("intermedia", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("boolean", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("entero", BD_DRIVER_EIP_NAME) || !tableExistsInSchema("flotante", BD_DRIVER_EIP_NAME));
    }

    /**
     * Permite verificar si las tablas de un esquema existen
     * @param tableName Nombre de la tabla a verificar
     * @param schemaName Nombre del esquema a verificar
     * @return true si existe la tabla dentro del esquema
     * @throws SQLException
     * @throws IOException
     */
    public static boolean tableExistsInSchema(String tableName, String schemaName) throws SQLException, IOException {
        if (schemaExistsInDB(schemaName)) {
            Connection con = createConnection(schemaName);
            String query = "select table_name from information_schema.tables where table_name=? and table_schema=?";
            try(PreparedStatement preparedStatement = con.prepareStatement(query)){
                preparedStatement.setString(1,tableName);
                preparedStatement.setString(2,schemaName);
                ResultSet resultSet = preparedStatement.executeQuery();
                boolean res = false;
                if (resultSet.next()) {
                    res = (tableName.equals(resultSet.getString(1)));
                }
                con.close();
                return res;
            } catch (SQLException e) {
                throw new SQLException(e);
            }
        } else {
            return false;
        }
    }

    /**
     * Permite crear la tabla de usuarios en el esquema HMIUsers
     * @throws SQLException
     * @throws IOException
     */
    public static void createUsersTable() throws SQLException, IOException {
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

        // establish the connection
        try (Connection con = createConnectionToHMIUsers()) {

            // place the password for the root user
            // create statement object

            try(Statement st = con.createStatement()){
                // execute SQL query
                if (st != null)
                    st.executeUpdate(query);

                // process the result
            }

        } catch (SQLException e) {
            throw new SQLException(e);
        } catch(IOException e){
            throw new IOException(e);
        }
    }

    /**
     * Permite crear un esquema requerido si este no existe
     * @param schemaName Esquema a crearse si no existe
     * @throws SQLException
     * @throws IOException
     */
    public static void createSchemaIfNotExists(String schemaName) throws SQLException, IOException {
        if (!schemaExistsInDB(schemaName)) {
            try(Connection con = createConnection("")){
                String query = "CREATE database ?";
                try(PreparedStatement preparedStatement = con.prepareStatement(query)){
                    preparedStatement.setString(1,schemaName);
                    preparedStatement.executeQuery();
                }
                try(Statement statement = con.createStatement()){
                    statement.execute(query);
                }
            }catch (SQLException e) {
                throw new SQLException(e);
            } catch(IOException e){
                throw new IOException(e);
            }
        }
    }

    /**
     * Permite generar el esquema bd_driver_eip
     * @throws SQLException
     * @throws IOException
     */
    public void generateSchemaBDDriverEIP() throws SQLException, IOException {
        createSchemaIfNotExists(BD_DRIVER_EIP_NAME);
        try(Connection con = createConnectionToBDDriverEIP()){
            runScript(con);
        }catch (SQLException e) {
            throw new SQLException(e);
        }catch (IOException e){
            throw new IOException(e);
        }

    }

    /**
     * Permite ejecutar un script sql
     * @param con Conexión hacia la base de datos
     * @throws IOException
     */
    public void runScript(Connection con) throws IOException {
        ScriptRunner scriptRunner = new ScriptRunner(con);
        try (Reader reader = new BufferedReader(new FileReader(bdDriverEIPScriptLocation))){
            scriptRunner.runScript(reader);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Permite generar el esquema HMIUsers
     * @throws SQLException
     * @throws IOException
     */
    public static void generateSchemaHMIUsers() throws SQLException, IOException {
        createSchemaIfNotExists("HMIUsers");
        createUsersTable();
        HMIUser admin = new HMIUser(ADMIN,ADMIN,"admin@librehmi",ADMIN,"Administrador","12345");
        admin.createInDatabase();
    }
}
