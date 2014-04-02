package com.b6west.grind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sabrinadrammis on 3/29/14.
 */
public class TasksDB {

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "due_date";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_IMPORTANCE = "importance";
    public static final String KEY_DIFFICULTY = "difficulty";
    public static final String KEY_COMPLETED = "completed";


    private static final String DATABASE_NAME = "com.b6west.grind.TasksDB";
    private static final String DATABASE_TABLE = "tasksTable";
    private static final int DATABASE_VERSION = 1;


    private DBHelper dbHelper;
    private final Context context;
    private SQLiteDatabase database;


    private static class DBHelper extends SQLiteOpenHelper {

        private DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    KEY_TITLE + " TEXT NOT NULL, " +
                    KEY_DATE + " TEXT, " +
                    KEY_CATEGORY + " TEXT, " +
                    KEY_IMPORTANCE + " INTEGER, " +
                    KEY_DIFFICULTY + " INTEGER, " +
                    KEY_COMPLETED + " INTEGER" + ");"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(sqLiteDatabase);
        }
    }

    public TasksDB (Context c) {
        context = c;
    }

    public TasksDB open() throws SQLException{
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public long createEntry(String taskTitle, String category, String date, int importance, int difficulty) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(KEY_TITLE, taskTitle);
        contentValues.put(KEY_CATEGORY, category);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_IMPORTANCE, importance);
        contentValues.put(KEY_DIFFICULTY, difficulty);
        return database.insert(DATABASE_TABLE, null, contentValues);
    }


    public List<Task> getData(TaskScreen.Order order) {
        String[] columns = new String[]{ KEY_TITLE, KEY_CATEGORY, KEY_DATE, KEY_IMPORTANCE, KEY_DIFFICULTY};
        List<Task> fetch = new ArrayList<Task>();

        Cursor cursor = null;

        switch (order) {
            case none:
                cursor = database.query(DATABASE_TABLE, columns, null, null, null, null, null);
                break;
            case title:
                cursor = database.query(DATABASE_TABLE, columns, null, null, null, null, KEY_TITLE);
                break;
            case date:
                break;
            case difficulty:
                break;
            case importance:
                break;
            default:
                cursor = database.query(DATABASE_TABLE, columns, null, null, null, null, null);
                break;
        }

        int iID = cursor.getColumnIndex(KEY_ID);
        int iTitle = cursor.getColumnIndex(KEY_TITLE);
        int iCategory= cursor.getColumnIndex(KEY_CATEGORY);
        int iDate = cursor.getColumnIndex(KEY_DATE);
        int iDifficulty = cursor.getColumnIndex(KEY_DIFFICULTY);
        int iImportance = cursor.getColumnIndex(KEY_IMPORTANCE);


        //do these for everything

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //add to data
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(cursor.getString(iDate), null);

            Task task = new Task(cursor.getInt(iID), cursor.getString(iTitle), date, cursor.getInt(iImportance), cursor.getInt(iDifficulty));
            fetch.add(task);
        }
        Log.w(fetch.toString() + "fetch", "Grind");

        return fetch;
    }

    public boolean deleteEntry(int id)
    {
        return database.delete(DATABASE_TABLE, KEY_ID + "=" + id, null) > 0;
    }
}
