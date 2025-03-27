package com.andy.expensetracker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static Properties properties=new Properties();

    static {
//        try(FileInputStream fis=new FileInputStream("src/main/resources/com/andy/expensetracker/config.properties")){
        try(InputStream fis=ConfigLoader.class.getClassLoader().getResourceAsStream("com/andy/expensetracker/config.properties")){
            if(fis==null){
                System.out.println("Unable to find config.properties");
            }else{
                properties.load(fis);
            }

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}
