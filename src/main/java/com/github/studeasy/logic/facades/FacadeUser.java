package com.github.studeasy.logic.facades;

import com.github.studeasy.dao.exceptions.BadCredentialsException;
import com.github.studeasy.dao.userDAO.UserDAO;
import com.github.studeasy.gui.routers.AbstractRouter;
import com.github.studeasy.logic.common.Session;
import com.github.studeasy.logic.common.User;
import com.github.studeasy.logic.facades.exceptions.BadInformationException;
import com.github.studeasy.logic.utils.KeyGen;
import com.github.studeasy.logic.utils.Mail;
import com.github.studeasy.logic.utils.PasswordUtils;
import com.github.studeasy.logic.utils.regexUtils;

import java.util.ArrayList;

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
     * REGEX for the password
     */
    private final String REGEXPASSWORD = "(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}";

    /**
     * REGEX for the email
     */
    private final String REGEXEMAIL = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";

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
     * Ask to the DAO if the user is confirmed
     * @param email email of the user
     * @return true if the user is confirmed, false otherwise
     * @throws Exception if an error occur
     */
    public boolean isConfirmed(String email) throws Exception{
        return (DAO.isConfirmed(email));
    }

    /**
     * Function registerUpdate will register/update user information in the system.
     * Some information, as the password strength etc..., will be checked before registration
     * @param firstName the first name of the user
     * @param lastName the last name of the user
     * @param email the email of the user
     * @param confirmEmail confirm the email
     * @param password the password of the user
     * @param confirmPassword confirm the password
     * @throws BadInformationException if ane error occur
     */
    public String registerUpdate(String firstName,String lastName,String pseudo, String email, String confirmEmail,String password,String confirmPassword, int action) throws Exception {

        String salt;
        //Minimum eight characters, at least one letter, one number and one special character
        if(password.equals(confirmPassword)){
            if(email.equals(confirmEmail)){
                if(true){ //regexUtils.matches_password(password)
                    if (regexUtils.matches_mail(email)) { //regexUtils.matches_mail(email)
                        //if it's a register
                        if(action == 0){
                            salt = PasswordUtils.getSalt(30);
                            password = PasswordUtils.generateSecurePassword(password, salt);
                            String key = KeyGen.generateKey();
                            DAO.register(firstName, lastName, pseudo, email, password, salt,key);
                            return key;
                        }
                        //if it's an update of the profile
                        else{
                            if(AbstractRouter.confirmationBox("Are your sure ?","Update your profile","Warning")){
                                salt = PasswordUtils.getSalt(30);
                                password = PasswordUtils.generateSecurePassword(password, salt);
                                User u = DAO.update(firstName, lastName, pseudo, email, password, salt);
                                Session.getInstance().setCurrentUser(u);
                            }
                        }

                    } else {
                        throw new BadInformationException("Bad information, The password is not enough strong");
                    }
                }else{
                    throw new BadInformationException("Bad information, The password is not enough strong");
                }
            }else {
                throw new BadInformationException("Bad information, Emails doesn't correspond");
            }
        }else {
            throw new BadInformationException("Bad information, Passwords doesn't correspond");
        }
        return null;
    }

    public void sendMail(String email, String key){
        Mail.sendMail("Stud'Easy Validation","Hi, Here is your key to confirm your account:\n"+ key,email);
    }


    public void deleteAccount(int id) throws Exception {
        Session.getInstance().disconnect();
        DAO.deleteUser(id);
    }


    /**
     * Method which will get all the users
     * @return an ArrayList of all the users
     */
    public ArrayList<User> seeAllUsers() {
        //we ask to the DAO to retrieve all the users
        return DAO.seeAllUsers();
    }

    /**
     * Method who will ask to the DAO to confirm the account
     * @param email the email of the user to confirm
     * @param key key entered by the user
     * @return true if the account is confirmed false otherwise
     */
    public boolean confirmAccount(String email,String key) throws Exception {
        return(DAO.confirmAccount(email,key));
    }
}