package com.andy.expensetracker.Expense;

import com.andy.expensetracker.Login.User;
import com.andy.expensetracker.Login.LoginController;
import com.andy.expensetracker.Login.LoginModel;
import com.andy.expensetracker.App;
import com.andy.expensetracker.Login.UserSettingsDialog;
import com.andy.expensetracker.SceneLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class MainController  {

    User user = User.getInstance();
//    private LoginModel Login=new LoginModel();
//    private final Button showDetailButton = new Button("Detail");
    private final LocalDate localdate=LocalDate.now();
    private int Cur_year=localdate.getYear();
    private int Cur_mon=localdate.getMonthValue();
    private int Income_ID;
    @FXML
    private Label UserName,TotalIncome,TotalExpense,Balance;

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

    @FXML
    private TableColumn<ExpenseModel,Void> col_button;

    private ObservableList<ExpenseModel> data;



    public MainController(){

    }


    @FXML
    public void initialize()  {



//        year=localdate.getYear();
//        mon=localdate.getMonthValue();

//        UserName.textProperty().bind(Login.userProperty());
        UserName.setText(user.getUsername());
        Combo_Year.getItems().addAll(Gen_Years());
        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Year.getStyleClass().addAll("comboBox-font-style");
        Combo_Month.getItems().addAll(Gen_Month());
        Combo_Month.setValue(String.valueOf(Cur_mon));
        Combo_Month.getStyleClass().addAll("comboBox-font-style");
        Combo_Category.getItems().add(new Category(-1,"All Categories"));
        Combo_Category.getItems().addAll(getCategory());
        Combo_Category.getSelectionModel().selectFirst();
        Combo_Category.getStyleClass().addAll("comboBox-font-style");


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
//        col_button.setCellValueFactory(new PropertyValueFactory<ExpenseModel,Button>("Button"));
        col_button.getStyleClass().addAll("buttonTableView-style");
//        showDetailButton.setStyle("-fx-background-color: ##C6D5E7; -fx-text-fill: Black;");
        col_button.setCellFactory(param -> new TableCell<ExpenseModel, Void>() {
            private final Button showDetailButton = new Button("Detail");
            {
                showDetailButton.getStyleClass().addAll("buttonDetail-style");
                showDetailButton.setCursor(Cursor.HAND);
                showDetailButton.setOnAction(event -> {
                    // Get the row data associated with this button click
                    ExpenseModel selectedModel = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, selectedModel.getDescription());
                    alert.setHeaderText("Description");
                    alert.show();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {

                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }
                ExpenseModel expense = getTableView().getItems().get(getIndex());
                if (expense.getDescription() == null || expense.getDescription().isEmpty()) {
                    setGraphic(null); // Hide button if description is empty or null
                } else {
                    setGraphic(showDetailButton); // Show button if description exists
                }
            }
        });



        getAllExpense(Cur_year,Cur_mon,-1, user.getUserId());
        ExpenseTable.setItems(data);
        ExpenseTable.setEditable(false);
        ExpenseTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) { // Check if double-click
                ExpenseModel selectedExpense = ExpenseTable.getSelectionModel().getSelectedItem();
                if (selectedExpense != null) {
                    UpdateExpenseController updateExpenseController=new UpdateExpenseController(selectedExpense);
                    Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
                    SceneLoader.loadScene("UpdateExpense.fxml",currentStage,updateExpenseController);
                    FXMLLoader loader=new FXMLLoader(App.class.getResource("UpdateExpense.fxml"));
//                    UpdateExpenseController updateExpenseController=new UpdateExpenseController(stage,Login,selectedExpense);
//                    loader.setController(updateExpenseController);
//                    try {
//                        root=loader.load();
//                    } catch (IOException e) {
//                        throw new RuntimeException(e);
//                    }
//                    scene=new Scene(root);
//
//                    stage.setScene(scene);
//                    stage.show();
                }
            }
        });
