package com.github.studeasy.dao.userDAO;

import com.github.studeasy.dao.exceptions.BadCredentialsException;
import com.github.studeasy.logic.common.User;
import com.github.studeasy.logic.factory.MySQLFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * The user DAO using a MySQL database
 * Contains all the methods accessing user related data
 */
public class MySQLUserDAO extends UserDAO{

    /**
     * The connection to the database
     */
    private final Connection DB;

    /**
     * Instantiate the connection db
     */
    private MySQLUserDAO() {
        MySQLFactory connection = MySQLFactory.getInstance();
        this.DB = connection.getDb();
    }

    /**
     * Lazy holder which contains the objet MySQLUserDAO
     */
    private static class createMySQLFactory{
        static final MySQLUserDAO INSTANCE = new MySQLUserDAO();
    }

    /**
     * Static method which returns the instance of the MySQLUserDAO
     * @return the instance of MySQLUserDAO
     */
    public static MySQLUserDAO getInstance(){
        return MySQLUserDAO.createMySQLFactory.INSTANCE;
    }

    /**
     * Method asking the database if a user with this mail exist,
     * and returning him if he exists
     * @param email the email to check
     * @return the user corresponding
     * @throws Exception if the user doesn't exist in the database
     */
    public User searchUser(String email) throws Exception{
        User currentUser = null;
        try {
            // We prepare the SQL request to retrieve a user
            PreparedStatement preparedStatement;
            // Will contain the result of the query
            ResultSet resultSet;
            String request = "SELECT * FROM user WHERE emailAddress = ?";
            preparedStatement = DB.prepareStatement(request);
            preparedStatement.setString(1, email);
            // We execute the query
            resultSet = preparedStatement.executeQuery();
            // We check if the query retrieved a user
            if (!resultSet.next()) {
                // No, we throw an error
                throw new BadCredentialsException("No user found");
            } else {
                // We create a user according to his role
                currentUser= new User(resultSet.getString(3),resultSet.getString(2),resultSet.getString(6),resultSet.getString(5),resultSet.getInt(4),resultSet.getString(8),resultSet.getString(7),resultSet.getInt(9));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return currentUser;
    }
}
