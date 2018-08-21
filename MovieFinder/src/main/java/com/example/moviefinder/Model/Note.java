package com.example.moviefinder.Model;

public class Note {

    private int id;
    private int parent;
    private String note;

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
