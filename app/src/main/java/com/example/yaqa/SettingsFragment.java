package com.example.yaqa;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.yaqa.database.QuestionSetMetadataDatabase;
import com.example.yaqa.database.ResultDatabase;
import com.example.yaqa.helper.FileHelper;
import com.example.yaqa.helper.JSONDataHelper;
import com.example.yaqa.model.QuestionSet;

import java.io.File;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(final Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_pref, rootKey);

        Preference scan_btn = (Preference) getPreferenceScreen().findPreference("scan");
        scan_btn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(getContext(), "Scanning...", Toast.LENGTH_LONG).show();
                QuestionSetMetadataDatabase.deleteAllSet();
                File coreDir = new File(Config.getCorePath());
                String[] dir_list = coreDir.list();
                for (String childDir_name : dir_list) {
                    File childDir = new File(Config.getCorePath(), childDir_name);
                    if (!childDir.isDirectory()) {
                        continue;
                    }
                    String[] file_list = childDir.list();
                    for (String secondChildDir_name : file_list) {
                        if (!secondChildDir_name.equals("data.json")) {
                            continue;
                        }
                        try {
                            QuestionSet entry = JSONDataHelper.questionParsing(FileHelper.readFromFile(childDir.toString() + "/data.json"));
                            QuestionSet metadata = new QuestionSet(entry.uuid, entry.name, entry.desc, entry.number, entry.author);
                            metadata.file_path = entry.file_path;
                            QuestionSetMetadataDatabase.addQuestionSet(metadata);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
                return true;
            }
        });
        Preference wipeResultBtn = (Preference) getPreferenceScreen().findPreference("wipe_result");
        wipeResultBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ResultDatabase.wipeAllResult();
                        Toast.makeText(getContext(), "Result wiped", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setTitle("Warning").setMessage("Do you really want to wipe your result?");
                AlertDialog dialog = builder.create();
                dialog.setInverseBackgroundForced(true);
                dialog.show();
                return true;
            }
        });

        Preference aboutBtn = (Preference) getPreferenceScreen().findPreference("about");
        aboutBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), android.R.style.Theme_Material_Dialog_Alert);
                builder.setTitle("About").setMessage("SE114.L11 Project, Made by:\nNguyen Ngoc Dang - 18520557\nHa Minh Hieu - 18520736");
                AlertDialog dialog = builder.create();
                dialog.setInverseBackgroundForced(true);
                dialog.show();
                return true;
            }
        });
    }
}
