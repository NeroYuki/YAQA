package com.example.yaqa;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.yaqa.model.Question;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static android.app.Activity.RESULT_OK;

public class QuestionEditorFragment extends Fragment {
    private Question entry = null;
    private int pos = 0;
    EditQuestionActivity parent = null;
    private ImageView imageView = null;
    private Bitmap pendingImageData = null;

    QuestionEditorFragment(Question entry, int pos) {
        this.entry = entry;
        this.pos = pos;
    }
    public final int GALLERY_REQUEST = 99;

    public void setParent(EditQuestionActivity parent) {
        this.parent = parent;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View inflated_view = inflater.inflate(R.layout.fragment_edit_question, container, false);
        final EditText questionPosText = inflated_view.findViewById(R.id.editText);
        final EditText correctAnsText = inflated_view.findViewById(R.id.editText2);
        final EditText wrongAns1Text = inflated_view.findViewById(R.id.editText3);
        final EditText wrongAns2Text = inflated_view.findViewById(R.id.editText4);
        final EditText wrongAns3Text = inflated_view.findViewById(R.id.editText5);
        TextView questionNumber = inflated_view.findViewById(R.id.textView23);
        Button saveButton = inflated_view.findViewById(R.id.button6);
        Button deleteButton = inflated_view.findViewById(R.id.button7);
        Button imageButton = inflated_view.findViewById(R.id.button5);
        imageView = inflated_view.findViewById(R.id.imageView8);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parent != null) {
                    entry.question_string = questionPosText.getText().toString();
                    entry.correct_answer = correctAnsText.getText().toString();
                    entry.wrong_answers.set(0, wrongAns1Text.getText().toString());
                    entry.wrong_answers.set(1, wrongAns2Text.getText().toString());
                    entry.wrong_answers.set(2, wrongAns3Text.getText().toString());
                    parent.onSavingQuestion(entry, pos, pendingImageData);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (parent != null) {
                    parent.onDeletingQuestion(pos);
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
            }
        });

        questionPosText.setText(entry.question_string);
        correctAnsText.setText(entry.correct_answer);
        wrongAns1Text.setText(entry.wrong_answers.get(0));
        wrongAns2Text.setText(entry.wrong_answers.get(1));
        wrongAns3Text.setText(entry.wrong_answers.get(2));
        questionNumber.setText(String.format("Question %d", pos + 1));
        if (!entry.image_url.equals("null")) {
            File imgFile = new  File(Config.getCorePath() + entry.image_url);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                imageView.setImageBitmap(myBitmap);
            }
        }
        //System.out.println(a.toString());
        return inflated_view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST:
                    Uri selectedImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        imageView.setImageBitmap(bitmap);
                        pendingImageData = Bitmap.createBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }

    }
}
