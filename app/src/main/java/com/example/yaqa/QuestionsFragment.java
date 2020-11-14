package com.example.yaqa;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yaqa.adapter.QuestionSetAdapter;
import com.example.yaqa.model.QuestionSet;

import java.util.ArrayList;

public class QuestionsFragment extends Fragment {

    private RecyclerView recyclerView;
    private QuestionSetAdapter adapter;
    private ArrayList<QuestionSet> questionSetList;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questions, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        recyclerView = (RecyclerView) getView().findViewById(R.id.question_list_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        questionSetList = new ArrayList<>();
        adapter = new QuestionSetAdapter(getActivity(), questionSetList);
        recyclerView.setAdapter(adapter);
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        createListData();
    }

    private void createListData() {
        QuestionSet questionSetEntry = new QuestionSet("Earth", "a", 10, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Jupiter", "b", 26, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Mars", "c", 4, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Pluto", "d", 1, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Venus", "e", 9, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Saturn", "f", 11, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Mercury", "g", 4, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Neptune", "h", 12, "me");
        questionSetList.add(questionSetEntry);
        questionSetEntry = new QuestionSet("Uranus", "i", 9, "me");
        questionSetList.add(questionSetEntry);
        adapter.notifyDataSetChanged();
    }
}
