package com.b6west.grind;

import android.util.Log;

import java.util.Date;

/**
 * Created by mikemikael3 on 3/27/14.
 */
public class Task {

    public int id;
    public String title;
    public int difficulty = 0;
    public int importance = 0;
    public String category;
    public Date dueDate;
    public int score;
    public boolean completed = false;


    public Task(int id, String title, Date date, int importance, int difficulty){
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.importance = importance;
        this.dueDate = date;
        calculateScore(); //completed is false for all new entries
    }

    public Task(int id, String title, int importance, int difficulty){
        this.id = id;
        this.title = title;
        this.importance = importance;
        this.difficulty = difficulty;
        calculateScore(); //completed is false for all new entries
    }

    public int getId() { return id; }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public int getScore() { return score; }

    public boolean getCompleted() { return false; }

    public void setCompleted(boolean completed) { this.completed = completed; }

    public void calculateScore(){
        int dueDateScore = 0;
        if (completed) {
            score = -1;
        } else {
            Log.w("Grind", "task date: " + this.getDueDate());

            if (this.getDueDate() == null ) {
                score = 0;
            } else {
                Log.w("Grind", "task imp: " + importance);
                long diffInMillisec = (new Date()).getTime() - this.getDueDate().getTime();

                long diffInDays = (diffInMillisec / (24 * 60 * 60 * 1000));

               //Creating different cases , < 1 day away < 3 days away < 7 days < 15days < 30days
                if (diffInDays < 1){
                    dueDateScore = 10;
                }
                else if(diffInDays < 3){
                    dueDateScore = 8;
                }

                else if(diffInDays < 7){
                    dueDateScore = 5;
                }
                else if(dueDateScore < 30){
                    dueDateScore = 3;

                }

                else{
                    dueDateScore = 1;
                }


                score = importance + difficulty + dueDateScore;
            }
        }
        Log.w("Grind", score + " : score");
    }


    public String toString() {
        return "Title: " + title + " duedate: " + dueDate + " difficulty: " + difficulty + " importance: " + importance;
    }


}
