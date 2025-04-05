package com.andy.expensetracker.Controllers;

import com.andy.expensetracker.Models.User;
import com.andy.expensetracker.SceneLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class NewUserController {

    private User user=User.getInstance(); ;
    private ObservableList<String> categories=FXCollections.observableArrayList();
    public NewUserController(){

    }


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
    private Button SignUpButton,addCategoryButton,removeCategoryButton;
    @FXML
    private ListView<String> categoryListView;

    @FXML
    void handleHomeClicked(ActionEvent event) throws IOException {

        LoginController loginController=new LoginController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/Login.fxml",currentStage, loginController);


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
        }else if(username.length()>10){
            alert.setContentText("The name cannot be longer than 10 characters.\nPlease enter a shorter name");
            alert.show();
        }else{
            if(user.Singup(username,passwd,categories)){

                LoginController loginController=new LoginController();
                Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
                SceneLoader.loadScene("views/Login.fxml",currentStage, loginController);

                Alert msg=new Alert(Alert.AlertType.INFORMATION,"\""+username+"\" is created successfully!!!");
                msg.setHeaderText("Success");
                msg.setTitle("User Create");
                msg.show();
            }


        }



    }

    public void initialize() {
        categories = FXCollections.observableArrayList(
                "Housing", "Utilities", "Food", "Saving", "Loan", "Healthcare", "Child expense", "Personal care", "Pets", "Clothes", "Home supplies", "Fun", "Memberships", "Others", "Insurance", "Transportation"
        );
        categoryListView.setItems(categories);


        categoryListView.setOnMouseClicked(e -> {
            //remove an item by double clicking
            if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2) {
                String selectedItem = categoryListView.getSelectionModel().getSelectedItem();

                if (selectedItem.equals("Others")) {
                    Alert errordialog = new Alert(Alert.AlertType.ERROR);
                    errordialog.setHeaderText(selectedItem + " can not be removed.");
                    errordialog.showAndWait();

                } else {
                    categories.remove(selectedItem);
                }
            }
        });

        addCategoryButton.getStyleClass().add("addRemoveButton-style");
        removeCategoryButton.getStyleClass().add("addRemoveButton-style");

        addCategoryButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Add Category");
            dialog.setHeaderText("Enter a new category name");
            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                String input = toInitCap(name.trim());
                if (!categories.contains(input) && !input.isEmpty()) {
                    categories.add(input);
                }
            });
        });

        removeCategoryButton.setOnAction(e -> {
            String selectedItem = categoryListView.getSelectionModel().getSelectedItem();
            if(selectedItem!=null) {
                if (selectedItem.equals("Others")) {
                    Alert errordialog = new Alert(Alert.AlertType.ERROR);
                    errordialog.setHeaderText(selectedItem + " can not be removed.");
                    errordialog.showAndWait();

                } else {
                    categories.remove(selectedItem);
                }
            }
        });
    }

    private String toInitCap(String text){
        if(text==null||text.isEmpty()){
            return text;
        }
        return text.substring(0,1).toUpperCase()+text.substring(1).toLowerCase();
    }
}
