package com.andy.expensetracker.Login;

import com.andy.expensetracker.Database.DBConnection;
import com.andy.expensetracker.Database.SqliteConnection;
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

    DBConnection SQLConn;
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
}
