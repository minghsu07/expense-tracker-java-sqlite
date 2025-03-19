package com.andy.expensetracker.Components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class EditableComboBoxFilter extends ComboBox<String> {

    private  ObservableList<String> fullList;


    public EditableComboBoxFilter(){
//        super(items);
        this.fullList=FXCollections.observableArrayList();
        this.setEditable(true);

        this.getEditor().textProperty().addListener((observable,oldValue,newValue)->{

                filterItems(newValue);


        });

        this.focusedProperty().addListener((observable, oldValue, newValue) -> {
           this.show();
        });

        // Ensure that we display matching items, even when the user is typing

        this.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(item);
                }
            }

        });

        this.setVisibleRowCount(5);
    }
    public void setItemsList(ObservableList<String> items) {
        this.fullList = items;
        this.setItems(items);
    }
    private void filterItems(String query){



        ObservableList<String> filteredList;
        if(query.isEmpty()){
            filteredList=fullList;
        }else{
            filteredList=FXCollections.observableArrayList();
            for(String item:fullList){

                if(item.toLowerCase().startsWith(query.toLowerCase())){
                    filteredList.add(item);
                }
            }

        }



        if(query.isEmpty()){
            this.getSelectionModel().clearSelection();
            this.setItems(fullList);
        }

        this.setItems(filteredList);
        this.show();

    }
}
