package com.andy.expensetracker.Controllers;
import com.andy.expensetracker.Models.LoginModel;
import com.andy.expensetracker.Models.User;
import com.andy.expensetracker.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

import java.io.IOException;


public class LoginController  {

    public LoginModel loginModel=new LoginModel();

    public LoginController(){

    }


    @FXML
    private TextField Username;

    @FXML
    private ToggleButton showPasswordToggle;

    @FXML
    private PasswordField Password;

    @FXML
    private TextField textfield;

    @FXML
    void handleSignupClicked(ActionEvent event) throws IOException {

        NewUserController newUserController=new NewUserController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/NewUserPage.fxml",currentStage, newUserController);

    }


    @FXML
    void handleLoginClicked(ActionEvent event) throws Exception{
        String username=Username.getText().replaceAll("\\s","");
        String passwd=Password.getText().replaceAll("\\s","");

        Alert alert=new Alert(Alert.AlertType.ERROR,"Please check your username and password");

        User user = User.getInstance(); // Get the Singleton instance
        // Try to log in
        if(user.login(username,passwd)){
            //go to main page
            MainController mainController=new MainController();
            Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
            SceneLoader.loadScene("views/Main.fxml",currentStage, mainController);

        }else{

            alert.setHeaderText("User not found");
            alert.show();

        }

    }



    public void initialize(){

        textfield.setVisible(false);
        showPasswordToggle.setOnAction(event -> {
            if (showPasswordToggle.isSelected()) {
                Password.setManaged(false);
                Password.setVisible(false);
                textfield.setText(Password.getText());
                textfield.setVisible(true);

            } else {
                textfield.setVisible(false);
                Password.setVisible(true);
                Password.setText(textfield.getText());
            }
        });

    }
}
