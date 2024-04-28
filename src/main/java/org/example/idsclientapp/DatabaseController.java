package org.example.idsclientapp;

import java.sql.*;

/**
 * @brief Class responsible for handling database operations.
 */
public final class DatabaseController
{
    /**
     * @brief Flag indicating whether the JDBC driver is loaded.
     */
    private static boolean isDriverLoaded = false;

    /**
     * @brief Static block to load the JDBC driver.
     */
    static
    {
        try 
        {
            // Attempt to load the Oracle JDBC driver
            Class.forName("oracle.jdbc.driver.OracleDriver");
            // Inform that the driver has been loaded successfully
            System.out.println("Driver Loaded");
            isDriverLoaded = true;
        } catch (ClassNotFoundException e)
        {
            // Handle ClassNotFoundException if the driver is not found
            e.printStackTrace();
        }
    }

    /**
     * @brief Method to establish a database connection.
     * @return Connection object representing the database connection.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection() throws SQLException
    {
        // Retrieve database connection details from environment variables
        String url = System.getenv("xkucer0t_client_app_URL");
        String user = System.getenv("xkucer0t_client_app_user");
        String password = System.getenv("xkucer0t_client_app_password");

        // Construct the JDBC URL
        url = "jdbc:oracle:thin:@//" + url + "/orclpdb";

        Connection con = null;
        // If the driver is loaded, attempt to establish a connection
        if (isDriverLoaded) 
        {
            con = DriverManager.getConnection(url, user, password);
            // Inform that the connection has been established
            System.out.println("Connection established");
        }
        return con;
    }

    /**
     * @brief Method to execute a SQL query.
     * @param sql The SQL query to execute.
     * @param args The arguments for the SQL query.
     * @return ResultSet object representing the results of the query.
     */
    public static ResultSet query(String sql, String[] args)
    {
        PreparedStatement statement = null;
        ResultSet rs = null;
        try 
        {
            // If the connection is not already established, obtain it
            if (con == null) 
            {
                con = DatabaseController.getConnection();
            }
            
            // Create a prepared statement for the provided SQL query
            con.createStatement();
            statement = con.prepareStatement(sql);

            // Set the arguments for the prepared statement
            for (int i = 0; i < args.length; i++) 
            {
                statement.setString(i + 1, args[i]);
            }

            // Execute the query and store the result set
            rs = statement.executeQuery();

        } 
        catch (SQLException e) 
        {
            // Handle SQLException if an error occurs during query execution
            e.printStackTrace();
        }
        return rs;
    }

    /**
     * @brief Method to close the database connection.
     * @throws SQLException if a database access error occurs.
     */
    public static void closeConnection() throws SQLException
    {
        if (con != null) 
        {
            // Close the connection and set it to null
            con.close();
            con = null;
        }
    }

    /**
     * @brief Instance of Connection for managing database interactions.
     */
    private static Connection con;
}
