package com.b6west.grind;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.b6west.grind.database.TaskDatabaseHelper;
//import com.parse.Parse;
//import com.parse.ParseAnalytics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TaskScreen extends ActionBarActivity {
    ListView taskList;
    TextView addTaskPrompt;


    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    //list view list from SQL
    private ArrayList<Task> tasks = new ArrayList<Task>();

    public enum Order { none, title, difficulty, importance, date};

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        taskList = (ListView)findViewById(R.id.lvTaskList);

        taskList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        taskList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.task_list_context, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.menu_delete_task:

                        //Delete from data base and notify adapter of change
                }
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        addTaskPrompt = (TextView)findViewById(R.id.tvAddTaskPrompt);




        dbHelper = new TaskDatabaseHelper(this);
        displayData();

        TaskAdapter taskAdapter = new TaskAdapter(tasks);
        taskList.setAdapter(taskAdapter);



       // ArrayAdapter<Task> adapter  = new ArrayAdapter<Task>(this,android.R.layout.simple_list_item_1,tasks);
        //taskList.setAdapter(adapter);
        if(taskList.getCount()== 0){
            addTaskPrompt.setText("Add a task!");
        }
        else{
            addTaskPrompt.setHeight(0);
            addTaskPrompt.setWidth(0);
        }





        //initialize Parse
        //for analytics if we need it
       // Parse.initialize(this, "unglciIFqSiLlkBuzEpkOlE4eQhoq7FWqGDFLmaA", "Tx1sNxriLDdElnXgTKZrLZ9hN8zlOkAUBiUu3PnC");
       // ParseAnalytics.trackAppOpened(getIntent());
    }

    /**
     * displays data from SQLite
     */
    private void displayData() {
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM " + dbHelper.TABLE_TASK, null);

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            //get dateString from SQL
            String dateString = cursor.getString(cursor.getColumnIndex(dbHelper.KEY_DATE));

            if (dateString == null) {
                //add from SQL to the task list
                Task task = new Task(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                        cursor.getString(cursor.getColumnIndex(dbHelper.KEY_TITLE)),
                        cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_IMPORTANCE)),
                        cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_DIFFICULTY)));
                Log.w("Grind", task.difficulty + "");
                tasks.add(task);
            } else {
                try {
                    //convert SQL date string to Date object
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                    //make new task and add it to task list
                    Task task = new Task(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_TITLE)),
                            date,
                            cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_IMPORTANCE)),
                            cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_DIFFICULTY)));
                    tasks.add(task);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        cursor.close();
    }


    private class TaskAdapter extends ArrayAdapter<Task>{

        TextView taskTitle;
        CheckBox completed;
        TextView date;


        public TaskAdapter(ArrayList<Task> taskArrayList){
            super(TaskScreen.this, 0 ,taskArrayList );
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView== null){
                convertView = TaskScreen.this.getLayoutInflater().inflate(R.layout.task_row,null);
            }

            Task task = getItem(position);

            //Setting task title
            taskTitle = (TextView)convertView.findViewById(R.id.tvTaskTitle);
            taskTitle.setText(task.getTitle());

            //Setting task date

           date = (TextView)convertView.findViewById(R.id.tvTaskDate);
           date.setText(task.getDueDate().toString());

            return convertView;



        }
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
        if (id == R.id.action_create) {
            Intent intent = new Intent(TaskScreen.this, NewTask.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

