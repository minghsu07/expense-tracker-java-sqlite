package com.andy.expensetracker.Database;
import java.io.File;
import java.sql.*;

public class SqliteConnection {
    private static final String DB_PATH = "Expense.db";

    public static void Initialize(){
        File dbfile=new File(DB_PATH);
        if(!dbfile.exists()){
            try{
                createDatabase();
            }catch(Exception e){
                e.printStackTrace();
            }

        }
    }

    public static Connection connector(){
        Initialize();
        try{

            Class.forName("org.sqlite.JDBC");
            Connection conn=DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
            return conn;
        }
        catch (Exception e){
            return null;
        }
    }

    private static void createDatabase() throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH)) {
            if (conn != null) {
                System.out.println("New database created at " + DB_PATH);
                createTablesIfNotExists(); // Create initial tables
            }else{
                System.out.println("Tables are ready.");
            }
        }
    }

    private static void createTablesIfNotExists() throws Exception {
        String createTableSQL = """
                CREATE TABLE EX_USER(
                      USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                      USERNAME VARCHAR(20) UNIQUE,
                      PASSWORD VARCHAR(40),
                      Created_Date    Date
                  );
                
                 CREATE TABLE EX_CATEGORY(
                      CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                      CATEGORY_NAME VARCHAR(25) NOT NULL
                  );
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('HOUSING');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('UTILITIES');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('FOOD');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('SAVING');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('LOAN');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('HEALTHCARE');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('CHILD EXPENSE');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('PERSONAL CARE');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('PETS');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('CLOTHES');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('HOME SUPPLIES');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('FUN');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('MEMBERSHIPS');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('OTHERS');
                  Insert into EX_CATEGORY (CATEGORY_NAME) values ('INSURANCE');
                  CREATE TABLE EX_EXPENSE(
                      EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                      USER_ID INTEGER NOT NULL REFERENCES EX_USER(USER_ID),
                      ITEM    VARCHAR(25) NOT NULL,
                      CATEGORY_ID INTEGER NOT NULL REFERENCES EX_CATEGORY(CATEGORY_ID),
                      AMOUNT DECIMAL(10,2) NOT NULL,
                      CREATED_DATE DATE NOT NULL,
                      DESCRIPTION TEXT
                  );
                """;

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(createTableSQL);
            System.out.println("Tables are ready.");
        }
    }
}
