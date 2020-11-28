package com.github.studeasy.gui.controller.user;

import com.github.studeasy.gui.controller.AbstractController;
import com.github.studeasy.gui.routers.user.RouterUser;
import com.github.studeasy.logic.facades.user.FacadeUser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;

public class LoginController extends AbstractController {

    @FXML
    private TextField emailTF;

    @FXML
    private TextField passwordTF;

    public LoginController(){
        super(new RouterUser(),new FacadeUser());
    }

    public void login(ActionEvent av){
        String email = emailTF.getText();
        String password = passwordTF.getText();
        ((FacadeUser) facade).login(email,password);
    }

    public void loadRegister(ActionEvent event) throws IOException {
        ((RouterUser) router).register(event);
    }
}