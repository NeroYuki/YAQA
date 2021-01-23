package com.example.yaqa;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.EditTextPreference;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.example.yaqa.database.QuestionSetMetadataDatabase;
import com.example.yaqa.model.QuestionSet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class GamerulesFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.gamerule_pref, rootKey);

        EditTextPreference max_question = (EditTextPreference) getPreferenceScreen().findPreference("max_question_count");
        MultiSelectListPreference selected_sets = (MultiSelectListPreference) getPreferenceScreen().findPreference("selected_sets");
        EditTextPreference time_limit = (EditTextPreference) getPreferenceScreen().findPreference("time_limit");
        EditTextPreference lives_count = (EditTextPreference) getPreferenceScreen().findPreference("lives_count");

        ArrayList<QuestionSet> setList = QuestionSetMetadataDatabase.getAllSetMetadata();
        String[] nameList = new String[setList.size()];
        String[] idList = new String[setList.size()];
        for (int i = 0; i < setList.size(); i++) {
            System.out.println(setList.get(i).name + "-" + setList.get(i).uuid);
            nameList[i] = setList.get(i).name;
            idList[i] = setList.get(i).uuid;
        }

        selected_sets.setEntries(nameList);
        selected_sets.setEntryValues(idList);

        Set<String> available = new HashSet<>();
        ArrayList<String> filtered = new ArrayList<>();
        for (int i = 0; i < idList.length; i++) {
            available.add(idList[i]);
        }
        for (String x: selected_sets.getValues()) {
            if (available.contains(x)) {filtered.add(x);}
        }

        selected_sets.setValues(new HashSet<String>(filtered));

        max_question.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean res = true;
                if (!newValue.toString().matches("[\\d]+")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Invalid Input");
                    builder.setMessage("Must be number");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    res = false;
                    return res;
                }

                int intValue = Integer.parseInt(newValue.toString());
                if (intValue < 0 || intValue > 50) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Lives count must be between 0 and 50");
                    builder.setTitle("Invalid input");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    res = false;
                    return res;
                }
                return res;
            }
        });

        lives_count.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean res = true;
                if (!newValue.toString().matches("[\\d]+")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Must be number");
                    builder.setTitle("Invalid input");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    res = false;
                    return res;
                }
                int intValue = Integer.parseInt(newValue.toString());
                if (intValue < 0 || intValue > 50) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Lives count must be between 0 and 50");
                    builder.setTitle("Invalid input");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    res = false;
                    return res;
                }
                return res;
            }
        });

        time_limit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean res = true;
                if (!newValue.toString().matches("[\\d]{2}:[\\d]{2}")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Invalid Input");
                    builder.setMessage("Must be in MM:SS format");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                    res = false;
                }
                return res;
            }
        });
    }
}
