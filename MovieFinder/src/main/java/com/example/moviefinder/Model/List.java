package com.example.moviefinder.Model;

import com.example.moviefinder.Database.AppDatabase;
import com.google.gson.Gson;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.ArrayList;

@Table(database = AppDatabase.class)
public class List extends BaseModel {
    //Future implementation

    @Column
    @PrimaryKey (autoincrement = true)
    private int id;
    @Column
    private String name;
    @Column
    private String movieGsonArray;

    public List(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieGsonArray() {
        return movieGsonArray;
    }

    public void setMovieGsonArray(String movieId) {
        this.movieGsonArray = movieId;
    }
}
