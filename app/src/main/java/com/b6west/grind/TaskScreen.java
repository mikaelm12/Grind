package com.b6west.grind;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.ViewGroup;
import android.widget.AbsListView;

import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.b6west.grind.database.TaskDatabaseHelper;
import com.parse.Parse;
import com.parse.ParseAnalytics;
//import com.parse.Parse;
//import com.parse.ParseAnalytics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_screen);

        /////// log to the CSV file ///////////////////
        String FILENAME = "grindOpens.csv";

        //get today's date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        //today's date, task title, due date, importance, difficulty, isUpdate
        String entry = dateFormat.format(cal.getTime()) + "\n";

        File externalDir = getExternalFilesDir(null);
        String filePath = externalDir + "/" + FILENAME;
        File file = new File(externalDir, FILENAME);

        try {
            FileWriter fileWriter = new FileWriter(file,true);
            //Use BufferedWriter instead of FileWriter for better performance
            BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
            fileWriter.append(entry);
            //Don't forget to close Streams or Reader to free FileDescriptor associated with it
            bufferFileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////

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
            addTaskPrompt.setText("    Add a task!");
        }
        else{
            addTaskPrompt.setHeight(0);
            addTaskPrompt.setWidth(0);
        }

        //Click to update data
        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Task selectedTask = (Task) parent.getItemAtPosition(position);
                if (!selectedTask.completed) {
                    Intent intent = new Intent(TaskScreen.this, NewTask.class);
                    intent.putExtra("update", true);
                    intent.putExtra("id", selectedTask.getId());
                    intent.putExtra("title", selectedTask.getTitle());
                    if (selectedTask.getDueDate() != null) { intent.putExtra("dueDate", selectedTask.getDueDate()); }
                    intent.putExtra("importance", selectedTask.getImportance());
                    intent.putExtra("difficulty", selectedTask.getDifficulty());
                    startActivity(intent);
                }
            }
        });


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
                        cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_DIFFICULTY)),
                        cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_COMPLETED)));
                tasks.add(task);
                int i = task.completed ? 1 : 0;
            } else {
                try {
                    //convert SQL date string to Date object
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                    //make new task and add it to task list
                    Task task = new Task(cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_ID)),
                            cursor.getString(cursor.getColumnIndex(dbHelper.KEY_TITLE)),
                            date,
                            cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_IMPORTANCE)),
                            cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_DIFFICULTY)),
                            cursor.getInt(cursor.getColumnIndex(dbHelper.KEY_COMPLETED)));
                    tasks.add(task);
                    int i = task.completed ? 1 : 0;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        cursor.close();
    }


    private class TaskAdapter extends ArrayAdapter<Task>{

        TextView taskTitle;
        CheckBox checkBox;
        TextView date;
        Task task;

        public TaskAdapter(ArrayList<Task> taskArrayList){
            super(TaskScreen.this, 0 ,taskArrayList );
        }

        private class ViewHolder {
            TextView taskTitle;
            CheckBox taskCB;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;

            if (convertView == null) {
                holder = new ViewHolder();
                convertView = TaskScreen.this.getLayoutInflater().inflate(R.layout.task_row, null);

                holder = new ViewHolder();
                //working with the checkbox
                holder.taskTitle = (TextView) convertView.findViewById(R.id.tvTaskTitle);
                holder.taskCB = (CheckBox) convertView.findViewById(R.id.cbCompleted);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final int pos = position;
            holder.taskTitle.setText(tasks.get(position).getTitle());
            holder.taskCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Task selectedTask = tasks.get(pos);
                    selectedTask.setCompleted(isChecked);
                    selectedTask.calculateScore();

                    int completed = isChecked? 1 : 0;
                    //update database
                    database = dbHelper.getWritableDatabase();

                    ContentValues data = new ContentValues();
                    data.put(TaskDatabaseHelper.KEY_COMPLETED,completed);
                    database.update(TaskDatabaseHelper.TABLE_TASK, data, "_id=" + selectedTask.getId(), null);
                    database.close();

                    notifyDataSetChanged();
                }
            });

            task = getItem(position);

            if (convertView != null) {
                int score = task.getScore();
                if (score > 27){
                        convertView.setBackgroundColor(Color.parseColor("#ff5f4a")); //dark red
                }
                else if(score > 25){
                        convertView.setBackgroundColor(Color.parseColor("#fd8a67")); //orange red
                }
                 else if(score > 20 ){
                        convertView.setBackgroundColor(Color.parseColor("#FFB384"));
                }
                 else if (score > 15) {
                        convertView.setBackgroundColor(Color.parseColor("#FFD3AD"));
                }
                  else if (score > 10){
                        convertView.setBackgroundColor(Color.parseColor("#FFEAC7"));
                }
                 else if (score >= 0){
                        convertView.setBackgroundColor(Color.TRANSPARENT);
                }
                  else{
                        convertView.setBackgroundColor(Color.parseColor("#66cc66"));
                }

            }

            //Setting task title
            taskTitle = (TextView)convertView.findViewById(R.id.tvTaskTitle);
            taskTitle.setText(task.getTitle());

            //Setting task date
            date = (TextView)convertView.findViewById(R.id.tvTaskDate);
            if (task.getDueDate() == null) {
                date.setText("");
            } else {
                DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.FULL);
                date.setText(dateFormatter.format(task.getDueDate()));
            }


            //set checkbox as checked
            checkBox = (CheckBox) convertView.findViewById(R.id.cbCompleted);
            if (task.getCompleted()) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }

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
            intent.putExtra("update", false);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        /////// log to the CSV file ///////////////////
        String FILENAME = "grindOpens.csv";

        //get today's date
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        //today's date, task title, due date, importance, difficulty, isUpdate
        String entry = dateFormat.format(cal.getTime()) + "\n";

        File externalDir = getExternalFilesDir(null);
        String filePath = externalDir + "/" + FILENAME;
        File file = new File(externalDir, FILENAME);

        try {
            FileWriter fileWriter = new FileWriter(file,true);
            //Use BufferedWriter instead of FileWriter for better performance
            BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
            fileWriter.append(entry);
            //Don't forget to close Streams or Reader to free FileDescriptor associated with it
            bufferFileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //////////////////////////////////////////////
    }
}

