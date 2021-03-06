package com.b6west.grind;

import android.annotation.TargetApi;

import android.app.ActionBar;

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
import android.util.SparseBooleanArray;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.b6west.grind.database.TaskDatabaseHelper;
//import com.parse.Parse;
//import com.parse.ParseAnalytics;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class TaskScreen extends ActionBarActivity {
    ListView taskList;
    TextView addTaskPrompt;

    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    //list view list from SQL
    private List<Task> tasks = new ArrayList<Task>();

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


        addTaskPrompt = (TextView)findViewById(R.id.tvAddTaskPrompt);


        dbHelper = new TaskDatabaseHelper(this);
        displayData();

        final TaskAdapter taskAdapter = new TaskAdapter(tasks);
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

        taskList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                taskAdapter.getItem(i).setSelected(!taskAdapter.getItem(i).selected);
                taskAdapter.notifyDataSetChanged();


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
                switch (item.getItemId()) {
                    case R.id.menu_delete_task:
                        database = dbHelper.getWritableDatabase();
                        for (int i = taskAdapter.getCount(); i >= 0; i--) {

                            if (taskList.isItemChecked(i)) {
                                Task task = tasks.get(i);
                                database.delete(dbHelper.TABLE_TASK, dbHelper.KEY_ID + "=" + task.getId(), null);
                                tasks.remove(i);
                            }
                        }
                        taskAdapter.clear();
                        taskAdapter.addAll(tasks);
                        Log.w("Grind", tasks.toString());
                        taskAdapter.notifyDataSetChanged();

                        actionMode.finish();
                        return true;
                    default:
                        return false;     //Delete from data base and notify adapter of change
                }


            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {


                //for (int i = taskAdapter.getCount(); i >=0; i--){

                // taskAdapter.getItem(i).setSelected(false);
                //}
                //taskAdapter.notifyDataSetChanged();
                //actionMode.finish();


            }
        });

        // SPINNER
        SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.action_list, android.R.layout.simple_spinner_dropdown_item);

        ActionBar.OnNavigationListener mOnNavigationListener = new ActionBar.OnNavigationListener() {
            // Get the same strings provided for the drop-down's ArrayAdapter
            String[] strings = getResources().getStringArray(R.array.action_list);

            @Override
            public boolean onNavigationItemSelected(int position, long itemId) {
                // Create new fragment from our own Fragment class

                String query = "";
                switch(position) {
                    case 0:
                        query = "SELECT * FROM " + dbHelper.TABLE_TASK;
                        tasks = getTasksFromQuery(query);
                        break;
                    case 1:
                        query = "SELECT * FROM " + dbHelper.TABLE_TASK + " ORDER BY " + dbHelper.KEY_DATE + " DESC";
                        tasks = getTasksFromQuery(query);

                        Collections.sort(tasks, new Comparator<Task>() {
                            @Override
                            public int compare (Task t1, Task t2) {
                                if (t1.getDueDate() != null && t2.getDueDate() != null ) {
                                   return t1.getDueDate().compareTo(t2.getDueDate());
                                } else if (t1.getDueDate() == null && t2.getDueDate() == null) {
                                   return 0; // t1 and t2 are equal
                                } else if (t1.getDueDate() == null) { // t2's date is NOT null
                                    return 1; // t2 sorts earlier in the list, t2 < t1
                                } else { // t2's date is null
                                    return -1; // t1 sorts earlier in the list, t1 < t2
                                }
                            }
                        });

                        break;
                    case 2:
                        query = "SELECT * FROM " + dbHelper.TABLE_TASK;
                        tasks = getTasksFromQuery(query);

                        Collections.sort(tasks, new Comparator<Task>() {
                        @Override
                            public int compare (Task t1, Task t2) {
                                // sort by DESC order
                                if (t1.getScore() > t2.getScore() ) {
                                    return -1; // put t1 earlier in the list
                                } else if ( t1.getScore() < t2.getScore() ) {
                                    return 1; // put t2 earlier in the list
                                } else {
                                    return 0; // equal scores
                                }
                            }
                        });

                        break;
                    case 3:
                        query = "SELECT * FROM " + dbHelper.TABLE_TASK + " ORDER BY " + dbHelper.KEY_IMPORTANCE + " DESC";
                        tasks = getTasksFromQuery(query);
                        break;
                    case 4:
                        query = "SELECT * FROM " + dbHelper.TABLE_TASK + " ORDER BY " + dbHelper.KEY_DIFFICULTY + " DESC";
                        tasks = getTasksFromQuery(query);
                        break;
                    default:
                }

                Collections.sort(tasks, new Comparator<Task>() {
                    @Override
                    public int compare (Task t1, Task t2) {
                        if (t1.completed && !t2.completed) {
                            return 1;
                        } else if ( !t1.completed && t2.completed ) {
                            return -1;
                        } else {
                            return 0;
                        }
                    }
                });

                taskAdapter.clear();
                taskAdapter.addAll(tasks);
                taskAdapter.notifyDataSetChanged();


                /////// log to the CSV file ///////////////////
                String FILENAME = "grindSort.csv";
                String entry = position + "\n";
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

                return true;
            }

        };
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
    }

    public List<Task> getTasksFromQuery(String query) {
        database = dbHelper.getWritableDatabase();
        Cursor cursor = database.rawQuery(query, null);
        List<Task> filterTaskList = new ArrayList<Task>();

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
                filterTaskList.add(task);
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
                    filterTaskList.add(task);
                    int i = task.completed ? 1 : 0;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        cursor.close();

        return filterTaskList;
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

        public TaskAdapter(List<Task> taskArrayList){
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
                convertView = TaskScreen.this.getLayoutInflater().inflate(R.layout.list_item_task, null);


                holder = new ViewHolder();
                //working with the checkbox
                holder.taskTitle = (TextView) convertView.findViewById(R.id.tvTaskTitle);
                holder.taskCB = (CheckBox) convertView.findViewById(R.id.cbCompleted);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            final int pos = position;


            holder.taskCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Task selectedTask = tasks.get(pos);
                    selectedTask.setCompleted(isChecked);
                    selectedTask.calculateScore();

                    int completed = isChecked? 1 : 0;
                    //update database
                    database = dbHelper.getWritableDatabase();

                    ContentValues data = new ContentValues();
                    data.put(dbHelper.KEY_COMPLETED, completed);
                    database.update(dbHelper.TABLE_TASK, data, dbHelper.KEY_ID + "=" + selectedTask.getId(), null);
                    database.close();

                    notifyDataSetChanged();
                }
            });

            task = getItem(position);

            if (convertView != null) {
                int score = task.getScore();
                if(task.selected){
                    convertView.setBackgroundColor(Color.parseColor("#8A8282")); //grey
                }
                else if (score > 25){
                        convertView.setBackgroundColor(Color.parseColor("#ff5f4a")); //dark red
                }
                else if(score > 23){
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


    @TargetApi(Build.VERSION_CODES.FROYO)
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

