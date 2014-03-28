package com.b6west.grind;

import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

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

    TextView difficultyPrompt;
    SeekBar difficultyBar;

    Button bDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.addtaskactivity);

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

    //Importance initialization
        importancePrompt = (TextView)findViewById(R.id.tvImportance);
        importanceBar = (SeekBar)findViewById(R.id.sbImportance);
        importanceBar.setMax(10);
        importanceBar.setProgress(2);

    // Difficulty initialization
        difficultyPrompt = (TextView)findViewById(R.id.tvDifficulty);
        difficultyBar = (SeekBar)findViewById(R.id.sbDifficulty);
        difficultyBar.setMax(10);
        difficultyBar.setProgress(3);

    //Done!
        bDate = (Button)findViewById(R.id.bDone);
        bDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewTask.this, TaskScreen.class);


                //Extracting the user input values

                String taskTitle = enterTaskName.getText().toString();

                int importance = importanceBar.getProgress();
                int difficulty = difficultyBar.getProgress();

                // public Task(String title, Date date, int importance, int difficulty){
                Task task = new Task(taskTitle, dueDate, importance, difficulty);


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

