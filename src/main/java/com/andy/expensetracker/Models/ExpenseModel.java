package com.andy.expensetracker.Models;

import javafx.scene.control.Button;

import java.time.LocalDate;

public class ExpenseModel {
    private int ID;
    private String Item;
    private String Category;
    private Double Amount;
    private LocalDate Date;
    private String Description;
    private Button Button;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ExpenseModel(int id, String item, String category, Double amount, LocalDate date,String description){
        ID=id;
        Item=item;
        Category=category;
        Amount=amount;
        Date=date;

        if(description!=null){
            Description=description;
            Button=new Button("Detail");
        }
    }

    public String getItem() {
        return Item;
    }

    public void setItem(String item) {
        Item = item;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public Double getAmount() {
        return Amount;
    }

    public void setAmount(Double amount) {
        Amount = amount;
    }

    public LocalDate getDate() {
        return Date;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
    public void setDate(LocalDate date) {
        Date = date;
    }
    public Button getButton() {
        return Button;
    }

    public void setButton(Button button) {
        Button = button;
    }

}
