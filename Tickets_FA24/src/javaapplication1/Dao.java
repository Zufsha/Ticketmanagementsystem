//Zufsha Khan
//The Ticket Management System is a simple Java application that helps IIT create, view, and update support tickets. 
//The system allows users to submit ticket details, view all tickets in a list, and make changes when needed
//12/7/24

package javaapplication1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Dao {
    // instance fields
    static Connection connect = null;
    Statement statement = null;

    // constructor
    public Dao() {

    }

    public Connection getConnection() {
        // setup the connection with the DB
        try {
            connect = DriverManager
                    .getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
                            + "&user=fp411&password=411");
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return connect;
    }

    // CRUD implementation

    public void createTables() {
        // variables for SQL Query table creations
        final String createTicketsTable = "CREATE TABLE zkhan_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200))";
        final String createUsersTable = "CREATE TABLE zkhan_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

        try {

            // execute queries to create tables

            statement = getConnection().createStatement();

            statement.executeUpdate(createTicketsTable);
            statement.executeUpdate(createUsersTable);
            System.out.println("Created tables in given database...");

            // end create table
            // close connection/statement object
            statement.close();
            connect.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        // add users to user table
        addUsers();
    }

    public void addUsers() {
        // add list of users from userlist.csv file to users table

        // variables for SQL Query inserts
        String sql;

        Statement statement;
        BufferedReader br;
        List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

        // read data from file
        try {
            br = new BufferedReader(new FileReader(new File("./userlist.csv")));

            String line;
            while ((line = br.readLine()) != null) {
                array.add(Arrays.asList(line.split(",")));
            }
        } catch (Exception e) {
            System.out.println("There was a problem loading the file");
        }

        try {

            // Setup the connection with the DB

            statement = getConnection().createStatement();

            // create loop to grab each array index containing a list of values
            // and PASS (insert) that data into your User table
            for (List<String> rowData : array) {

                sql = "insert into zkhan_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
                        + rowData.get(1) + "','" + rowData.get(2) + "');";
                statement.executeUpdate(sql);
            }
            System.out.println("Inserts completed in the given database...");

            // close statement object
            statement.close();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public int insertRecords(String ticketName, String ticketDesc) {
        int id = 0;
        try {
            statement = getConnection().createStatement();
            statement.executeUpdate("Insert into zkhan_tickets" + "(ticket_issuer, ticket_description) values(" + " '"
                    + ticketName + "','" + ticketDesc + "')", Statement.RETURN_GENERATED_KEYS);

            // retrieve ticket id number newly auto generated upon record insertion
            ResultSet resultSet = null;
            resultSet = statement.getGeneratedKeys();
            if (resultSet.next()) {
                // retrieve first field in table
                id = resultSet.getInt(1);
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return id;

    }

    public ResultSet readRecords() {

        ResultSet results = null;
        try {
            statement = connect.createStatement();
            results = statement.executeQuery("SELECT * FROM zkhan_tickets");
            //connect.close();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        return results;
    }

    // continue coding for updateRecords implementation
    public boolean updateTicket(int ticketId, String newIssuer, String newDescription) {
        boolean isUpdated = false;
        PreparedStatement preparedStatement = null;

        try {
            // SQL query to update ticket details
            String sql = "UPDATE zkhan_tickets SET ticket_issuer = ?, ticket_description = ? WHERE ticket_id = ?";
            preparedStatement = getConnection().prepareStatement(sql);
            preparedStatement.setString(1, newIssuer);
            preparedStatement.setString(2, newDescription);
            preparedStatement.setInt(3, ticketId);

            //update
            int rowsUpdated = preparedStatement.executeUpdate();
            
          
            if (rowsUpdated > 0) {
                isUpdated = true; // updated ticket
            }
        } catch (SQLException e) {
            e.printStackTrace();  
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return isUpdated;
    }

    // continue coding for deleteRecords implementation
    public boolean deleteTicket(int ticketId) {
        boolean isDeleted = false;
        PreparedStatement preparedStatement = null;

        try {
            // SQL query to delete ticket by ID
            String sql = "DELETE FROM zkhan_tickets WHERE ticket_id = ?";
            preparedStatement = getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, ticketId);

            // run the query
            int rowsDeleted = preparedStatement.executeUpdate();
            
            // Check if any row was deleted 
            if (rowsDeleted > 0) {
                isDeleted = true; // deleted ticket
            }
        } catch (SQLException e) {
            e.printStackTrace();  
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return isDeleted;
        
    }
    
}

