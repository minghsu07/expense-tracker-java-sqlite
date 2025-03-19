package com.andy.expensetracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneLoader {

    public static <T> void loadScene(
            String fxmlFile,Stage stage,T controller
    ){
        try{
            //Load the fxml file
            FXMLLoader loader=new FXMLLoader(SceneLoader.class.getResource(fxmlFile));

            //Manually set a controller
            loader.setController(controller);

            Parent root=loader.load();

            //Set the new scene in the existing stage
            stage.setScene(new Scene(root));
            stage.show();

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



}
