package com.andy.expensetracker.Login;

import com.andy.expensetracker.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class NewUserController {
    private Scene scene;
    private Stage stage;
    private Parent root;
    private LoginModel loginmodel=new LoginModel();

    @FXML
    private TextField Username;
    @FXML
    private PasswordField Password;
    @FXML
    private PasswordField RetryPasswd;
    @FXML
    private Button HomeButton;
    @FXML
    private Button SignUpButton;

    @FXML
    void handleHomeClicked(ActionEvent event) throws IOException {
        root=FXMLLoader.load(Main.class.getResource("Login.fxml"));

        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void handleSignupClicked(ActionEvent event) throws Exception{

        Alert alert=new Alert(Alert.AlertType.ERROR);
        String username=Username.getText().replaceAll("\\s","");
        String passwd=Password.getText().replaceAll("\\s","");
        String retrypasswd=RetryPasswd.getText().replaceAll("\\s","");



        if(username.isEmpty() || passwd.isEmpty() || retrypasswd.isEmpty() ){
            alert.setContentText("You must fill in all fields");
            alert.show();
        }else if(!passwd.equals(retrypasswd)) {
            alert.setContentText("Password doesn't match");
            alert.show();
        }else{
            if(loginmodel.Singup(username,passwd)){
                root=FXMLLoader.load(Main.class.getResource("Login.fxml"));

                stage=(Stage)((Node)event.getSource()).getScene().getWindow();
                scene=new Scene(root);
                stage.setScene(scene);
                stage.show();

                Alert msg=new Alert(Alert.AlertType.INFORMATION,"\""+username+"\" is created successfully!!!");
                msg.setHeaderText("Success");
                msg.setTitle("User Create");
                msg.show();
            }


        }



    }


}
