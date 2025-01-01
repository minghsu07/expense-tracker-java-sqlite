package com.andy.expensetracker.Expense;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

public class ExpenseModel {
    private int ID;
    private String Item;
    private String Category;
    private BigDecimal Amount;
    private LocalDate Date;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public ExpenseModel(int id, String item, String category, BigDecimal amount, LocalDate date){
        ID=id;
        Item=item;
        Category=category;
        Amount=amount;
        Date=date;
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

    public String getAmount() {
        return "$"+Amount;
    }

    public void setAmount(BigDecimal amount) {
        Amount = amount;
    }

    public LocalDate getDate() {
        return Date;
    }

    public void setDate(LocalDate date) {
        Date = date;
    }


}
