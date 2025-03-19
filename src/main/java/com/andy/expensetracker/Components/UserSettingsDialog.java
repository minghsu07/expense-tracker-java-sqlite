package com.andy.expensetracker.Components;

import com.andy.expensetracker.Models.Category;
import com.andy.expensetracker.Models.User;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserSettingsDialog extends Application {

    User user= User.getInstance();
    @Override
    public void start(Stage primaryStage){
        showSettingsDialog();
    }

    public void showSettingsDialog(){
        //create a new dialog
        Dialog<String> dialog=new Dialog<>();
        dialog.setTitle("User Settings");
        dialog.setResizable(false);

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);

        Button changeNameButton = new Button("Change Name");
        Button changePasswordButton = new Button("Change Password");
        Button ManageCategoryButton = new Button("Manage Category");
        Button DeleteUserButton=new Button("Delete User");



        // Action handlers
        changeNameButton.setOnAction(e -> {
            this.showChangeUsernameDialog(user.getUsername());
        });
        changePasswordButton.setOnAction(e -> {
            String newPasswd=showChangePasswdDialog(user.getPassword());
            if(newPasswd!=null){
                System.out.println("Updated passwod: "+newPasswd);
            }
        });
        ManageCategoryButton.setOnAction(e->{
            this.ManageCategoryButtonDialog();
        });

        DeleteUserButton.setOnAction(e->{
            Alert alert=new Alert(Alert.AlertType.WARNING,"Are you sure?",ButtonType.YES,ButtonType.NO);
            Optional<ButtonType> result=alert.showAndWait();
            if(result.isPresent() && result.get()==ButtonType.YES){
                user.Delete();
            }

        });


        // Create a VBox to display buttons vertically
        VBox buttonContainer = new VBox(10); // 10px gap between buttons
        buttonContainer.setPrefWidth(300);
        buttonContainer.setPrefHeight(150);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(changeNameButton, changePasswordButton,ManageCategoryButton,DeleteUserButton);


        // Set Button sizes
        double buttonWidth= buttonContainer.getPrefWidth()*2/3;
        changeNameButton.setPrefWidth(buttonWidth);
        changePasswordButton.setPrefWidth(buttonWidth);
        ManageCategoryButton.setPrefWidth(buttonWidth);
        DeleteUserButton.setPrefWidth(buttonWidth);


        // Set custom layout in dialog
        dialog.getDialogPane().setContent(buttonContainer);

        // Show and wait for result
        dialog.showAndWait();
    }

    public  String showChangePasswdDialog(String currentPassword){

        Dialog<String> dialog=new Dialog<>();
        dialog.setTitle("Change Password");
        dialog.setHeaderText("Enter a new password:");

        //Buttons
        ButtonType confirmButtonType=new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType,ButtonType.CANCEL);

        //Fields
        Label curPasswdLabel=new Label("Current Password:");
        TextField curPasswdField=new TextField(currentPassword);
        curPasswdField.setEditable(false);

        Label newPasswdLabel=new Label("New Password:");
        TextField newPasswdField=new TextField();
        Label retryPasswdLabel=new Label("Retry Password:");
        TextField retryPasswdField=new TextField();

        GridPane grid=new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.add(curPasswdLabel,0,0);
        grid.add(curPasswdField,1,0);
        grid.add(newPasswdLabel,0,1);
        grid.add(newPasswdField,1,1);
        grid.add(retryPasswdLabel,0,2);
        grid.add(retryPasswdField,1,2);

        dialog.getDialogPane().setContent(grid);
        // Disable confirm button if input is empty
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        newPasswdField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmButton.setDisable(newVal.trim().isEmpty());
        });

        retryPasswdField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmButton.setDisable(newVal.trim().isEmpty());
        });

        // Handle result
        dialog.setResultConverter(dialogButton -> {
            Alert alert=new Alert(Alert.AlertType.INFORMATION);
            if (dialogButton == confirmButtonType) {
                if(!newPasswdField.getText().equals(retryPasswdField.getText())){
                    alert.setHeaderText("Password doesn't match");
                    alert.show();
                }else{
                    String newPasswd=newPasswdField.getText().trim();
                    String query="Update EX_USER set PASSWORD=? WHERE USER_ID=?";
                    try(Connection conn=user.getSQLConn().getConnection();
                        PreparedStatement Prepstat=conn.prepareStatement(query)){
                        Prepstat.setString(1,newPasswd);
                        Prepstat.setInt(2,user.getUserId());
                        Prepstat.executeUpdate();
                        user.setPassword(newPasswd);
                        alert.setHeaderText("Updated successfully");
                        alert.show();
                    }
                    catch(SQLException e){
                        e.printStackTrace();
                        alert.setHeaderText("Failed to update");
                        alert.show();
                    }
                }

            }
            return null;
        });

        return dialog.showAndWait().orElse(null);
    }

    public void showChangeUsernameDialog(String currentUsername) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Change Username");
        dialog.setHeaderText("Enter a new username:");

        // Buttons
        ButtonType confirmButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);

        // Fields
        Label curNameLabel = new Label("Current Name:");
        TextField curNameField = new TextField(currentUsername);
        curNameField.setEditable(false);

        Label newNameLabel = new Label("New Name:");
        TextField newNameField = new TextField();

        // Layout using GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(curNameLabel, 0, 0);
        grid.add(curNameField, 1, 0);
        grid.add(newNameLabel, 0, 1);
        grid.add(newNameField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        // Disable confirm button if input is empty
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmButtonType);
        confirmButton.setDisable(true);

        newNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            confirmButton.setDisable(newVal.trim().isEmpty());
        });

        // Handle result
        dialog.setResultConverter(dialogButton -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            if (dialogButton == confirmButtonType) {
                String newName = newNameField.getText().trim();
                String query = "Update EX_USER set USERNAME=? WHERE USER_ID=?";
                try (Connection conn = user.getSQLConn().getConnection();
                     PreparedStatement Prepstat = conn.prepareStatement(query)) {
                    Prepstat.setString(1, newName);
                    Prepstat.setInt(2, user.getUserId());
                    Prepstat.executeUpdate();
                    user.setUsername(newName);
                    alert.setHeaderText("Updated successfully");
                    alert.show();
                } catch (SQLException e) {
                    alert.setHeaderText("The name has been used, Please choose other names");
                    alert.show();
                    e.printStackTrace();
                }

            }
            return null;
        });

        dialog.showAndWait();
    }

    public void ManageCategoryButtonDialog(){
        //create a dialog
        Dialog<String> dialog=new Dialog<>();
        dialog.setTitle("Manage Categories");
        dialog.setHeight(400);

        //Define a list storing the categories that a user removed
        ObservableList<Category> delCategories= FXCollections.observableArrayList();

        //Layout
        Label newCategoryLabel=new Label("Name:");
        TextField newCategoryField=new TextField();
        newCategoryField.setPrefWidth(150);

        Button addCategoryButton=new Button("Add");
        ListView<Category> listviewofCategories=new ListView<>();

        ButtonType confirmButtonType=new ButtonType("Contfirm", ButtonBar.ButtonData.OK_DONE);


        VBox Container=new VBox(20);
        HBox buttoncontainer=new HBox(15);

        buttoncontainer.getChildren().addAll(newCategoryLabel,newCategoryField,addCategoryButton);
        buttoncontainer.setAlignment(Pos.CENTER);
        Container.getChildren().addAll(buttoncontainer,listviewofCategories);

        dialog.getDialogPane().setContent(Container);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType,ButtonType.CANCEL);


        //Load the current categories
        ObservableList<Category> curCategories=FXCollections.observableArrayList(user.getCategory());

        listviewofCategories.setItems(curCategories);
        //Display only the category names
        listviewofCategories.setCellFactory(listView->new ListCell<Category>(){
            @Override
            protected void updateItem(Category item,boolean empty){
                super.updateItem(item,empty);
                if(item==null || empty){
                    setText(null);
                }else{
                    setText(item.getName());
                }
            }
        });

        //Delete a category by double clicked
        listviewofCategories.setOnMouseClicked(e->{
            //check if the click was left mouse "Primary" and the number of clicks is 2
            if(e.getButton()== MouseButton.PRIMARY&& e.getClickCount()==2){

                //Get selected category
                Category selectedItem=listviewofCategories.getSelectionModel().getSelectedItem();

                if(selectedItem!=null&& curCategories.contains(selectedItem)){
                    //set "Income" and "Others" not removable
                    if(selectedItem.getName().equals("Income")||selectedItem.getName().equals("Others"))
                    {
                        Alert errordialog=new Alert(Alert.AlertType.ERROR);
                        errordialog.setHeaderText(selectedItem.getName()+" can not be removed.");
                        errordialog.showAndWait();
                        //if CategoryID 0, meaning it is new, no need to be added to the delete list
                    }else if(selectedItem.getID()==0){
                        curCategories.remove(selectedItem);
                    }else{
                        delCategories.add(selectedItem);
                        curCategories.remove(selectedItem);
                    }

                }
            }
        });


        //Add a new category
        addCategoryButton.setOnAction(e->{
            String newCategoryName=this.toInitCap(newCategoryField.getText().trim());
            //check if it is duplicate
            if(newCategoryName!=null && !curCategories.contains(newCategoryName)){

                Category newCategory=new Category(0,newCategoryName);
                curCategories.add(newCategory);

            }
        });

        //the filter runs before the default action
        dialog.getDialogPane().lookupButton(confirmButtonType).addEventFilter(ActionEvent.ACTION, event->{
            StringBuilder content=new StringBuilder();
            if(!delCategories.isEmpty()){
                content.append("Expenses in these categories will be reassigned to Others:\n");
                content.append(delCategories.stream()
                                .map(Category::getName)
                                .collect(Collectors.joining(", ")))
                        .append("\n\n");
            }

            //new categories id are 0
            ObservableList<Category> newCategories=curCategories.filtered(c->c.getID()==0);
            if(!newCategories.isEmpty()){
                content.append("The following categories will be added:\n");
                content.append(newCategories.stream()
                        .map(Category::getName).collect(Collectors.joining(", "))).append("\n\n");
            }


            Alert alert=new Alert(Alert.AlertType.WARNING,"Are you sure?",ButtonType.YES,ButtonType.NO);
            alert.setHeaderText("The following changes will be made");
            alert.setContentText(content.toString());
            Optional<ButtonType> result=alert.showAndWait();
            //If user clicks "No" or closes the alert
            if(result.isEmpty() || result.get()!=ButtonType.YES){
                event.consume(); //stop the dialog from closing
            }else{
                ModifyCategories(newCategories,delCategories);
            }
        });


        dialog.showAndWait();
    }


    private void ModifyCategories(ObservableList<Category> newcategories,ObservableList<Category> delcategories){

        int otherID;
        String query="select category_id from ex_category where lower(category_name)='others' and user_id=?";

        try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement queryOtherStmt=conn.prepareStatement(query)){
            conn.setAutoCommit(false);
            queryOtherStmt.setInt(1,user.getUserId());

            ResultSet result=queryOtherStmt.executeQuery();
            if(result.next()){
                otherID=result.getInt("category_id");
            }else{
                throw new SQLException("Others ID not found");
            }

            //add new categories
            String insertquery="insert into ex_category (category_name,user_id) values(?,?)";
            try(PreparedStatement insertCategorystmt=conn.prepareStatement(insertquery)){
                for(Category c: newcategories){
                    insertCategorystmt.setString(1,c.getName());
                    insertCategorystmt.setInt(2,user.getUserId());
                    insertCategorystmt.executeUpdate();
                    System.out.println(c.getName()+" added successfully to "+user.getUserId());
                }
            }
            catch (SQLException e){
                conn.rollback();
                e.printStackTrace();
            }

            //move deleted categories to others, then deleted them
            String updatequery="update ex_expense set category_id=? where category_id=?";
            String deletequery="delete from ex_category where category_id=?";
            try(PreparedStatement updateCategorystmt=conn.prepareStatement(updatequery);
                PreparedStatement deleteCategorystmt=conn.prepareStatement(deletequery)){
                for(Category c: delcategories){
                    //update the expense category that is deleting category
                    updateCategorystmt.setInt(1,otherID);
                    updateCategorystmt.setInt(2,c.getID());
                    updateCategorystmt.executeUpdate();
                    //delete the category
                    deleteCategorystmt.setInt(1,c.getID());
                    deleteCategorystmt.executeUpdate();
                    System.out.println(c.getID()+"deleted Successfully");
                }
            }
            catch (SQLException e){
                conn.rollback();
                e.printStackTrace();
            }

            conn.commit();

        }
        catch (SQLException e){
            e.printStackTrace();

        }
    }


    private String toInitCap(String text){
        if(text==null||text.isEmpty()){
            return text;
        }
        return text.substring(0,1).toUpperCase()+text.substring(1).toLowerCase();
    }
}
