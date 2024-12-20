package com.andy.expensetracker.Database;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
                String localDir = System.getProperty("user.dir")+"\\src\\main\\java\\com\\andy\\expensetracker\\Database\\";
                String SQL_FILE = localDir+"initializeDB.sql";
                Statement stmt = conn.createStatement();
                executeSqlFile(SQL_FILE, conn);
                System.out.println("Tables are ready.");

            }else{
                System.out.println("Tables are ready.");
            }
        }
    }

    private static void executeSqlFile(String filePath, Connection conn) throws IOException {
        StringBuilder sqlQuery = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sqlQuery.append(line).append("\n");
            }
        }

        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sqlQuery.toString());
            System.out.println("SQL File executed Successfully");
        } catch (Exception e) {
            System.err.println("Error executing SQL file: " + e.getMessage());
        }
    }

}
