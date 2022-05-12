package com.example.bookapp.models;

import java.io.Serializable;

public class Tags implements Serializable {
    public String id;
    public String name;
    public boolean isSelected=false;
    public Tags() {
    }

    public Tags(String id , String name,  boolean isSelected) {
        this.id = id;
        this.name = name;
        this.isSelected = isSelected;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        return ( this.getId().equals (((Tags) obj). getId())
                && this.getName().equals (((Tags) obj). getName()));
    }
}
