package com.b6west.grind;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.b6west.grind.database.TaskDatabaseHelper;

import java.util.ArrayList;

public class TaskScreen extends ActionBarActivity {
    ListView taskList;
    TextView addTaskPrompt;
    Button addTask;

    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private ArrayList<String> taskID = new ArrayList<String>();
    private ArrayList<String> taskTitle = new ArrayList<String>();

    public enum Order { none, title, difficulty, importance, date};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        taskList = (ListView)findViewById(R.id.lvTaskList);

        addTaskPrompt = (TextView)findViewById(R.id.tvAddTaskPrompt);

        addTask = (Button)findViewById(R.id.bAddTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskScreen.this, NewTask.class);
                startActivity(intent);
            }
        });

        if(taskList.getCount()== 0){
           addTaskPrompt.setEnabled(true);
        }

        dbHelper = new TaskDatabaseHelper(this);
        displayData();

        //initialize Parse
        //probably not going to use Parse at allf
//        Parse.initialize(this, "unglciIFqSiLlkBuzEpkOlE4eQhoq7FWqGDFLmaA", "Tx1sNxriLDdElnXgTKZrLZ9hN8zlOkAUBiUu3PnC");
    }

    /**
     * displays data from SQLite
     */
    private void displayData() {
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + dbHelper.TABLE_TASK, null);


        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            Log.w("Grind", cursor.getString(cursor.getColumnIndex(dbHelper.KEY_ID)) + " , " +
                    cursor.getString(cursor.getColumnIndex(dbHelper.KEY_TITLE)) + " , " +
                    cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_DIFFICULTY)) + " , " +
                    cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_IMPORTANCE)) + " , ");
        }

        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.task_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

