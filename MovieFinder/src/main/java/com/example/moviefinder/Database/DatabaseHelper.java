package com.example.moviefinder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static DatabaseHelper sInstance;

    static final String dbName = "movieFinderDB";
    static final String note_table = "Note";
    static final String id_col = "Id";
    static final String parent_col = "Parent";
    static final String text_col = "Text";

    private static final int DATABASE_VERSION = 1;

    public static synchronized DatabaseHelper getsInstance(Context context) {
        if (sInstance == null){
            sInstance = new DatabaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public DatabaseHelper(@Nullable Context context) {
        super(context, dbName, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + note_table + " (" +
                id_col + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                parent_col + " TEXT NOT NULL, " +
                text_col + " TEXT NOT NULL);"
        );
    }

    public boolean insertNote(int parent, String note) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(parent_col, parent);
        contentValues.put(text_col, note);
        long result = db.insert(note_table, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public String loadNote(int parent) {
        SQLiteDatabase db = this.getReadableDatabase();
        String result = "";
        Cursor c = null;
        String query = "Select * FROM " + note_table + " where " + parent_col + " = ?";
        c = db.rawQuery(query, new String[] {String.valueOf(parent)});
        if (c.moveToFirst()) {
            result = c.getString(2);
        }
        return result;
    }

    public boolean updateNote(int parent, String note ) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = null;
        try {
            String query = "select * from " + note_table + " where " + parent_col + " = ?";
            c = db.rawQuery(query, new String[] {String.valueOf(parent)});
            if (c.getCount() > 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(text_col, note);
                int result = db.update(note_table, contentValues, "PARENT = ?", new String[] {String.valueOf(parent)});
                if (result == -1) {
                    return false;
                } else {
                    return true;
                }
            } else {
                insertNote(parent, note);
            }
        } catch (Exception e) {
            Log.e("Exception " , e.toString());
        }
        return true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS " + dbName);
        onCreate(db);
    }



}
