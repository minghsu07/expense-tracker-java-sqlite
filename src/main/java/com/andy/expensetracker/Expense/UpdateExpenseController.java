package com.andy.expensetracker.Expense;

import com.andy.expensetracker.App;
import com.andy.expensetracker.Login.LoginController;
import com.andy.expensetracker.Login.LoginModel;
import com.andy.expensetracker.Login.User;
import com.andy.expensetracker.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class UpdateExpenseController {
//    private Stage stage;
//    private Scene scene;
//    private Parent root;

    User user= User.getInstance();
    private ExpenseModel Expense;
    @FXML
    private TextField Item,Amount;
    @FXML
    private ComboBox Combo_Category;
    @FXML
    private DatePicker SelectedDate;
    @FXML
    private TextArea Description;


    public UpdateExpenseController(ExpenseModel expense){

        Expense=expense;
    }

    public void initialize(){

        Item.getStyleClass().addAll("NewExpense-font-style");
        Amount.getStyleClass().addAll("NewExpense-font-style");
        Description.getStyleClass().addAll("NewExpense-font-style");
        SelectedDate.getStyleClass().addAll("NewExpense-font-style");

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
        ArrayList<Category> categories=getCategory();
        Combo_Category.getItems().addAll(categories);
        for (Category category : categories) {
            if (category.getName().equals(Expense.getCategory())) { // Matching by name
                Combo_Category.getSelectionModel().select(category);
                break;
            }
        }
        Combo_Category.getStyleClass().addAll("NewExpense-font-style");

        //Set a custom cell factory to define how items are displayed
        Combo_Category.setCellFactory(listView -> new ListCell<Category>(){
            @Override
            protected void updateItem(Category item,boolean empty){
                super.updateItem(item,empty);
                if (empty || item == null) {
                    setText(null);
                }else{
                    setText(item.getName());
                }
            }
        });
        //Set a custom display format for the selected item in the ComboBox
        Combo_Category.setButtonCell(new ListCell<Category>(){
            @Override
            protected void updateItem(Category item,boolean empty){
                super.updateItem(item,empty);
                if(empty || item==null){
                    setText(null);
                }
                else{
                    setText(item.getName());
                }
            }
        });
        Description.setText(Expense.getDescription());
        SelectedDate.setValue(Expense.getDate());
    }

    public void handleHomeClicked(ActionEvent event) throws Exception{

        MainController mainController=new MainController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("Main.fxml",currentStage,mainController);

    }

    public void Updateclicked(ActionEvent event) throws Exception{
        Alert alert=new Alert(Alert.AlertType.ERROR);
        PreparedStatement PreStat=null;

        Alert warning=new Alert(Alert.AlertType.WARNING,"You sure want to update?",ButtonType.YES,ButtonType.NO);
        warning.setHeaderText("Update Expense");

        Optional<ButtonType> result = warning.showAndWait();

        if(result.isPresent() && result.get() == ButtonType.NO){
            return;
        }else if(Item==null||Amount==null||Amount.getLength()<2){

            alert.setContentText("You must fill in all required fields");
            alert.show();
        }else {
            String item = Item.getText();
            BigDecimal amount = new BigDecimal(Amount.getText().substring(1).toString());
            Category selectedItem = (Category) Combo_Category.getSelectionModel().getSelectedItem();
            int category_id = selectedItem.getID();

            LocalDate date = SelectedDate.getValue();
            String formattedDate = date.toString();
            String desc = "";
            if (Description != null) {
                desc = Description.getText();

            }
            try {
                String query = "";
                query = "UPDATE EX_EXPENSE SET ITEM=? , CATEGORY_ID=?,AMOUNT=?,CREATED_DATE=?,DESCRIPTION=? WHERE EXPENSE_ID=?";

                PreStat = user.getSQLConn().getConnection().prepareStatement(query);

                PreStat.setString(1, item);
                PreStat.setInt(2, category_id);
                PreStat.setBigDecimal(3, amount);
                PreStat.setString(4, formattedDate);
                PreStat.setString(5, desc);
                PreStat.setInt(6, Expense.getID());

                PreStat.executeUpdate();
                Alert msg=new Alert(Alert.AlertType.INFORMATION,"Expensed Updated");
                msg.setHeaderText("Success");
                msg.setTitle("Update Expense");
                msg.show();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

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

                PreparedStatement PreStat= user.getSQLConn().getConnection().prepareStatement(query);
                PreStat.setInt(1,Expense.getID());
                PreStat.executeUpdate();
                System.out.println("The expense has been deleted!");

                MainController mainController=new MainController();
                Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
                SceneLoader.loadScene("Main.fxml",currentStage,mainController);
            }
            catch (SQLException e){
                e.printStackTrace();
            }
        }

    }
    private ArrayList<Category> getCategory(){

        ArrayList<Category> categories= new ArrayList<Category>();

        try{
            String query="select category_id, Concat(" +
                    "upper(substring(category_name,1,1))," +
                    "Lower(substring(category_name,2,Length(category_name)))" +
                    ") as Name from ex_category";
            PreparedStatement Prepstat=user.getSQLConn().getConnection().prepareStatement(query);
            ResultSet result=Prepstat.executeQuery();

            while(result.next()){
                categories.add(new Category(result.getInt("Category_ID"),result.getString("Name")));
            }

        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return categories;
    }
}
