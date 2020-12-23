package com.github.studeasy.dao.userDAO;

import com.github.studeasy.dao.exceptions.BadCredentialsException;
import com.github.studeasy.logic.common.User;
import com.github.studeasy.logic.facades.exceptions.BadKeyException;
import com.github.studeasy.logic.factory.Factory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
    public MySQLUserDAO() {
        Factory connection = Factory.getInstance();
        this.DB = connection.getDb();
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
                currentUser= new User(resultSet.getInt(1),resultSet.getString(3),resultSet.getString(2),resultSet.getString(6),resultSet.getString(5),resultSet.getInt(4),resultSet.getString(8),resultSet.getString(7),resultSet.getInt(9),resultSet.getString(10));
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return currentUser;
    }

    public boolean isConfirmed(String email) throws Exception{
        // We prepare the SQL request to retrieve a user
        PreparedStatement preparedStatement;
        // Will contain the result of the query
        ResultSet resultSet;
        String request = "SELECT confirm FROM user WHERE emailAddress = ?";
        preparedStatement = DB.prepareStatement(request);
        preparedStatement.setString(1, email);
        // We execute the query
        resultSet = preparedStatement.executeQuery();
        // We check if the query retrieved a user
        if (!resultSet.next()) {
            // No, we throw an error
            throw new BadCredentialsException("No user found");
        } else {
            return (resultSet.getInt(1) == 1);
        }
    }

    /**
     * Method which will register a new user in the database
     * @param firstName first name of the new user
     * @param lastName last Name of the new user
     * @param pseudo pseudo of the new user
     * @param email email of the new user
     * @param password password of the new user
     * @param salt  salt key of the new user
     * @param key key sent by email
     * @throws SQLException if an error occur
     */
    public void register(String firstName,String lastName,String pseudo,String email,String password, String salt, String key) throws SQLException {
        // We prepare the SQL request to insert a user
        PreparedStatement preparedStatement;
        String request = "INSERT INTO user (firstName,lastName,role,password,emailAddress,pseudo,salt,keyConfirm) VALUES  (?,?,?,?,?,?,?,?)";
        preparedStatement = DB.prepareStatement(request);
        preparedStatement.setString(1, firstName);
        preparedStatement.setString(2, lastName);
        preparedStatement.setInt(3, 1);
        preparedStatement.setString(4, password);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, pseudo);
        preparedStatement.setString(7, salt);
        preparedStatement.setString(8, key);
        // We execute the query
        preparedStatement.executeUpdate();

    }

    /**
     * method which will delete an user from the db
     * @param id the email of the new user
     * @throws Exception if an error occur
     */
    public void deleteUser(int id) throws Exception{
        // We prepare the SQL request to update a user
        PreparedStatement preparedStatement;
        String request = "DELETE FROM user WHERE user.idUser = ? ";
        preparedStatement = DB.prepareStatement(request);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Method which will update the user in database
     * @param firstName the first name of the user
     * @param lastName last name of the user
     * @param pseudo pseudo of the user
     * @param email email of the user
     * @param password password of the user
     * @param salt salt of the user
     * @return the User updated
     * @throws Exception if an error occur
     */
    public User update(String firstName, String lastName, String pseudo, String email, String password, String salt) throws Exception {
        // We prepare the SQL request to insert a user
        PreparedStatement preparedStatement;
        String request = "UPDATE user SET firstName= ?, "
                +" lastName = ? ,"
                +" role = ?, "
                + "password = ?, "
                + "emailAddress = ?,"
                + "pseudo = ?,"
                + "salt = ?"
                + "WHERE emailAddress = ?";
        preparedStatement = DB.prepareStatement(request);
        preparedStatement.setString(1, firstName);
        preparedStatement.setString(2, lastName);
        preparedStatement.setInt(3, 1);
        preparedStatement.setString(4, password);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, pseudo);
        preparedStatement.setString(7, salt);
        preparedStatement.setString(8, email);

        // We execute the query
        preparedStatement.executeUpdate();
        return searchUser(email);
    }

    /**
     * Get all the users who are student
     * @return An ArrayList of all the students in database
     */
    public ArrayList<User> seeAllUsers(){
        ArrayList<User> users = new ArrayList<>();
        try {
            // We prepare the SQL request to retrieve a category
            PreparedStatement preparedStatement;
            // Will contain the result of the query
            ResultSet resultSet;
            String request = "SELECT * FROM user WHERE role=?";
            preparedStatement = DB.prepareStatement(request);
            //Indicate that the role of the user must be student
            preparedStatement.setInt(1, 1);

            // We execute the query
            resultSet = preparedStatement.executeQuery();
            // We retrieve all the existing users
            while (resultSet.next()) {
                // We create the user
                User user = new User(resultSet.getInt(1),resultSet.getString(3),resultSet.getString(2),resultSet.getString(6),resultSet.getString(5),resultSet.getInt(4),resultSet.getString(8),resultSet.getString(7),resultSet.getInt(9), resultSet.getString(10));
                // And put it with the others
                users.add(user);
            }
        }
        // Error with the database
        catch(SQLException err){
            err.printStackTrace();
        }
        return users;
    }

    /**
     * Method who will confirm the account
     * @param email the email of the user to confirm
     * @param key key entered by the user
     * @return true if the account is confirmed false otherwise
     */
    public boolean confirmAccount(String email,String key) throws Exception{

        // We prepare the SQL request to retrieve a user
        PreparedStatement preparedStatement;
        PreparedStatement preparedStatement2;
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
            //if the 2 keys corresponds
            System.out.println("key "+ resultSet.getString(12));
            if(resultSet.getString(12).equals(key)){
                //let's confirm the account
                String request2 = "UPDATE user SET confirm = ?"+" WHERE emailAddress = ?";
                preparedStatement2 = DB.prepareStatement(request2);
                preparedStatement2.setInt(1, 1);
                preparedStatement2.setString(2, email);
                preparedStatement2.executeUpdate();
                return true;

            }else{
                return false;
            }

        }


    }

}
