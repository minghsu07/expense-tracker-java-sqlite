package com.andy.expensetracker.Database;
import com.andy.expensetracker.ConfigLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.File;
import java.sql.*;

public class DBConnection {

    private static final String DB_PATH= ConfigLoader.getProperty("DB_Name");
    public static Connection connection;

    public DBConnection(){
        File dbfile=new File(DB_PATH);
        if(!dbfile.exists()){
            try{
                Class.forName("org.sqlite.JDBC");
                connection=DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
                createDatabase();

            }catch(Exception e){
                e.printStackTrace();
            }

        }else{
            try{
                Class.forName("org.sqlite.JDBC");
                connection=DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
            }catch(Exception e){
                e.printStackTrace();
            }
        }



    }

    public Connection getConnection() {
        if (!isDbConnected()) {
            try {
                Class.forName("org.sqlite.JDBC");
                File dbfile = new File(DB_PATH);
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
                return connection;
            } catch (Exception e) {
                e.printStackTrace();

            }

            Alert alert = new Alert(Alert.AlertType.ERROR, "Error", ButtonType.OK);
            alert.setContentText("Cannot connect to the Database.");
            alert.show();

        }

        return connection;
    }

    public boolean isDbConnected() {
        try {
            return !connection.isClosed();
        } catch (Exception e) {
            return false;
        }
    }

    private static void createDatabase() throws Exception {
        try {
            if (connection != null) {
                System.out.println("New database created at " + DB_PATH);



                Statement stmt= connection.createStatement();
                stmt.addBatch("CREATE TABLE EX_USER(\n" +
                        "      USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "      USERNAME VARCHAR(20) UNIQUE,\n" +
                        "      PASSWORD VARCHAR(40),\n" +
                        "      Created_Date    Date);");
                stmt.addBatch("CREATE TABLE EX_CATEGORY(\n" +
                        "      CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "      CATEGORY_NAME VARCHAR(25) NOT NULL,\n" +
                                "      USER_ID INT NOT NULL REFERENCES EX_USER(USER_ID)\n"+
                                "  );");


//                    stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('INCOME');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('RENTAL');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('ELECTRICITY BILL');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('FOOD');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('GROCERY');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('PHONE BILL');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('HOME SUPPLIES');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('GASOLINE');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('SAVING');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('ENTERTAINMENT');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('INSURANCE');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('INVESTMENT(US)');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('INVESTMENT(TW)');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('TUITION');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('CAR LOAN');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('FUND FOR PARENTS');");
//                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('TRAVEL FUND');");

                stmt.addBatch("CREATE TABLE EX_EXPENSE(\n" +
                        "      EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "      USER_ID INTEGER NOT NULL REFERENCES EX_USER(USER_ID),\n" +
                        "      ITEM    VARCHAR(25) NOT NULL,\n" +
                        "      CATEGORY_ID INTEGER NOT NULL REFERENCES EX_CATEGORY(CATEGORY_ID),\n" +
                        "      AMOUNT DECIMAL(10,2) NOT NULL,\n" +
                        "      CREATED_DATE DATE NOT NULL,\n" +
                        "      DESCRIPTION TEXT\n" +
                        ");");

                stmt.executeBatch();
                System.out.println("Tables are ready.");

            }else{
                System.out.println("Tables are ready.");
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }



}
