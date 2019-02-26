package com.example.moviefinder.Database;


import com.example.moviefinder.Model.Note;
import com.example.moviefinder.Model.Note_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

public class DatabaseQueryHelper {


    public static Note findNoteForParentMovie(int id) {
        return SQLite.select().from(Note.class).where(Note_Table.parent.eq(id)).querySingle();
    }


}
