package com.andy.expensetracker.Login;
import com.andy.expensetracker.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;


public class LoginController  {

    private Stage stage;
    private Scene scene;
    private Parent root;

    public LoginModel loginModel=new LoginModel();

    @FXML
    private TextField Username;

    @FXML
    private PasswordField Password;

    @FXML
    void handleSignupClicked(ActionEvent event) throws IOException {
        root=FXMLLoader.load(Main.class.getResource("NewUserPage.fxml"));

        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void handleLoginClicked(ActionEvent event) throws Exception{
        String username=Username.getText().replaceAll("\\s","");
        String passwd=Password.getText().replaceAll("\\s","");

        Alert alert=new Alert(Alert.AlertType.ERROR,"Please check your username and password");

        int userID=loginModel.Login(username,passwd);
        if(userID == -1){
            alert.setHeaderText("User not found");
            alert.show();
        }else{
            loginModel.setID(userID);
            loginModel.setUsername(username);
            System.out.println("USERID: "+loginModel.getID());
            //go to main page
        }

    }
}
