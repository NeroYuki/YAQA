package com.example.yaqa.helper;

import com.example.yaqa.model.Question;
import com.example.yaqa.model.QuestionSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.json.*;

import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class JSONDataHelper {
    public static QuestionSet questionParsing(String data) throws Exception {
        QuestionSet result = null;
        try {
            //result = gson.fromJson(data, QuestionSet.class);

            JSONObject obj = new JSONObject(data);
            result = new QuestionSet(
                    obj.getString("uuid"),
                    obj.getString("name"),
                    obj.getString("desc"),
                    obj.getInt("number"),
                    obj.getString("author"));
            result.file_path = obj.getString("file_path");
            ArrayList<Question> questionList = new ArrayList<>();
            JSONArray list = obj.getJSONArray("questions");
            for (int i = 0; i < list.length(); i++) {
                JSONObject val = list.getJSONObject(i);
                JSONArray wrong_list = val.getJSONArray("wrong_answers");
                ArrayList<String> wrong_answer = new ArrayList<>();
                for (int j = 0; j < wrong_list.length(); j++) {
                    wrong_answer.add(wrong_list.getString(j));
                }
                Question entry = new Question(
                        val.getString("question_string"),
                        val.getString("correct_answer"),
                        val.getString("image_url"),
                        val.getInt("difficulty"),
                        wrong_answer);
                questionList.add(entry);

            }
            result.attachQuestions(questionList);
        }
        catch (Exception je) {
            je.printStackTrace();
            throw new Exception("Error when parsing JSON file");
        }
        return result;
    }

    public static String saveQuestionSetToJSON(QuestionSet entry) {
        //TODO:implement
        String json = "{\"uuid\": \"0\"}";
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            json = gson.toJson(entry);

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return json;
    }
}
