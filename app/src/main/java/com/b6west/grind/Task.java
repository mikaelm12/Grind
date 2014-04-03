package com.b6west.grind;

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
    public int Score;


    public Task(int id, String title, Date date, int importance, int difficulty){
        this.id = id;
        this.title = title;
        this.difficulty = difficulty;
        this.importance = importance;
        this.dueDate = date;
    }

    public Task(int id, String title, int importance, int difficulty){
        this.id = id;
        this.title = title;
        this.importance = importance;
        this.difficulty = difficulty;
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

    public void calculateScore(){

    }

    public String toString() {
        return "Title: " + title + " duedate: " + dueDate + " difficulty: " + difficulty + " importance: " + importance;
    }


}
