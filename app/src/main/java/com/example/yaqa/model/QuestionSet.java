package com.example.yaqa.model;

import java.util.ArrayList;

public class QuestionSet {
    public String uuid;
    public String name;
    public String desc;
    public int number;
    public String author;
    public ArrayList<Question> questions;
    public String file_path = "";

    public QuestionSet(String uuid, String name, String desc, int number, String author) {
        this.uuid = uuid;
        this.name = name;
        this.desc = desc;
        this.number = number;
        this.author = author;
    }

    public void attachQuestions(ArrayList<Question> entries) {
        questions = entries;
    }
}
