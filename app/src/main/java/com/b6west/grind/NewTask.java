package com.b6west.grind;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLInput.*;

import com.b6west.grind.database.TaskDatabaseHelper;

import java.util.Calendar;



/**
 * Created by mikemikael3 on 3/27/14.
 */
public class NewTask extends FragmentActivity {

    TextView TaskNamePrompt;
    EditText enterTaskName;

    TextView setDatePrompt;
    Button date;

    TextView importancePrompt;
    SeekBar importanceBar;
    int importance = 0;

    TextView difficultyPrompt;
    SeekBar difficultyBar;
    int difficulty = 0;

    Button Done;

    String dateString; //yyyy-MM-dd

    private TaskDatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private String id,title;
    private boolean isUpdate;


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtaskactivity);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        //Importance initialization
        importancePrompt = (TextView)findViewById(R.id.tvImportance);
        importanceBar = (SeekBar)findViewById(R.id.sbImportance);

        importanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                importance = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Difficulty initialization
        difficultyPrompt = (TextView)findViewById(R.id.tvDifficulty);
        difficultyBar = (SeekBar)findViewById(R.id.sbDifficulty);
        difficultyBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                difficulty = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        //Task Name Initialization
        TaskNamePrompt = (TextView)findViewById(R.id.tvEnterTaskName);
        enterTaskName = (EditText)findViewById(R.id.etTaskName);

        // Date Stuff Initialization
        setDatePrompt = (TextView)findViewById(R.id.tvSetDate);
        date = (Button)findViewById(R.id.bDate);
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectDate(view);

            }
        });


        //initialize database helper
        dbHelper = new TaskDatabaseHelper(this);

        //Done!
        Done = (Button)findViewById(R.id.bDone);
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewTask.this, TaskScreen.class);


                //Extracting the user input values
                String taskTitle = enterTaskName.getText().toString();

                Log.d("Grind", "Test: " + importanceBar.getProgress());
                Log.d("Grind", "Test: " + difficultyBar.getProgress());


                saveData(taskTitle, dateString, "", importanceBar.getProgress(), difficultyBar.getProgress(), 0);

                startActivity(intent);
            }
        });

   }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void selectDate(View view) {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getFragmentManager(), "DatePicker");
    }
    public void populateSetDate(int year, int month, int day) {
        date.setText(month + "/" + day + "/" + year);

        //update the date string, must format yyyy-MM-dd
        if (month < 10 && day < 10) {
            dateString = year + "-" + "0" + month + "-" + "0" + day;
        } else if (month < 10) {
            dateString = year + "-" + "0" + month + "-" + day;
        } else if (day < 10) {
            dateString = year + "-" + month + "-" + "0" + day;
        } else {
            dateString = year + "-" + month + "-" + day;
        }
    }


    /**
     * Save new task to SQLite
     *
     * @param title any non null title
     * @param due_date string in the format "yyyy-MM-dd"
     * @param category string of an existing category
     * @param importance range 1 to 10
     * @param difficulty range from 1 to 10
     * @param completed 0 for not completed, 1 for completed
     */
    private void saveData(String title, String due_date, String category, int importance, int difficulty, int completed){
        database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(TaskDatabaseHelper.KEY_TITLE, title);
        values.put(TaskDatabaseHelper.KEY_DATE, due_date);
        values.put(TaskDatabaseHelper.KEY_CATEGORY, category);
        values.put(TaskDatabaseHelper.KEY_IMPORTANCE, importance);
        values.put(TaskDatabaseHelper.KEY_DIFFICULTY, difficulty);
        values.put(TaskDatabaseHelper.KEY_COMPLETED, completed);


        if(isUpdate)
        {
            //update database with new data
            database.update(TaskDatabaseHelper.TABLE_TASK, values, TaskDatabaseHelper.KEY_ID + "=" + id, null);
        }
        else
        {
            //insert data into database
            database.insert(TaskDatabaseHelper.TABLE_TASK, null, values);
        }
        //close database
        database.close();
        finish();
    }


@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    public Calendar dueDate;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dueDate = Calendar.getInstance();
        int yy = dueDate.get(Calendar.YEAR);
        int mm = dueDate.get(Calendar.MONTH);
        int dd = dueDate.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, yy, mm, dd);
    }

    public void onDateSet(DatePicker view, int yy, int mm, int dd) {
        populateSetDate(yy, mm+1, dd);
    }
}

}

