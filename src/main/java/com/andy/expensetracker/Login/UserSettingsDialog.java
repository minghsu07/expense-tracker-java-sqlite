package com.andy.expensetracker.Login;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UserSettingsDialog extends Application {

    @Override
    public void start(Stage primaryStage){
        showSettingsDialog();
    }

    public void showSettingsDialog(){
        //create a new dialog
        Dialog<String> dialog=new Dialog<>();
        dialog.setTitle("User Settings");
        dialog.setResizable(false);
        dialog.setOnCloseRequest(e -> e.consume());

        Button changeNameButton = new Button("Change Name");
        Button changePasswordButton = new Button("Change Password");
        Button cancelButton = new Button("Cancel");

        // Set Button sizes
        double width = Double.MAX_VALUE / 2;
        changeNameButton.setMaxWidth(width);
        changePasswordButton.setPrefWidth(width);
        cancelButton.setPrefWidth(width);

        // Action handlers
        changeNameButton.setOnAction(e -> {
            dialog.setResult("Change Name");
            dialog.close();
        });
        changePasswordButton.setOnAction(e -> {
            dialog.setResult("Change Password");
            dialog.close();
        });
        cancelButton.setOnAction(e -> {
            dialog.setResult("Cancel");
            dialog.close();
        });

        dialog.setOnCloseRequest(e -> {
            dialog.setResult("Cancel");
            dialog.close();
        });

        // Create a VBox to display buttons vertically
        VBox buttonContainer = new VBox(10); // 10px gap between buttons
        buttonContainer.setPrefWidth(300);
        buttonContainer.setPrefHeight(150);
        buttonContainer.getChildren().addAll(changeNameButton, changePasswordButton, cancelButton);

        // Set custom layout in dialog
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.setContent(buttonContainer);

        // Show and wait for result
        dialog.showAndWait().ifPresent(result -> {
            System.out.println("User selected: " + result);
        });
    }
}
