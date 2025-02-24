package com.andy.expensetracker.Expense;

import com.andy.expensetracker.EditableComboBoxFilter;
import com.andy.expensetracker.Login.User;
import com.andy.expensetracker.SceneLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.function.UnaryOperator;

public class NewExpense {


    User user = User.getInstance();

    @FXML
    private TextField Amount;
    @FXML
    private EditableComboBoxFilter Item;
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
        SceneLoader.loadScene("Main.fxml",currentStage, mainController);

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

            String item=(String)Item.getSelectionModel().getSelectedItem();
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




    private ArrayList<Category> getCategory(){

        Combo_Category.getItems().clear();
        ArrayList<Category> categories= new ArrayList<Category>();


            String query="select category_id, Concat(" +
                    "upper(substring(category_name,1,1))," +
                    "Lower(substring(category_name,2,Length(category_name)))" +
                    ") as Name from ex_category where Name != \"Income\"";

        try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement Prepstat=conn.prepareStatement(query))
        {
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
        resetItemField();
//        Item.setText("");
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
        Combo_Category.getItems().addAll(getCategory());
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


    private void resetItemField(){
        String query="SELECT DISTINCT E.ITEM AS ITEM " +
                "FROM EX_EXPENSE AS E " +
                "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                "WHERE E.USER_ID="+user.getUserId();

        ObservableList<String> items= FXCollections.observableArrayList();

        try(Connection conn=user.getSQLConn().getConnection();
        PreparedStatement PrepStat=conn.prepareStatement(query))
        {
            ResultSet result=PrepStat.executeQuery();
            while(result.next()){
                items.add(result.getString("ITEM"));
            }

            Item.setItemsList(items);

        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
