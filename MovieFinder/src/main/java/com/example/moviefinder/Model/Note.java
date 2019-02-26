package com.example.moviefinder.Model;

import com.example.moviefinder.Database.AppDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = AppDatabase.class)
public class Note extends BaseModel {


    @Column
    @PrimaryKey (autoincrement =  true)
    private int id;
    @Column
    private int parent;
    @Column
    private String note = "";

    public Note() {
    }

    public Note(int ID, int Parent, String text){
        this.id = ID;
        this.parent = Parent;
        this.note = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent() {
        return parent;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
