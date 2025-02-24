package com.andy.expensetracker;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.BiConsumer;

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


//    public static <T, D> void loadSceneWithData(
//            String fxmlFile, Stage stage, BiConsumer<T, D> controllerConsumer, D data)
//    {
//        try {
//            FXMLLoader loader = new FXMLLoader(SceneLoader.class.getResource(fxmlFile));
//            Parent root = loader.load();
//
//            // Get the controller instance
//            T controller = loader.getController();
//
//            // Pass data to the controller using the lambda function
//            if (controllerConsumer != null) {
//                controllerConsumer.accept(controller, data);
//            }
//
//            // Show the new scene
//            stage.setScene(new Scene(root));
//            stage.show();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @FunctionalInterface
//    public interface SceneDataHandler<T,D>{
//        void handle(T controller, D data);
//    }

}
