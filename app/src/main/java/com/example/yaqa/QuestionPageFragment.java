package com.example.yaqa;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.yaqa.model.Question;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

public class QuestionPageFragment extends Fragment {
    private Question entry;
    private ArrayList<String> answer_list;
    private QuestionSlidePagerActivity parent;

    public QuestionPageFragment() {
        entry = new Question("", "", "", 0, new ArrayList<String>(3));
    }
    public QuestionPageFragment(Question entry) {

        this.entry = entry;
        this.answer_list = new ArrayList<>(4);
        answer_list.add(entry.correct_answer);
        answer_list.add(entry.wrong_answers.get(0));
        answer_list.add(entry.wrong_answers.get(1));
        answer_list.add(entry.wrong_answers.get(2));

        //shuffle the answer list
        Collections.shuffle(answer_list);

    }

    public void setParent(QuestionSlidePagerActivity p) {
        this.parent = p;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_question, container, false);
        System.out.println("Setting question: " + entry.question_string);
        TextView txtQuestion = rootView.findViewById(R.id.txt_question);
        Button btnAnswerA = rootView.findViewById(R.id.answer_a);
        Button btnAnswerB = rootView.findViewById(R.id.answer_b);
        Button btnAnswerC = rootView.findViewById(R.id.answer_c);
        Button btnAnswerD = rootView.findViewById(R.id.answer_d);
        ImageView imageView = rootView.findViewById(R.id.imageView);

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBtnAnswerClicked(view);
            }
        };

        txtQuestion.setText(entry.question_string);
        btnAnswerA.setText(answer_list.get(0));
        btnAnswerB.setText(answer_list.get(1));
        btnAnswerC.setText(answer_list.get(2));
        btnAnswerD.setText(answer_list.get(3));
        btnAnswerA.setOnClickListener(clickListener);
        btnAnswerB.setOnClickListener(clickListener);
        btnAnswerC.setOnClickListener(clickListener);
        btnAnswerD.setOnClickListener(clickListener);
        if (!entry.image_url.equals("null")) {
            File imgFile = new  File(Config.getCorePath() + entry.image_url);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }
        return rootView;
    }


    public void onBtnAnswerClicked(View view) {
        int selected_answer = -1;
        int result = 0;
        if (view.getId() == R.id.answer_a) {
            selected_answer = 0;
        }
        if (view.getId() == R.id.answer_b) {
            selected_answer = 1;
        }
        if (view.getId() == R.id.answer_c) {
            selected_answer = 2;
        }
        if (view.getId() == R.id.answer_d) {
            selected_answer = 3;
        }
        if (selected_answer == -1) return;
        if (answer_list.get(selected_answer).equals(entry.correct_answer)) {
            Toast.makeText(getContext(), "This is the correct answer :D", Toast.LENGTH_SHORT).show();
            result = 1;
        }
        else {
            Toast.makeText(getContext(), "This is the wrong answer :(", Toast.LENGTH_SHORT).show();
        }
        if (parent != null) {
            parent.onQuestionNotify(result);
        }
    }
}
