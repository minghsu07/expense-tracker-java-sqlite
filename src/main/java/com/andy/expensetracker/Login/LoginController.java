package com.andy.expensetracker.Login;
import com.andy.expensetracker.Expense.MainController;
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

//    private Stage stage;
//    private Scene scene;
//    private Parent root;

    public LoginModel loginModel=new LoginModel();

    public LoginController(){

    }

//    public LoginController(Stage stage){
//        this.stage=stage;
//    }

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
        SceneLoader.loadScene("NewUserPage.fxml",currentStage, newUserController);

//        FXMLLoader loader=new FXMLLoader(App.class.getResource("NewUserPage.fxml"));
//        NewUserController newUserController=new NewUserController(stage);
//        loader.setController(newUserController);
//        root=loader.load();
////        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
//        scene=new Scene(root);
//        stage.setScene(scene);
//        stage.show();
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
            SceneLoader.loadScene("Main.fxml",currentStage, mainController);

        }else{

            alert.setHeaderText("User not found");
            alert.show();

        }

    }



    public void initialize(){
//        Username.setText("Andy");
//        Password.setText("andy");
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
