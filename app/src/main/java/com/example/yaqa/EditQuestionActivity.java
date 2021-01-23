package com.example.yaqa;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yaqa.database.QuestionSetMetadataDatabase;
import com.example.yaqa.helper.FileHelper;
import com.example.yaqa.helper.JSONDataHelper;
import com.example.yaqa.model.Question;
import com.example.yaqa.model.QuestionSet;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class EditQuestionActivity extends AppCompatActivity {

    QuestionSet editingSet = null;
    NavigationView navigationView = null;
    EditQuestionActivity self = null;
    String editFilePath = "";

    public EditQuestionActivity() {
        //Create new question set
        editingSet = new QuestionSet(UUID.randomUUID().toString(), "Title", "Description", 0, "");
        editingSet.attachQuestions(new ArrayList<Question>());
    }

    private void loadQuestionSetFromFile(String filename) throws Exception {
        editFilePath = filename;
        try {
            if (filename.startsWith("assets"))
                editingSet = JSONDataHelper.questionParsing(FileHelper.readFromInputStream(getApplicationContext().getAssets().open("ExampleSet.json")));
            else
                editingSet = JSONDataHelper.questionParsing(FileHelper.readFromFile(filename));
            editingSet.file_path = filename.replace("/data.json","").replace(Config.getCorePath(), "");
        }
        catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    private void saveQuestionSetToFile(String filename) {
        try {
            if (editFilePath.equals("")) {
                //creating new file here
                editingSet.file_path = "/" + editingSet.uuid + "/";
                File pendingNewDir = new File(Config.getCorePath() + editingSet.file_path);
                if (!pendingNewDir.mkdirs()) {
                    System.out.println("Directory creation failed");
                    return;
                }
                FileHelper.writeToFile(pendingNewDir.getAbsolutePath() + "/data.json", JSONDataHelper.saveQuestionSetToJSON(editingSet));
                QuestionSet metadata = new QuestionSet(editingSet.uuid, editingSet.name, editingSet.desc, editingSet.number, editingSet.author);
                metadata.file_path = editingSet.file_path;
                QuestionSetMetadataDatabase.addQuestionSet(metadata);
                editFilePath = pendingNewDir.getAbsolutePath() + "/data.json";

                Toast.makeText(this, "Successfully created new question set", Toast.LENGTH_LONG).show();
            }
            else {
                //getApplicationContext().getAssets().
                FileHelper.writeToFile(editFilePath, JSONDataHelper.saveQuestionSetToJSON(editingSet));
                QuestionSet metadata = new QuestionSet(editingSet.uuid, editingSet.name, editingSet.desc, editingSet.number, editingSet.author);
                metadata.file_path = editingSet.file_path;
                QuestionSetMetadataDatabase.updateSelectedQuestionSet(metadata);
            }
            Toast.makeText(this, "Successfully saved question set", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        editingSet.author = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("username", "Unknown");
        final String editTarget = this.getIntent().getStringExtra("editTarget");
        try {
            if (editTarget != null) {
                loadQuestionSetFromFile(editTarget);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        self = this;

        navigationView = (NavigationView) findViewById(R.id.questions_nav_view);
        //navigationView.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();
                System.out.println("Edit question navigation: enter " + id);
                if (id == 0) {
                    //navigationView.getMenu().removeItem(navigationView.getMenu().add("").getItemId());
                    addNewQuestion();
                    id = editingSet.number;
                }
                QuestionEditorFragment selectedFragment = new QuestionEditorFragment(editingSet.questions.get(id - 1), id - 1);
                selectedFragment.setParent(self);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.replace(R.id.question_fragment_container, selectedFragment, "root").commit();

                return true;
            }
        });
        reloadQuestionList();

    }

    public void addNewQuestion() {
        editingSet.questions.add(new Question("", "", "null", 0, new ArrayList<String>(Arrays.asList("", "", ""))));
        editingSet.number += 1;

        reloadQuestionList();
        reloadSetInfo();
    }

    public void onSavingQuestion(Question entry, int pos, Bitmap pendingImageData) {

        if (pendingImageData != null) {
            if (editFilePath.equals("")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error")
                        .setMessage("Please save your newly created question set before adding image");
                builder.create().show();
            }
            else {
                //attempt to copy image into question set directory
                File dest = null;
                File append = null;
                if (entry.image_url.equals("null")) {
                    append = new File(editingSet.file_path,UUID.randomUUID().toString() + ".png");
                    dest = new File(Config.getCorePath(), append.toString());
                }
                else {
                    dest = new File(Config.getCorePath(), entry.image_url);
                }
                boolean res = FileHelper.writePNGtoFile(dest.toString(), pendingImageData);
                if (res && entry.image_url.equals("null")) {
                    entry.image_url = append.toString();
                }
            }
        }

        editingSet.questions.set(pos, entry);
        Toast.makeText(getApplicationContext(), "Question Saved", Toast.LENGTH_SHORT).show();

    }

    public void onDeletingQuestion(int pos) {
        final int buffer_pos = pos;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to delete this question?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                transaction.remove(getSupportFragmentManager().findFragmentByTag("root")).commit();
                editingSet.questions.remove(buffer_pos);
                Toast.makeText(getApplicationContext(), "Question removed", Toast.LENGTH_SHORT).show();
                editingSet.number -= 1;
                reloadQuestionList();
                reloadSetInfo();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void reloadQuestionList() {
        //update navigationView
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.question_navigation);
        Menu nav_menu = navigationView.getMenu();
        nav_menu.add(0, 0, 0, "Add a new question");
        nav_menu.getItem(0).setIcon(R.drawable.ic_add_black_24dp);
        for (int i = 0; i < editingSet.number; i++) {
            navigationView.getMenu().add(0, i + 1, i + 1, String.format("Question %d", i + 1));
        }
    }

    public void reloadSetInfo() {
        TextView txtTitle = findViewById(R.id.textView7);
        TextView txtQuestionCount = findViewById(R.id.textView13);
        txtTitle.setText(editingSet.name);
        txtQuestionCount.setText(String.format("%d Question(s)", editingSet.number));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.question_edit_menu, menu);
        reloadSetInfo();

        return true;
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                EditQuestionActivity.super.onBackPressed();
            }
        }).setNegativeButton("Keep editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setTitle("Confirm").setMessage("There are unsaved changes, what do you want to do?");
        builder.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.btnEdit:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                View inflated_view = inflater.inflate(R.layout.dialog_question_set_edit, null);
                final EditText editTextName = inflated_view.findViewById(R.id.editText6);
                final EditText editTextDesc = inflated_view.findViewById(R.id.editText7);
                final EditText editTextAuthor = inflated_view.findViewById(R.id.editText8);
                editTextName.setText(editingSet.name);
                editTextDesc.setText(editingSet.desc);
                editTextAuthor.setText(editingSet.author);

                builder.setView(inflated_view)
                        // Add action buttons
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                editingSet.name = editTextName.getText().toString();
                                editingSet.desc = editTextDesc.getText().toString();
                                editingSet.author = editTextAuthor.getText().toString();
                                Toast.makeText(getApplicationContext(), "Saved question set info", Toast.LENGTH_SHORT);
                                reloadSetInfo();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
                break;
            case R.id.btnSave:
                saveQuestionSetToFile(editFilePath);
                break;
            case R.id.btnDelete:
                //TODO: implement this
                break;
            default:
                System.out.println("Unknown option");
        }
        return true;
    }
}
