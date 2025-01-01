package com.andy.expensetracker;


import com.andy.expensetracker.Login.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("Login.fxml"));
        LoginController loginController=new LoginController(stage);
        fxmlLoader.setController(loginController);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ExpenseTracker");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}