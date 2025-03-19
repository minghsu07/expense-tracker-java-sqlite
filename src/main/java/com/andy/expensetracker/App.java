package com.andy.expensetracker;


import com.andy.expensetracker.Controllers.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {



        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/Login.fxml"));
        LoginController loginController=new LoginController();
        fxmlLoader.setController(loginController);
        Parent root=fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setTitle("ExpenseTracker");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}