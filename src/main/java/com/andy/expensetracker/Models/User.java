package com.andy.expensetracker.Models;

import com.andy.expensetracker.Database.DBConnection;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class User {

    //singleton instance
    private static User instance;

    public DBConnection SQLConn;
    private int ID;
    private String Username;
    private String Password;
    private Date LoginTime= Date.valueOf(LocalDate.now());


    private User(){
        SQLConn=new DBConnection();
    }

    public static User getInstance(){
        if(instance==null){
            instance=new User();
        }
        return instance;
    }

    public void setUserDate(int userId,String username,String passwd){
        this.ID=userId;
        this.Username=username;
        this.Password=passwd;
    }

    public int getUserId(){
        return ID;
    }

    public String getUsername(){
        return Username;
    }

    public String getPassword(){
        return Password;
    }

    public DBConnection getSQLConn(){
        return SQLConn;
    }
    public void setUsername(String name){ this.Username=name; }
    public void setPassword(String passwd){ this.Password=passwd;}

    public void logout(){

        try {
            SQLConn.getConnection().close();
        }catch (Exception e){
            e.printStackTrace();
        }
        instance=null;

    }

    public boolean login(String username,String passwd){


        ResultSet result=null;
        String query=null;

        query = "select USER_ID from EX_USER where username=? and password=?";
        try(Connection conn = SQLConn.getConnection(); // Use try-with-resources to automatically close the connection
            PreparedStatement PreStat = conn.prepareStatement(query)) {
//            query = "select USER_ID,USERNAME from EX_USER where username=? and password=?";

            PreStat.setString(1, username);
            PreStat.setString(2,passwd);
            result = PreStat.executeQuery();
           if(result.next()){
                this.ID=result.getInt("USER_ID");
                setUserDate(ID,username,passwd);
                return true;
            }else{
                return  false;
            }

        }
        catch(SQLException e){
            e.printStackTrace();

        }

        return false;
    }

    public boolean Singup(String username, String passwd, ObservableList<String> categories) {

        String query = "select * from EX_USER where username=?";
        int userID = 0;
        try (Connection conn = this.getSQLConn().getConnection();
             PreparedStatement checkUserStmt = conn.prepareStatement(query)
        ) {


            checkUserStmt.setString(1, username);
            try (ResultSet result = checkUserStmt.executeQuery()) {
                if (result.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR, "The user name exists");
                    alert.show();
                    return false;

                }
            }


            conn.setAutoCommit(false);
            String insertQuery = "Insert into EX_USER (USERNAME, PASSWORD, CREATED_DATE ) values (?, ?, ?)";
            try (PreparedStatement insertUserStmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

                insertUserStmt.setString(1, username);
                insertUserStmt.setString(2, passwd);
                insertUserStmt.setDate(3, LoginTime);

                int affectedRows = insertUserStmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating user failed, no rows affected.");
                }
                try (ResultSet generatedKeys = insertUserStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        userID = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to retrieve user ID.");
                    }
                }

            }

            String insertCategoreis = "INSERT into EX_CATEGORY (CATEGORY_NAME,USER_ID) VALUES(?,?)";
            try (PreparedStatement insertCategoryStmt = conn.prepareStatement(insertCategoreis)) {
                categories.add("Income");
                for (String category : categories) {
                    insertCategoryStmt.setString(1, category);
                    insertCategoryStmt.setInt(2, userID);
                    insertCategoryStmt.executeUpdate();
                }

            } catch (SQLException categoryEx) {
                conn.rollback();
                throw categoryEx;
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean Delete(){

        String deleteAllExpense="delete from ex_expense where user_id=?";
        String deleteAllCategories="delete from ex_category where user_id=?";
        String deleteUser="delete from ex_user where user_id=?";
        try(Connection conn=this.getSQLConn().getConnection();
            PreparedStatement stmtExpense =conn.prepareStatement(deleteAllExpense);
            PreparedStatement stmtCategory=conn.prepareStatement(deleteAllCategories);
            PreparedStatement stmtUser=conn.prepareStatement(deleteUser)) {

            //begin tran
            conn.setAutoCommit(false);

            //set parameters for each statments
            stmtExpense.setInt(1,getUserId());
            stmtCategory.setInt(1,getUserId());
            stmtUser.setInt(1,getUserId());

            stmtExpense.addBatch();
            stmtCategory.addBatch();
            stmtUser.addBatch();

            int[] result=stmtExpense.executeBatch();
            stmtCategory.executeBatch();
            stmtUser.executeBatch();

            conn.commit();

            Alert msg=new Alert(Alert.AlertType.INFORMATION,"The User has been deleted successfully", ButtonType.OK);
            msg.show();
            return true;


        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }



    }

    public  ArrayList<Category> getCategory(){

        ArrayList<Category> categories= new ArrayList<Category>();

        String query="select category_id, Concat(" +
                "upper(substring(category_name,1,1))," +
                "Lower(substring(category_name,2,length(category_name)))" +
                ") as Name from ex_category WHERE USER_ID=?";

        try(Connection conn=this.getSQLConn().getConnection();
            PreparedStatement Prepstat=conn.prepareStatement(query))
        {
            Prepstat.setInt(1,getUserId());
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
