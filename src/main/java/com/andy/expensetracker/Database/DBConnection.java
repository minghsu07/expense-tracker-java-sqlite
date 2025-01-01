package com.andy.expensetracker.Database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class DBConnection {
    private static final String DB_PATH = "Expense.db";
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

    public Connection getConnection(){
        if(!isDbConnected()){
            try{
                Class.forName("org.sqlite.JDBC");
                File dbfile=new File(DB_PATH);
                connection=DriverManager.getConnection("jdbc:sqlite:"+DB_PATH);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        return connection;
    }

    public boolean isDbConnected(){
        try{
            return !connection.isClosed();
        }
        catch (Exception e){
            return false;
        }
    }

    private static void createDatabase() throws Exception {
        try {
            if (connection != null) {
                System.out.println("New database created at " + DB_PATH);
//                String localDir = System.getProperty("user.dir")+"\\src\\main\\java\\com\\andy\\expensetracker\\Database\\";
//                String SQL_FILE = localDir+"initializeDB.sql";
                String query=

                        "CREATE TABLE EX_EXPENSE(\n" +
                        "      EXPENSE_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "      USER_ID INTEGER NOT NULL REFERENCES EX_USER(USER_ID),\n" +
                        "      ITEM    VARCHAR(25) NOT NULL,\n" +
                        "      CATEGORY_ID INTEGER NOT NULL REFERENCES EX_CATEGORY(CATEGORY_ID),\n" +
                        "      AMOUNT DECIMAL(10,2) NOT NULL,\n" +
                        "      CREATED_DATE DATE NOT NULL,\n" +
                        "      DESCRIPTION TEXT\n" +
                        ");";
//                Statement stmt = connection.createStatement();
//                executeSqlFile(SQL_FILE);
                Statement stmt= connection.createStatement();
                stmt.addBatch("CREATE TABLE EX_USER(\n" +
                        "      USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                        "      USERNAME VARCHAR(20) UNIQUE,\n" +
                        "      PASSWORD VARCHAR(40),\n" +
                        "      Created_Date    Date);");
                stmt.addBatch("CREATE TABLE EX_CATEGORY(\n" +
                        "      CATEGORY_ID INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                                "      CATEGORY_NAME VARCHAR(25) NOT NULL\n" +
                                "  );");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('HOUSING');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('UTILITIES');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('FOOD');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('SAVING');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('LOAN');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('HEALTHCARE');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('CHILD EXPENSE');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('PERSONAL CARE');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('PETS');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('CLOTHES');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('HOME SUPPLIES');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('FUN');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('MEMBERSHIPS');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('OTHERS');");
                stmt.addBatch("Insert into EX_CATEGORY (CATEGORY_NAME) values ('INSURANCE');");
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

    private static void executeSqlFile(String filePath) throws IOException {
        StringBuilder sqlQuery = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlQuery.append(line).append("\n");
            }
        }

        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sqlQuery.toString());
            System.out.println("SQL File executed Successfully");
        } catch (Exception e) {
            System.err.println("Error executing SQL file: " + e.getMessage());
        }
    }

}
