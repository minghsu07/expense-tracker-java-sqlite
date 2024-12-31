package com.andy.expensetracker.Expense;

import com.andy.expensetracker.Login.LoginController;
import com.andy.expensetracker.Login.LoginModel;
import com.andy.expensetracker.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.sql.Date;
import java.util.function.UnaryOperator;

public class NewExpense {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private LoginModel Login;

    @FXML
    private TextField Item,Amount;
    @FXML
    private ComboBox Combo_Category;
    @FXML
    private DatePicker SelectedDate;

    public NewExpense(Stage stage,LoginModel login){
        Login=login;
        this.stage=stage;
    }

    public void handleHomeClicked(ActionEvent event) throws Exception{

        FXMLLoader loader=new FXMLLoader(Main.class.getResource("Main.fxml"));
        MainController mainController=new MainController(stage,Login);
        loader.setController(mainController);
        root=loader.load();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void initialize(){
        // Define a filter to allow only digits
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*\\.?\\d*")) { // Allows only numbers
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter=new TextFormatter<>(filter);
        Amount.setTextFormatter(textFormatter);
        Combo_Category.getItems().addAll(getCategory());
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
    }

    public void Addclicked(ActionEvent event) throws  Exception{
        Alert alert=new Alert(Alert.AlertType.ERROR);
        String item=Item.getText().replaceAll("\\s","");
        Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
        int category_id=selectedItem.getID();
        BigDecimal amount=new BigDecimal(Amount.getText().toString());
        LocalDate date=SelectedDate.getValue();
        String formattedDate = date.toString();



        if(item.isEmpty()){
            alert.setContentText("You must fill in all fields");
            alert.show();
        }else {
            PreparedStatement PreStat=null;

            try{
                String query="";
                query="INSERT INTO EX_EXPENSE (USER_ID,ITEM,CATEGORY_ID,AMOUNT,CREATED_DATE) VALUES(?,?,?,?,?)";

                PreStat=Login.SQLConn.getConnection().prepareStatement(query);
                PreStat.setInt(1,Login.getID());
                PreStat.setString(2,item);
                PreStat.setInt(3,category_id);
                PreStat.setBigDecimal(4,amount);
                PreStat.setString(5,formattedDate);

                PreStat.executeUpdate();

                Alert msg=new Alert(Alert.AlertType.INFORMATION,"New Expensed Added");
                msg.setHeaderText("Success");
                msg.setTitle("Add Expense");
                msg.show();
            }
            catch (SQLException e){
                e.printStackTrace();
            }
            finally {
                Login.SQLConn.getConnection().close();
                PreStat.close();
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
            PreparedStatement Prepstat=Login.SQLConn.getConnection().prepareStatement(query);
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
