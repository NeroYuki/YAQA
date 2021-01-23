package com.example.yaqa.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.yaqa.Config;
import com.example.yaqa.EditQuestionActivity;
import com.example.yaqa.R;
import com.example.yaqa.model.QuestionSet;

import java.util.ArrayList;
import java.util.Locale;

public class QuestionSetAdapter extends RecyclerView.Adapter<QuestionSetAdapter.QuestionSetHolder> {

    private Context context;
    private ArrayList<QuestionSet> questionSetList;
    private RecyclerView bindedRecycler = null;


    public QuestionSetAdapter(Context context, ArrayList<QuestionSet> questionSetList) {
        this.context = context;
        this.questionSetList = questionSetList;
    }

    @NonNull
    @Override
    public QuestionSetHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_set_item, parent, false);
        View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int itemPosition = bindedRecycler.getChildLayoutPosition(view);
                QuestionSet item = questionSetList.get(itemPosition);
                Toast.makeText(context, item.name, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(bindedRecycler.getContext(), EditQuestionActivity.class);
                //TODO: load different question set to edit
                intent.putExtra("editTarget", Config.getCorePath() + item.file_path + "/data.json");
                bindedRecycler.getContext().startActivity(intent);
            }
        };
        view.setOnClickListener(mOnClickListener);
        return new QuestionSetHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionSetHolder holder, int position) {
        QuestionSet qs = questionSetList.get(position);
        holder.setDetails(qs);

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        bindedRecycler = recyclerView;
    }

    @Override
    public int getItemCount() {
        return questionSetList.size();
    }

    public class QuestionSetHolder extends RecyclerView.ViewHolder {
        private TextView txtName, txtDesc, txtNumber, txtAuthor;

        QuestionSetHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.textView2);
            txtDesc = itemView.findViewById(R.id.textView3);
            txtNumber = itemView.findViewById(R.id.textView4);
            txtAuthor = itemView.findViewById(R.id.textView5);
        }

        void setDetails(QuestionSet qs) {
            txtName.setText(qs.name);
            txtDesc.setText(qs.desc);
            txtNumber.setText(qs.number + " question(s)") ;
            txtAuthor.setText("Made by " + qs.author);
        }
    }
}
