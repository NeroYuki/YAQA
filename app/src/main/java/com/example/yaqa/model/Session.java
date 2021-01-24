package com.example.yaqa.model;

import java.util.*;

public class Session {
    private Player player;
    private ArrayList<Question> questionList;
    private Result playerResult;
    private int score = 0;
    private int correct_answer = 0;
    //TODO: Add other fields
    public boolean isMultiplayer = false;
    private int remainingLife = 1;
    private Map<Player, Result> multiplayerResult = null;
    private int questionCount = 0;

    public Session() {

    }

    public void initializeMultiplayerSession(ArrayList<Player> connectedPlayer) {
        if (connectedPlayer.size() == 0) {
            System.out.println("No connected player");
            return;
        }
        isMultiplayer = true;
        multiplayerResult = new HashMap<>();
        for (Player entry : connectedPlayer) {
            multiplayerResult.put(entry, new Result("", new Date(), 0, 0, 0 ));
        }
    }

    public void addAllQuestion(ArrayList<Question> entry) {
        if (questionList == null) {
            questionList = new ArrayList<>();
        }
        for (Question x : entry) {
            questionList.add(x);
        }
    }

    public void attachQuestion(ArrayList<Question> entry) {
        questionList = entry;
    }

    public Question getQuestion(int index) {
        return questionList.get(index);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCorrect_answer() {
        return correct_answer;
    }

    public void setCorrect_answer(int correct_answer) {
        this.correct_answer = correct_answer;
    }

    public int getQuestion_count() {
        if (questionCount == 0)
            return questionList.size();
        return questionCount;
    }

    public int getRemainingLife() {
        return remainingLife;
    }

    public void setRemainingLife(int remainingLife) {
        this.remainingLife = remainingLife;
    }

    public void shuffleQuestion() {
        Collections.shuffle(questionList);
    }

    public void setQuestion_count(int min) {
        this.questionCount = min;
    }
}
