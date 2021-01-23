package com.example.yaqa.model;

import java.util.Date;

public class Result {
    public String UUID = "";
    public Date playTime;
    public int score = 0;
    public int correct_count = 0;
    public int total_count = 0;

    public Result(String UUID, Date playTime, int score, int correct_count, int total_count) {
        this.UUID = UUID;
        this.playTime = playTime;
        this.score = score;
        this.correct_count = correct_count;
        this.total_count = total_count;
    }
}
