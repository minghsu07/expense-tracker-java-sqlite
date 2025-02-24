package com.andy.expensetracker.Login;

import com.andy.expensetracker.App;
import com.andy.expensetracker.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class NewUserController {
//    private Scene scene;
//    private Stage stage;
//    private Parent root;
    private LoginModel loginmodel=new LoginModel();

    public NewUserController(){

    }
//    public NewUserController(Stage stage){
//        this.stage=stage;
//    }

    @FXML
    private TextField Username,RetryField;

    @FXML
    private TextField PasswordField;
    @FXML
    private PasswordField Password;
    @FXML
    private PasswordField RetryPasswd;
    @FXML
    private ToggleButton ShowPasswdToggle,ShowRetryToggle;
    @FXML
    private Button HomeButton;
    @FXML
    private Button SignUpButton;

    @FXML
    void handleHomeClicked(ActionEvent event) throws IOException {

        LoginController loginController=new LoginController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("Login.fxml",currentStage, loginController);

//        FXMLLoader loader=new FXMLLoader(App.class.getResource("Login.fxml"));
//        LoginController loginController=new LoginController(stage);
//        loader.setController(loginController);
//        root=loader.load();
//        scene=new Scene(root);
//        stage.setScene(scene);
//        stage.show();
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

                LoginController loginController=new LoginController();
                Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
                SceneLoader.loadScene("NewUserPage.fxml",currentStage, loginController);

//                FXMLLoader loader=new FXMLLoader(App.class.getResource("Login.fxml"));
//                LoginController loginController=new LoginController(stage);
//                loader.setController(loginController);
//                root=loader.load();
//                scene=new Scene(root);
//                stage.setScene(scene);
//                stage.show();

                Alert msg=new Alert(Alert.AlertType.INFORMATION,"\""+username+"\" is created successfully!!!");
                msg.setHeaderText("Success");
                msg.setTitle("User Create");
                msg.show();
            }


        }



    }

    public void initialize(){
        PasswordField.setVisible(false);
        ShowPasswdToggle.setOnAction(event -> {
            if (ShowPasswdToggle.isSelected()) {
                Password.setManaged(false);
                Password.setVisible(false);
                PasswordField.setText(Password.getText());
                PasswordField.setVisible(true);

            } else {
                PasswordField.setVisible(false);
                Password.setVisible(true);
                Password.setText(PasswordField.getText());
            }
        });

        RetryField.setVisible(false);
        ShowRetryToggle.setOnAction(event -> {
            if (ShowRetryToggle.isSelected()) {
                RetryPasswd.setManaged(false);
                RetryPasswd.setVisible(false);
                RetryField.setText(Password.getText());
                RetryField.setVisible(true);

            } else {
                RetryField.setVisible(false);
                RetryPasswd.setVisible(true);
                RetryPasswd.setText(RetryField.getText());
            }
        });
    }

}
