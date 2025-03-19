package com.andy.expensetracker.Controllers;

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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.function.UnaryOperator;

public class NewIncome {


    User user = User.getInstance();
    private int IncomeID;
    @FXML
    private TextField Item,Amount,Category;


    @FXML
    private DatePicker SelectedDate;

    @FXML
    private TextArea Description;

    public NewIncome(){
        setIncomeID();
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
            int category_id=0;
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
                PreStat.setInt(1,user.getUserId());
                PreStat.setString(2,item);
                PreStat.setInt(3,getIncomeID());
                PreStat.setBigDecimal(4,amount);
                PreStat.setString(5,formattedDate);
                PreStat.setString(6,desc);

                PreStat.executeUpdate();

                Alert msg=new Alert(Alert.AlertType.INFORMATION,"New Income Added");
                msg.setHeaderText("Success");
                msg.setTitle("Add Income");
                msg.show();
                resetFields();
            }
            catch (SQLException e){
                e.printStackTrace();
            }


        }


    }

    private void setIncomeID(){
        String query="SELECT CATEGORY_ID FROM EX_CATEGORY WHERE CATEGORY_NAME='Income' and user_id=?";
        try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement stat=conn.prepareStatement(query)){

            stat.setInt(1,user.getUserId());
            ResultSet result= stat.executeQuery();
            while(result.next()){
                IncomeID=result.getInt("CATEGORY_ID");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    private int getIncomeID(){
        return IncomeID;
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
        Item.getStyleClass().addAll("NewExpense-font-style");
        Item.setText("");
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
        Category.setText("Income");
        SelectedDate.setValue(LocalDate.now());
        SelectedDate.getStyleClass().add("NewExpense-font-style");
        Description.setText("");
    }
}