//        getTotalIncome(Cur_year,Cur_mon,-1, Login.getID());
//        getTotalExpense(Cur_year,Cur_mon,-1, Login.getID());
        getBalance(Cur_year,Cur_mon,-1, user.getUserId());

    }

    public void SearchClicked(ActionEvent event) throws Exception{
        int year,mon;

        Cur_year = Integer.parseInt(Combo_Year.getValue().toString());

        try{
            Cur_mon=Integer.parseInt(Combo_Month.getValue().toString());
        }catch (Exception e){
            Cur_mon=0;
        }

//        String date = String.valueOf(year) + ((mon < 10) ? "0" + mon : String.valueOf(mon));
        Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
        int category_id=selectedItem.getID();


//        getTotalIncome(year,mon,category_id, Login.getID());
//        getTotalExpense(year,mon,category_id, Login.getID());
        getBalance(Cur_year,Cur_mon,category_id, user.getUserId());
        getAllExpense(Cur_year,Cur_mon,category_id, user.getUserId());
        ExpenseTable.setItems(data);

    }

    public void CurrentMonClicked(ActionEvent event) throws Exception{
        Cur_year=localdate.getYear();
        Cur_mon=localdate.getMonthValue();
        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Month.getSelectionModel().select(Cur_mon);
        Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
        int category_id=selectedItem.getID();

        //for table view
        getAllExpense(Cur_year,Cur_mon,category_id, user.getUserId());
        ExpenseTable.setItems(data);
        //for labels
        getBalance(Cur_year,Cur_mon,category_id, user.getUserId());
    }

    public void PreviousMonClicked(ActionEvent event) throws Exception{
        if(Cur_mon==0){ //Currently all month
            Cur_year-=1;
        }else if(Cur_mon==1){ //Currently January
            Cur_mon=12;
            Cur_year-=1;
        }else{
            Cur_mon-=1;
        }


        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Month.getSelectionModel().select(Cur_mon);
        Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
        int category_id=selectedItem.getID();

        //for table view
        getAllExpense(Cur_year,Cur_mon,category_id, user.getUserId());
        ExpenseTable.setItems(data);
        //for labels
        getBalance(Cur_year,Cur_mon,category_id, user.getUserId());
    }

    public void NextMonClicked(ActionEvent event) throws Exception{
        if(Cur_mon==0){ //Currently all month
            Cur_year+=(Cur_year==localdate.getYear())?0:1;
        }else if(Cur_mon==12){ //Currently December
            Cur_mon=(Cur_year==localdate.getYear())?Cur_mon:1;
            Cur_year+=(Cur_year==localdate.getYear())?0:1;
        }else{
            Cur_mon+=1;
        }

        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Month.getSelectionModel().select(Cur_mon);
        Category selectedItem=(Category)Combo_Category.getSelectionModel().getSelectedItem();
        int category_id=selectedItem.getID();

        //for table view
        getAllExpense(Cur_year,Cur_mon,category_id, user.getUserId());
        ExpenseTable.setItems(data);
        //for labels
        getBalance(Cur_year,Cur_mon,category_id, user.getUserId());
    }

    public void NewExpenseClicked(ActionEvent event) throws Exception{
        NewExpense newExpense=new NewExpense();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("NewExpense.fxml",currentStage,newExpense);

    }

    public void NewIncomeClicked(ActionEvent event) throws Exception{
        NewIncome newIncome=new NewIncome();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("NewIncome.fxml",currentStage,newIncome);

    }

    public void UserClicked(ActionEvent event) throws Exception{
        UserSettingsDialog settingsDialog = new UserSettingsDialog();
        settingsDialog.showSettingsDialog();
    }
    public void LogoutClicked(ActionEvent event) throws Exception{
        user.logout();

        LoginController loginController=new LoginController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("Login.fxml",currentStage,loginController);

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
        String Selected_date="";


        data=null;



//            if(category_id!=-1){
//                query="SELECT E.EXPENSE_ID AS ID, STRFTIME('%F',CREATED_DATE) AS DATE," +
//                        "CONCAT(UPPER(SUBSTRING(C.CATEGORY_NAME,1,1)),LOWER(SUBSTRING(C.CATEGORY_NAME,2,LENGTH(C.CATEGORY_NAME)))) AS CATEGORY_NAME," +
//                        "E.ITEM AS ITEM," +
//                        "printf(\"%.2f\", E.AMOUNT) AS AMOUNT, " +
//                        "E.DESCRIPTION as DESCRIPTION " +
//                        "FROM EX_EXPENSE AS E " +
//                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
//                        "WHERE E.USER_ID="+user_id+" AND C.CATEGORY_ID="+category_id+" AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=?"+
//                        " ORDER BY E.CREATED_DATE;";
//            }else{
//                query="SELECT E.EXPENSE_ID AS ID, STRFTIME('%F',CREATED_DATE) AS DATE," +
//                        "CONCAT(UPPER(SUBSTRING(C.CATEGORY_NAME,1,1)),LOWER(SUBSTRING(C.CATEGORY_NAME,2,LENGTH(C.CATEGORY_NAME)))) AS CATEGORY_NAME," +
//                        "E.ITEM AS ITEM," +
//                        "printf(\"%.2f\", E.AMOUNT) AS AMOUNT, " +
//                        "E.DESCRIPTION as DESCRIPTION " +
//                        "FROM EX_EXPENSE AS E " +
//                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
//                        "WHERE E.USER_ID="+user_id+" AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=?"+
//                        " ORDER BY E.CREATED_DATE;";
//            }
            String query="SELECT E.EXPENSE_ID AS ID, STRFTIME('%F',CREATED_DATE) AS DATE," +
                        "CONCAT(UPPER(SUBSTRING(C.CATEGORY_NAME,1,1)),LOWER(SUBSTRING(C.CATEGORY_NAME,2,LENGTH(C.CATEGORY_NAME)))) AS CATEGORY_NAME," +
                        "E.ITEM AS ITEM," +
                        "printf(\"%.2f\", E.AMOUNT) AS AMOUNT, " +
                        "E.DESCRIPTION as DESCRIPTION " +
                        "FROM EX_EXPENSE AS E " +
                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                        "WHERE E.USER_ID="+user_id+" AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=? ";
            if(category_id!=-1){
                query +=" AND C.CATEGORY_ID=" + category_id;
            }
            query+=" ORDER BY E.CREATED_DATE ";
            if(mon==0){
                Selected_date=String.valueOf(year);
                query=query.replace(",STRFTIME('%m',CREATED_DATE)","");


            }else{
                Selected_date=String.valueOf(year)+String.format("%02d",mon);
            }
        try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement Prepstat=conn.prepareStatement(query)){

            Prepstat.setString(1,Selected_date);
            ResultSet result=Prepstat.executeQuery();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            data=FXCollections.observableArrayList();
            DecimalFormat decimalFormat=new DecimalFormat("#,##0.00");
            while(result.next()){
                int id=result.getInt("ID");
                String item=result.getString("ITEM");
                String Category=result.getString("CATEGORY_NAME");
                BigDecimal amount=result.getBigDecimal("Amount");
                decimalFormat.format(amount);
                LocalDate date=LocalDate.parse(result.getString("DATE"));
                String desc=result.getString("DESCRIPTION");

                data.add(new ExpenseModel(id,item,Category,amount,date,desc));
//                System.out.println(item+" "+Category+" "+amount+" "+date);
            }


        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void getBalance(int year, int mon, int category_id,int user_id){
        DecimalFormat formatter=new DecimalFormat("#,##0.00");

        double expense=getTotalExpense(year,mon,category_id,user_id);
        double income=getTotalIncome(year,mon,category_id,user_id);
        double balance=income-expense;

        TotalExpense.setText("$"+formatter.format(expense));
        TotalIncome.setText("$"+formatter.format(income));
        Balance.setText("$"+formatter.format(balance));
    }

    private double getTotalExpense(int year,int mon,int category_id,int user_id){
        double TotalExpense=0;
        String Selected_date="";



//        if (category_id != -1) {
//            query = "SELECT printf(\"%.2f\",SUM(E.AMOUNT)) AS AMOUNT " +
//                    "FROM EX_EXPENSE AS E " +
//                    "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
//                    "WHERE E.USER_ID=" + user_id + " AND C.CATEGORY_ID=" + category_id + " AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=? AND C.CATEGORY_ID!="+getIncome_ID()+
//                    " ORDER BY E.CREATED_DATE ;";
//        } else {
//            query = "SELECT printf(\"%.2f\",SUM(E.AMOUNT)) AS AMOUNT " +
//                    "FROM EX_EXPENSE AS E " +
//                    "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
//                    "WHERE E.USER_ID=" + user_id + " AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=? AND C.CATEGORY_ID!="+getIncome_ID()+
//                    " ORDER BY E.CREATED_DATE ";
//        }
        String query = "SELECT printf(\"%.2f\",SUM(E.AMOUNT)) AS AMOUNT " +
                    "FROM EX_EXPENSE AS E " +
                    "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                    "WHERE E.USER_ID=" + user_id + " AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=? AND C.CATEGORY_ID!="+getIncome_ID()+" ";

        if(category_id!=-1){
            query +=" AND C.CATEGORY_ID=" + category_id;
       }
        query+=" ORDER BY E.CREATED_DATE ";
        if(mon==0){
            Selected_date=String.valueOf(year);
            query=query.replace(",STRFTIME('%m',CREATED_DATE)","");


        }else{
            Selected_date=String.valueOf(year)+String.format("%02d",mon);
        }

        try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement Prepstat=conn.prepareStatement(query)){

            Prepstat.setString(1,Selected_date);
            ResultSet result = Prepstat.executeQuery();

            while(result.next()){
                TotalExpense=result.getDouble("AMOUNT");

            }
        }
        catch (SQLException e){
            e.printStackTrace();

        }
        return TotalExpense;

    }

    private double getTotalIncome(int year,int mon,int category_id,int user_id){
        double TotalIncome=0;
        String Selected_date="";

        if(category_id!=getIncome_ID() && category_id!=-1){
            return TotalIncome;
        }


        String query = "SELECT printf(\"%.2f\",SUM(E.AMOUNT)) AS AMOUNT " +
                    "FROM EX_EXPENSE AS E " +
                    "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                    "WHERE E.USER_ID=" + user_id + " AND C.CATEGORY_id="+getIncome_ID() +" AND CONCAT(STRFTIME('%Y',CREATED_DATE),STRFTIME('%m',CREATED_DATE))=?" +
                    " ORDER BY E.CREATED_DATE;";
        if(mon==0){
            Selected_date=String.valueOf(year);
            query=query.replace(",STRFTIME('%m',CREATED_DATE)","");


        }else{
            Selected_date=String.valueOf(year)+String.format("%02d",mon);
        }
        try (Connection conn=user.getSQLConn().getConnection();
             PreparedStatement preStat = conn.prepareStatement(query)){

            preStat.setString(1,Selected_date);
            ResultSet result = preStat.executeQuery();

            while(result.next()){
                TotalIncome=result.getDouble("AMOUNT");

            }
        }
        catch (SQLException e){
            e.printStackTrace();

        }

        return TotalIncome;
    }

    private ArrayList<String> Gen_Month(){

        ArrayList<String> Months=new ArrayList<String>();
        Months.add("All months");
        for(int i=1;i<=12;i++){

            Months.add(Integer.toString(i));
        }
        return Months;
    }

    private ArrayList<Category> getCategory(){

        ArrayList<Category> categories= new ArrayList<Category>();
        String query="select category_id, Concat(" +
                                            "upper(substring(category_name,1,1))," +
                                            "Lower(substring(category_name,2,Length(category_name)))" +
                                            ") as Name from ex_category";

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

    private int getIncome_ID(){

        String query="select category_id from ex_category where category_name=\"INCOME\"";
        try(Connection conn=user.getSQLConn().getConnection();
                PreparedStatement Prestat=conn.prepareStatement(query))
        {
            ResultSet resultSet=Prestat.executeQuery();
            while(resultSet.next()){
                Income_ID= resultSet.getInt("category_id");
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return Income_ID;
    }



}

