package com.andy.expensetracker.Login;

import com.andy.expensetracker.Database.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {

    //singleton instance
    private static User instance;

    public DBConnection SQLConn;
    private int ID;
    private String Username;
    private String Password;

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

    public void logout(){
//        ID=null;
//        Username=null;
//        Password=null;
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
                return false;
            }

        }
        catch(SQLException e){
            e.printStackTrace();

        }

        return false;
    }

}
