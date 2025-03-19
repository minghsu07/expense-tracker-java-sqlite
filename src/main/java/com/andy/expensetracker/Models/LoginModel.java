package com.andy.expensetracker.Models;

import com.andy.expensetracker.Database.DBConnection;
import javafx.scene.control.Alert;

import java.sql.*;

public class LoginModel {

    public DBConnection SQLConn;
    private int ID;
    private String Username;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }


    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }





    public LoginModel(){
        SQLConn=new DBConnection();
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
