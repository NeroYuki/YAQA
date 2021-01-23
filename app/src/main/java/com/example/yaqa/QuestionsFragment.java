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

    public QuestionsFragment() {
        questionSetList = new ArrayList<>();
    }

    public QuestionsFragment(ArrayList<QuestionSet> entry) {
        questionSetList = new ArrayList<>();
        for (QuestionSet element : entry) {
            questionSetList.add(element);
        }
    }
    
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
        adapter = new QuestionSetAdapter(getActivity(), questionSetList);
        recyclerView.setAdapter(adapter);
        // recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        createListData();


    }

    private void createListData() {
        if (questionSetList.size() == 0) {
            QuestionSet questionSetEntry = new QuestionSet("", "Example Set", "This is an example set", 4, "Guest");
            questionSetList.add(questionSetEntry);
        }
        adapter.notifyDataSetChanged();
    }
}
