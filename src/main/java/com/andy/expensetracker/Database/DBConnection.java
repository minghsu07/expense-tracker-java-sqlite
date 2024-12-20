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
        return connection;
    }

    private static void createDatabase() throws Exception {
        try {
            if (connection != null) {
                System.out.println("New database created at " + DB_PATH);
                String localDir = System.getProperty("user.dir")+"\\src\\main\\java\\com\\andy\\expensetracker\\Database\\";
                String SQL_FILE = localDir+"initializeDB.sql";
                Statement stmt = connection.createStatement();
                executeSqlFile(SQL_FILE);
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
