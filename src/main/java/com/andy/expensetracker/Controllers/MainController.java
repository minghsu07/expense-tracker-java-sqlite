package com.andy.expensetracker.Controllers;

import com.andy.expensetracker.App;
import com.andy.expensetracker.Components.UserSettingsDialog;
import com.andy.expensetracker.Models.*;
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

    private final LocalDate localdate=LocalDate.now();
    private int Cur_year=localdate.getYear();
    private int Cur_mon=localdate.getMonthValue();

    private ObservableList<ExpenseModel> Alldata=FXCollections.observableArrayList();
    private ObservableList<ExpenseModel> FilteredData=FXCollections.observableArrayList();
    private int Income_ID;
    @FXML
    private Label TotalIncome,TotalExpense,Balance;

    @FXML
    private ComboBox Combo_Year,Combo_Month,Combo_Category;

    @FXML
    private TableView<ExpenseModel> ExpenseTable;

    @FXML
    private TableColumn<ExpenseModel,String> col_Item;

    @FXML
    private TableColumn<ExpenseModel,String> col_Category;
    @FXML
    private TableColumn<ExpenseModel,Double> col_Amount;
    @FXML
    private TableColumn<ExpenseModel,Date> col_Date;

    @FXML
    private TableColumn<ExpenseModel,Void> col_button;

    private ObservableList<ExpenseModel> data;



    public MainController(){

    }


    @FXML
    public void initialize()  {


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
        col_Amount.setCellValueFactory(new PropertyValueFactory<ExpenseModel,Double>("Amount"));
        col_Amount.getStyleClass().addAll("column-style-left","column-font-style");
        col_Amount.setCellFactory(column-> new TableCell<ExpenseModel,Double>(){
            @Override
            protected void updateItem(Double amount,boolean empty){
                super.updateItem(amount,empty);
                DecimalFormat decimalFormat=new DecimalFormat("#,##0.00");
                if(empty || amount==null){
                    setText(null);
                }else{
                    setText("$"+String.format(decimalFormat.format(amount)));
                }
            }
        });
        col_Date.setCellValueFactory(new PropertyValueFactory<ExpenseModel,Date>("Date"));
        col_Date.getStyleClass().addAll("column-style-center","column-font-style");

        col_button.getStyleClass().addAll("buttonTableView-style");
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



        ExpenseTable.setEditable(false);
        ExpenseTable.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) { // Check if double-click
                ExpenseModel selectedExpense = ExpenseTable.getSelectionModel().getSelectedItem();
                if (selectedExpense != null) {
                    UpdateExpenseController updateExpenseController=new UpdateExpenseController(selectedExpense);
                    Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
                    SceneLoader.loadScene("views/UpdateExpense.fxml",currentStage,updateExpenseController);


                }
            }
        });
        Category selectedCategory=(Category)Combo_Category.getSelectionModel().getSelectedItem();


        getAllExpense();
        FilterExpenses(Cur_year,Cur_mon,selectedCategory);
        getBalance(Cur_year,Cur_mon,selectedCategory);

    }

    public void SearchClicked(ActionEvent event) throws Exception{
        Cur_year = Integer.parseInt(Combo_Year.getValue().toString());

        try{
            Cur_mon=Integer.parseInt(Combo_Month.getValue().toString());
        }catch (Exception e){
            Cur_mon=0;
        }

        Category selectedCategory=(Category)Combo_Category.getSelectionModel().getSelectedItem();


        getBalance(Cur_year,Cur_mon,selectedCategory);
        FilterExpenses(Cur_year,Cur_mon,selectedCategory);

    }

    public void CurrentMonClicked(ActionEvent event) throws Exception{
        Cur_year=localdate.getYear();
        Cur_mon=localdate.getMonthValue();
        Combo_Year.setValue(String.valueOf(Cur_year));
        Combo_Month.getSelectionModel().select(Cur_mon);
        Category selectedCategory=(Category)Combo_Category.getSelectionModel().getSelectedItem();

        getBalance(Cur_year,Cur_mon,selectedCategory);
        FilterExpenses(Cur_year,Cur_mon,selectedCategory);
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
        Category selectedCategory=(Category)Combo_Category.getSelectionModel().getSelectedItem();

        getBalance(Cur_year,Cur_mon,selectedCategory);
        FilterExpenses(Cur_year,Cur_mon,selectedCategory);
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
        Category selectedCategory=(Category)Combo_Category.getSelectionModel().getSelectedItem();

        getBalance(Cur_year,Cur_mon,selectedCategory);
        FilterExpenses(Cur_year,Cur_mon,selectedCategory);
    }

    public void NewExpenseClicked(ActionEvent event) throws Exception{
        NewExpense newExpense=new NewExpense();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/NewExpense.fxml",currentStage,newExpense);

    }

    public void NewIncomeClicked(ActionEvent event) throws Exception{
        NewIncome newIncome=new NewIncome();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/NewIncome.fxml",currentStage,newIncome);

    }

    public void UserClicked(ActionEvent event) throws Exception{
        UserSettingsDialog settingsDialog = new UserSettingsDialog();
        settingsDialog.showSettingsDialog();
        MainController mainController=new MainController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/Main.fxml",currentStage,mainController);
    }
    public void LogoutClicked(ActionEvent event) throws Exception{
        user.logout();

        LoginController loginController=new LoginController();
        Stage currentStage=(Stage)((Node)event.getSource()).getScene().getWindow();
        SceneLoader.loadScene("views/Login.fxml",currentStage,loginController);

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

    private void getAllExpense(){




        String query="SELECT E.EXPENSE_ID AS ID, STRFTIME('%F',CREATED_DATE) AS DATE," +
                        "CONCAT(UPPER(SUBSTRING(C.CATEGORY_NAME,1,1)),LOWER(SUBSTRING(C.CATEGORY_NAME,2,LENGTH(C.CATEGORY_NAME)))) AS CATEGORY_NAME," +
                        "E.ITEM AS ITEM," +
                        "printf(\"%.2f\", E.AMOUNT) AS AMOUNT, " +
                        "E.DESCRIPTION as DESCRIPTION " +
                        "FROM EX_EXPENSE AS E " +
                        "JOIN EX_CATEGORY AS C ON E.CATEGORY_ID=C.CATEGORY_ID " +
                        "WHERE E.USER_ID="+user.getUserId()+" order by date";


        try(Connection conn=user.getSQLConn().getConnection();
            PreparedStatement Prepstat=conn.prepareStatement(query)){

            ResultSet result=Prepstat.executeQuery();

            data=FXCollections.observableArrayList();
            DecimalFormat decimalFormat=new DecimalFormat("#,##0.00");
            while(result.next()){
                int id=result.getInt("ID");
                String item=result.getString("ITEM");
                String Category=result.getString("CATEGORY_NAME");
                Double amount=result.getDouble("Amount");
                decimalFormat.format(amount);
                LocalDate date=LocalDate.parse(result.getString("DATE"));
                String desc=result.getString("DESCRIPTION");

                Alldata.add(new ExpenseModel(id,item,Category,amount,date,desc));

            }


        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }
    public void FilterExpenses(int year,int mon,Category categoryName){
        String CategoryName=categoryName.getName();
        if(CategoryName=="All Categories"){
            if(mon==0){
                FilteredData=Alldata.filtered((expense)->
                        expense.getDate().getYear()==year );
            }else{
                FilteredData=Alldata.filtered((expense)->
                        expense.getDate().getYear()==year &&
                                expense.getDate().getMonthValue()==mon);
            }

        }else{
            if(mon==0){
                FilteredData=Alldata.filtered((expense)->
                        CategoryName.equals(expense.getCategory()) &&
                                expense.getDate().getYear()==year);
            }else{
                FilteredData=Alldata.filtered((expense)->
                        CategoryName.equals(expense.getCategory()) &&
                                expense.getDate().getYear()==year &&
                                expense.getDate().getMonthValue()==mon);
            }

        }

        ExpenseTable.setItems(FilteredData);
    }

    private void getBalance(int year, int mon,Category categoryName){
        DecimalFormat df = new DecimalFormat("#,##0.00"); // Rounds to 2 decimal places
        String CategoryName= categoryName.getName();
        double income;
        double expense;


        if(CategoryName.equals("All Categories")){
            if(mon==0){
                income=Alldata.filtered((e)->
                                "Income".equals(e.getCategory()) &&
                                        e.getDate().getYear()==year
                        )
                        .stream()
                        .mapToDouble(ExpenseModel::getAmount).sum();

                expense=Alldata.filtered(e ->
                                !"Income".equals(e.getCategory())&&
                                        e.getDate().getYear()==year )
                        .stream().mapToDouble(ExpenseModel::getAmount).sum();
            }else{
                income=Alldata.filtered((e)->
                                "Income".equals(e.getCategory()) &&
                                        e.getDate().getYear()==year &&
                                        e.getDate().getMonthValue()==mon
                        )
                        .stream()
                        .mapToDouble(ExpenseModel::getAmount).sum();

                expense=Alldata.filtered(e ->
                                !"Income".equals(e.getCategory())&&
                                        e.getDate().getYear()==year &&
                                        e.getDate().getMonthValue()==mon)
                        .stream().mapToDouble(ExpenseModel::getAmount).sum();
            }

        }else{
            income=0;
            expense=Alldata.filtered(e ->
                            CategoryName.equals(e.getCategory())&&
                                    e.getDate().getYear()==year &&
                                    e.getDate().getMonthValue()==mon)
                    .stream().mapToDouble(ExpenseModel::getAmount).sum();
        }


        String totalIncome = df.format(income);
        TotalIncome.setText("$"+totalIncome);

        String totalExpense=df.format(expense);
        TotalExpense.setText("$"+totalExpense);

        String balance=df.format(income-expense);
        Balance.setText("$"+balance);



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
        //sqllite
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

