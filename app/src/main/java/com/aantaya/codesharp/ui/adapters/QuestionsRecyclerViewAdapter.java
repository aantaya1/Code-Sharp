package com.aantaya.codesharp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.ui.home.RecyclerViewQuestionItem;

import java.util.List;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder>{

    List<RecyclerViewQuestionItem> questions;

    public QuestionsRecyclerViewAdapter(List<RecyclerViewQuestionItem> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questions_recyclerview_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerViewQuestionItem item = questions.get(position);
        holder.questionTitle.setText(item.getQuestionTitle());
        //todo: set difficulty background color
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView difficultyCircle;
        public TextView questionTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            difficultyCircle = itemView.findViewById(R.id.question_recyclerview_item_image);
            questionTitle = itemView.findViewById(R.id.question_recyclerview_item_title);
        }
    }
}
