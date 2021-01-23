package com.example.yaqa.model;

import java.util.ArrayList;

public class Question {
    public String question_string;
    public String correct_answer;
    public String image_url;
    public int difficulty;
    public ArrayList<String> wrong_answers;

    public Question(String question_string, String correct_answer, String image_url, int difficulty, ArrayList<String> wrong_answers) {
        this.question_string = question_string;
        this.correct_answer = correct_answer;
        this.image_url = image_url;
        this.difficulty = difficulty;
        this.wrong_answers = wrong_answers;
    }
}
