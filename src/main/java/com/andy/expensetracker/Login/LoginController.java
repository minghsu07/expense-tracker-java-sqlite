package com.andy.expensetracker.Login;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class LoginController implements Initializable {

    public LoginModel loginModel=new LoginModel();

    @FXML
    private Label isConnected;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if(loginModel.isDbConnected()){
            isConnected.setText("Connected");
        }else{
            isConnected.setText("Not connected");
        }
    }
}
