package com.andy.expensetracker.Expense;

import com.andy.expensetracker.App;
import com.andy.expensetracker.Login.LoginController;
import com.andy.expensetracker.Login.LoginModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class UpdateExpenseController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private LoginModel Login;
    private ExpenseModel Expense;
    @FXML
    private TextField Item,Amount;
    @FXML
    private ComboBox Combo_Category;
    @FXML
    private DatePicker SelectedDate;

    public UpdateExpenseController(Stage stage,LoginModel login,ExpenseModel expense){
        Login=login;
        this.stage=stage;
        Expense=expense;
    }

    public void initialize(){
        Item.setText(Expense.getItem());
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\$\\d*\\.?\\d*")) { // Allows only numbers
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter=new TextFormatter<>(filter);
        Amount.setText(Expense.getAmount());
        Amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith("$")) {
                Amount.setText("$" + newValue.replaceAll("[^0-9.]", "")); // Remove invalid characters
            } else {
                Amount.setText("$" + newValue.substring(1).replaceAll("[^0-9.]", ""));
            }
        });
        Combo_Category.setValue(Expense.getCategory());
        SelectedDate.setValue(Expense.getDate());
    }

    public void handleHomeClicked(ActionEvent event) throws Exception{

        FXMLLoader loader=new FXMLLoader(App.class.getResource("Main.fxml"));
        MainController mainController=new MainController(stage,Login);
        loader.setController(mainController);
        root=loader.load();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void Updateclicked(ActionEvent event) throws Exception{

    }

    public void DeleteClicked(ActionEvent event) throws Exception{

        Alert alert=new Alert(Alert.AlertType.WARNING,"You sure want to delete this expense?",ButtonType.YES,ButtonType.NO);
        alert.setHeaderText("Delete Expense");

        Optional<ButtonType> result = alert.showAndWait();

        // Check user response
        if (result.isPresent() && result.get() == ButtonType.YES) {
            try{
                String query="";
                query="DELETE FROM EX_EXPENSE WHERE EXPENSE_ID=?";

                PreparedStatement PreStat= Login.SQLConn.getConnection().prepareStatement(query);
                PreStat.setInt(1,Expense.getID());
                PreStat.executeUpdate();
                System.out.println("The expense has been deleted!");
                handleHomeClicked(new ActionEvent());
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

    }
}
