package com.andy.expensetracker.Controllers;

import com.andy.expensetracker.Models.Category;
import com.andy.expensetracker.Models.User;
import com.andy.expensetracker.SceneLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class NewExpense {


    User user = User.getInstance();

    @FXML
    private TextField Amount;
    @FXML
    private TextField Item;
    @FXML
    private ComboBox Combo_Category;
    @FXML
    private DatePicker SelectedDate;

    @FXML
    private TextArea Description;

    public NewExpense(){

    }


    public void handleHomeClicked(ActionEvent event) throws Exception{

        MainController mainController=new MainController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/Main.fxml",currentStage, mainController);

    }

    public void initialize(){
        resetFields();

    }

    public void Addclicked(ActionEvent event) throws  Exception{
        Alert alert=new Alert(Alert.AlertType.ERROR);

        if(Item==null||Amount==null||Amount.getLength()<2){
            alert.setContentText("You must fill in all required fields");
            alert.show();
        }else {

            String item=Item.getText();
            BigDecimal amount=new BigDecimal(Amount.getText().substring(1).toString());
            Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
            int category_id=selectedItem.getID();

            LocalDate date=SelectedDate.getValue();
            String formattedDate = date.toString();
            String desc="";
            if (Description != null) {
                desc = Description.getText();

            }


            String query="INSERT INTO EX_EXPENSE (USER_ID,ITEM,CATEGORY_ID,AMOUNT,CREATED_DATE,DESCRIPTION) VALUES(?,?,?,?,?,?)";

            try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement PreStat=conn.prepareStatement(query))
            {

                PreStat.setInt(1, user.getUserId());
                PreStat.setString(2,item);
                PreStat.setInt(3,category_id);
                PreStat.setBigDecimal(4,amount);
                PreStat.setString(5,formattedDate);
                PreStat.setString(6,desc);

                PreStat.executeUpdate();

                Alert msg=new Alert(Alert.AlertType.INFORMATION,"New Expensed Added");
                msg.setHeaderText("Success");
                msg.setTitle("Add Expense");
                msg.show();
                resetFields();
            }
            catch (SQLException e){
                e.printStackTrace();
            }



        }




    }






    private void resetFields(){
        // Define a filter to allow only digits
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\$\\d*\\.?\\d*")) { // Allows only numbers
                return change;
            }
            return null;
        };
//        Item.setCellFactory(param-> new EditableComboBoxFilter());
        Item.getStyleClass().addAll("NewExpense-font-style");

        TextFormatter<String> textFormatter=new TextFormatter<>(filter);
        Amount.setTextFormatter(textFormatter);
        Amount.getStyleClass().addAll("NewExpense-font-style");
        Amount.setText("$");
        Amount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.startsWith("$")) {
                Amount.setText("$" + newValue.replaceAll("[^0-9.]", "")); // Remove invalid characters
            } else {
                Amount.setText("$" + newValue.substring(1).replaceAll("[^0-9.]", ""));
            }
        });
        Combo_Category.getItems().clear();
        Combo_Category.getItems().addAll(this.getCategory());
        Combo_Category.getStyleClass().addAll("NewExpense-font-style");
//        Combo_Category.getSelectionModel().selectFirst();
        Combo_Category.setValue("");
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
        SelectedDate.setValue(LocalDate.now());
        SelectedDate.getStyleClass().add("NewExpense-font-style");
        Description.setText("");
    }

    private ArrayList<Category> getCategory(){
       ArrayList<Category> categories=user.getCategory();
       categories.removeIf(category -> category.getName().equals("Income"));
       return categories;
    }

}
