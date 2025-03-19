package com.andy.expensetracker.Models;

public class Category {
    public int getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    private int ID;
    private String Name;

    public Category(int id,String name){
        ID=id;
        Name=name;
    }

}
