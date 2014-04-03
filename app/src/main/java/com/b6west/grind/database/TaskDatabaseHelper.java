package com.b6west.grind.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by sabrinadrammis on 4/2/14.
 */
public class TaskDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_TASK = "tasksTable";

    public static final String KEY_ID = "_id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "due_date";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_IMPORTANCE = "importance";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String KEY_COMPLETED = "completed";

    private static final String DATABASE_NAME = "tasktable.db";
    private static final int DATABASE_VERSION = 2;

    private static final String DATABASE_CREATE = "CREATE TABLE " + TABLE_TASK + " (" +
            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_TITLE + " TEXT NOT NULL, " +
            KEY_DATE + " TEXT, " +
            KEY_CATEGORY + " TEXT, " +
            KEY_IMPORTANCE + " INTEGER, " +
            KEY_DIFFICULTY + " INTEGER, " +
            KEY_COMPLETED + " INTEGER" + ");";

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        Log.w("Grind", "creating new databse");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w("Grind", "onupgrade");

        if (oldVersion == 1) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
            onCreate(db);
        }
    }
}
