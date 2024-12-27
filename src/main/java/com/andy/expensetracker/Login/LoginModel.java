package com.andy.expensetracker.Login;

import com.andy.expensetracker.Database.DBConnection;
import com.andy.expensetracker.Database.SqliteConnection;
import javafx.scene.control.Alert;

import java.sql.*;

public class LoginModel {
//    Connection connection;
//    public LoginModel(){
//        connection= SqliteConnection.connector();
//        if(connection ==null){
//            System.exit(1);
//        }
//    }
//
//    public boolean isDbConnected(){
//        try{
//            return !connection.isClosed();
//        }catch(SQLException e){
//            e.printStackTrace();
//            return false;
//        }
//
//    }

    public DBConnection SQLConn;



    private int ID;
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    private String Username;
    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }





    public LoginModel(){
        SQLConn=new DBConnection();
    }

    public boolean isDbConnected(){
        try{
            return !SQLConn.getConnection().isClosed();
        }
        catch (Exception e){
            return false;
        }
    }

    public boolean Singup(String username,String passwd){

        PreparedStatement PreStat=null;
        ResultSet result=null;
        String query=null;

        try{
            query="select * from EX_USER where username=?";
            PreStat=SQLConn.getConnection().prepareStatement(query);
            PreStat.setString(1,username);
            result=PreStat.executeQuery();

            if(result.next()){
                Alert alert=new Alert(Alert.AlertType.ERROR,"The user is existing");
                alert.show();
                return false;

            }else{
                query="Insert into EX_USER (USERNAME, PASSWORD, CREATED_DATE ) values (?, ?, ?)";
                try{
                    PreStat=SQLConn.getConnection().prepareStatement(query);
                    PreStat.setString(1,username);
                    PreStat.setString(2,passwd);
                    PreStat.executeUpdate();


                    return true;
                }
                catch(SQLException e){
                    e.printStackTrace();
                    return false;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
            return false;
        }

    }

    public int Login(String username,String passwd){
        PreparedStatement PreStat=null;
        ResultSet result=null;
        String query=null;


        try {
            query = "select USER_ID,USERNAME from EX_USER where username=? and password=?";
            PreStat = SQLConn.getConnection().prepareStatement(query);
            PreStat.setString(1, username);
            PreStat.setString(2,passwd);
            result = PreStat.executeQuery();
            if(result.next()){
                return result.getInt("USER_ID");

            }else{
                return -1;
            }

        }
        catch(SQLException e){
            e.printStackTrace();
            return -1;
        }
    }
}
