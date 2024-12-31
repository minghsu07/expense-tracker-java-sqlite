package com.andy.expensetracker.Expense;

import com.andy.expensetracker.Login.LoginController;
import com.andy.expensetracker.Login.LoginModel;
import com.andy.expensetracker.Main;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.lang.module.ResolutionException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;

    private LoginModel Login;
    private final LocalDate localdate=LocalDate.now();
    private final int Cur_year=localdate.getYear();
    private final int Cur_mon=localdate.getMonthValue();
    @FXML
    private Label UserName,TotalAmount;

    @FXML
    private ComboBox Combo_Year,Combo_Month,Combo_Category;

    @FXML
    private TableView<ExpenseModel> ExpenseTable;

    @FXML
    private TableColumn<ExpenseModel,String> col_Item;

    @FXML
    private TableColumn<ExpenseModel,String> col_Category;
    @FXML
    private TableColumn<ExpenseModel,String> col_Amount;
    @FXML
    private TableColumn<ExpenseModel,Date> col_Date;

    private ObservableList<ExpenseModel> data;

    public MainController(Stage stage,LoginModel login){
        Login=login;
        this.stage=stage;
    }


    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {


//        year=localdate.getYear();
//        mon=localdate.getMonthValue();

//        UserName.textProperty().bind(Login.userProperty());
        UserName.setText(Login.getUsername());
        Combo_Year.getItems().addAll(Gen_Years());
        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Month.getItems().addAll(Gen_Month());
        Combo_Month.setValue(String.valueOf(Cur_mon));
        Combo_Category.getItems().add(new Category(-1,"All Categories"));
        Combo_Category.getItems().addAll(getCategory());
        Combo_Category.getSelectionModel().selectFirst();


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
        col_Item.setCellValueFactory(new PropertyValueFactory<ExpenseModel,String>("Item"));
        col_Item.getStyleClass().addAll("column-font-style");
        col_Category.setCellValueFactory(new PropertyValueFactory<ExpenseModel,String>("Category"));
        col_Category.getStyleClass().addAll("column-font-style");
        col_Amount.setCellValueFactory(new PropertyValueFactory<ExpenseModel,String>("Amount"));
        col_Amount.getStyleClass().addAll("column-style-left","column-font-style");
        col_Date.setCellValueFactory(new PropertyValueFactory<ExpenseModel,Date>("Date"));
        col_Date.getStyleClass().addAll("column-style-center","column-font-style");


        getAllExpense(Cur_year,Cur_mon,-1, Login.getID());
        ExpenseTable.setItems(data);
        ExpenseTable.setEditable(false);

        getTotalAmount(Cur_year,Cur_mon,-1, Login.getID());


    }

    public void SearchClicked(ActionEvent event) throws Exception{

        int year=Integer.parseInt(Combo_Year.getValue().toString());
        int mon=Integer.parseInt(Combo_Month.getValue().toString());
//        String date = String.valueOf(year) + ((mon < 10) ? "0" + mon : String.valueOf(mon));
        Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
        int category_id=selectedItem.getID();

        getAllExpense(year,mon,category_id, Login.getID());
        ExpenseTable.setItems(data);
        getTotalAmount(year,mon,category_id, Login.getID());
    }

    public void CurrentMonClicked(ActionEvent event) throws Exception{

        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Month.setValue(String.valueOf(Cur_mon));

    }

    public void NewExpenseClicked(ActionEvent event) throws Exception{
        FXMLLoader loader=new FXMLLoader(Main.class.getResource("NewExpense.fxml"));
        NewExpense newExpense=new NewExpense(stage,Login);
        loader.setController(newExpense);
        root=loader.load();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void LogoutClicked(ActionEvent event) throws Exception{

        FXMLLoader loader=new FXMLLoader(Main.class.getResource("Login.fxml"));
        LoginController loginController=new LoginController(stage);
        loader.setController(loginController);
        root=loader.load();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private ArrayList<String> Gen_Years(){

        LocalDate now= LocalDate.now();
        ArrayList<String> years=new ArrayList<String>();
        int CurrentYear=now.getYear();
        for(int i=0;i<15;i++){

            years.add(Integer.toString(CurrentYear-i));
        }
        return years;
    }

    private void getAllExpense(int year,int mon,int category_id,int user_id){

        String Selected_date=String.valueOf(year)+String.format("%02d",mon);
        data=null;
        try{
            String query="";
            if(category_id!=-1){
                query="SELECT STRFTIME('%F',CREATED_DATE) AS DATE," +
                        "CONCAT(UPPER(SUBSTRING(C.CATEGORY_NAME,1,1)),LOWER(SUBSTRING(C.CATEGORY_NAME,2,LENGTH(C.CATEGORY_NAME)))) AS CATEGORY_NAME," +
                        "E.ITEM AS ITEM," +
                        "printf(\"%.2f\", E.AMOUNT) AS AMOUNT " +
                        "FROM EX_EXPENSE AS E " +
                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                        "WHERE E.USER_ID="+user_id+" AND C.CATEGORY_ID="+category_id+" AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))='"+Selected_date +"'"+
                        " ORDER BY E.CREATED_DATE;";
            }else{
                query="SELECT STRFTIME('%F',CREATED_DATE) AS DATE," +
                        "CONCAT(UPPER(SUBSTRING(C.CATEGORY_NAME,1,1)),LOWER(SUBSTRING(C.CATEGORY_NAME,2,LENGTH(C.CATEGORY_NAME)))) AS CATEGORY_NAME," +
                        "E.ITEM AS ITEM," +
                        "printf(\"%.2f\", E.AMOUNT) AS AMOUNT " +
                        "FROM EX_EXPENSE AS E " +
                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                        "WHERE E.USER_ID="+user_id+" AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))='"+Selected_date +"'"+
                        " ORDER BY E.CREATED_DATE;";
            }

            PreparedStatement Prepstat=Login.SQLConn.getConnection().prepareStatement(query);
            ResultSet result=Prepstat.executeQuery();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            data=FXCollections.observableArrayList();
            while(result.next()){
                String item=result.getString("ITEM");
                String Category=result.getString("CATEGORY_NAME");
                BigDecimal amount=result.getBigDecimal("Amount");
                LocalDate date=LocalDate.parse(result.getString("DATE"));
                data.add(new ExpenseModel(item,Category,amount,date));
                System.out.println(item+" "+Category+" "+amount+" "+date);
            }


        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void getTotalAmount(int year,int mon,int category_id,int user_id){
        String Selected_date=String.valueOf(year)+String.format("%02d",mon);

        try {
            String query = "";
            if (category_id != -1) {
                query = "SELECT printf(\"%.2f\",SUM(E.AMOUNT)) AS AMOUNT " +
                        "FROM EX_EXPENSE AS E " +
                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                        "WHERE E.USER_ID=" + user_id + " AND C.CATEGORY_ID=" + category_id + " AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))='" + Selected_date + "'" +
                        " ORDER BY E.CREATED_DATE;";
            } else {
                query = "SELECT printf(\"%.2f\",SUM(E.AMOUNT)) AS AMOUNT " +
                        "FROM EX_EXPENSE AS E " +
                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                        "WHERE E.USER_ID=" + user_id + " AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))='" + Selected_date + "'" +
                        " ORDER BY E.CREATED_DATE;";
            }

            PreparedStatement Prepstat = Login.SQLConn.getConnection().prepareStatement(query);
            ResultSet result = Prepstat.executeQuery();
            while(result.next()){
                TotalAmount.setText("$"+result.getBigDecimal("AMOUNT"));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            TotalAmount.setText("$"+0.0);
        }


    }

    private ArrayList<String> Gen_Month(){

        ArrayList<String> Months=new ArrayList<String>();
        for(int i=1;i<=12;i++){

            Months.add(Integer.toString(i));
        }
        return Months;
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

