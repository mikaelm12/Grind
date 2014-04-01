package com.b6west.grind;

import java.util.Date;

/**
 * Created by mikemikael3 on 3/27/14.
 */
public class Task {

    public String title;
    public int difficulty = 0;
    public int importance = 0;
    public String catagory;
    public Date dueDate;
    public int Score;


    public Task(String title, Date date, int importance, int difficulty){
        this.title = title;
        this.difficulty = difficulty;
        this.importance = importance;
        this.dueDate = date;
    }

    public Task(String title, int importance, int difficulty){
        this.title = title;
        this.importance = importance;
        this.difficulty = difficulty;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public String getCatagory() {
        return catagory;
    }

    public void setCatagory(String catagory) {
        this.catagory = catagory;
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

    public void calculateScore(){

    }

    public String toString() {
        return "Title: " + title + " difficulty: " + difficulty + " importance: " + importance;
    }


}
