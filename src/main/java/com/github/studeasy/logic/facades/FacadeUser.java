package com.github.studeasy.logic.facades;

import com.github.studeasy.dao.exceptions.BadCredentialsException;
import com.github.studeasy.dao.userDAO.UserDAO;
import com.github.studeasy.logic.common.Session;
import com.github.studeasy.logic.common.User;
import com.github.studeasy.logic.facades.exceptions.BadInformationException;
import com.github.studeasy.logic.utils.PasswordUtils;

/**
 * The Facade User for the UserDAO
 * It contains methods that allow a user to login
 */
public class FacadeUser {

    /**
     * Singleton of the facade user
     */
    private static FacadeUser facadeUser = null;

    /**
     * The DAO connected to the database
     */
    private final UserDAO DAO;

    /**
     * Constructor of singleton FacadeUser
     * Instantiate the factory
     */
    private FacadeUser() {
        // We retrieve the UserDao
        this.DAO = UserDAO.getInstance();
    }

    /**
     * Static function that allow to get the instance of the FacadeUser
     * @return the instance of FacadeUser
     */
    public static FacadeUser getInstance(){
        if(facadeUser == null){
            facadeUser = new FacadeUser();
        }
        return facadeUser;
    }

    /**
     * Function login allows to check the password of a user,
     * if it's the right password set the user as the currentUser
     * @param email the mail of the user
     * @param password the password of the user
     * @throws Exception if the password or the email is wrong
     */
    public void  login(String email, String password) throws Exception {
        User u;
        Session sessionUser = Session.getInstance();
        u = DAO.searchUser(email);
        if(PasswordUtils.verifyUserPassword(password, u.getPassword(), u.getSalt())) { //change with hash password comparison
            sessionUser.setCurrentUser(u);
        }
        else{
            throw new BadCredentialsException("Bad Password");
        }
    }

    /**
     * Function register will register an user in the system.
     * Some information, as the password strength etc..., will be checked before registration
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param confirmEmail confirm the email
     * @param password the password of the user
     * @param confirmPassword confirm the password
     * @throws BadInformationException
     */
    public void register(String firstName,String lastName,String pseudo, String email, String confirmEmail,String password,String confirmPassword) throws Exception {

        String salt;
        //Minimum eight characters, at least one letter, one number and one special character
        String REGEXPASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";
        if(password.equals(confirmPassword)){
            if(email.equals(confirmEmail)){
                if(true){ //password.matches(REGEXPASSWORD)
                    salt = PasswordUtils.getSalt(30);
                    password = PasswordUtils.generateSecurePassword(password,salt);
                    DAO.register(firstName, lastName, pseudo, email, password,salt);
                }else{
                    throw new BadInformationException("Bad information, The password is not enough strong");
                }
            }else {
                throw new BadInformationException("Bad information, Emails doesn't correspond");
            }
        }else {
            throw new BadInformationException("Bad information, Passwords doesn't correspond");
        }
    }
}