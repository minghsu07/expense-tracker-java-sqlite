package com.andy.expensetracker.Login;

import com.andy.expensetracker.Database.SqliteConnection;
import java.sql.*;

public class LoginModel {
    Connection connection;
    public LoginModel(){
        connection= SqliteConnection.connector();
        if(connection ==null){
            System.exit(1);
        }
    }

    public boolean isDbConnected(){
        try{
            return !connection.isClosed();
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }

    }
}
