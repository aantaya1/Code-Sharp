package com.aantaya.codesharp.ui.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.QuestionDifficulty;
import com.aantaya.codesharp.ui.home.RecyclerViewQuestionItem;

import java.lang.ref.WeakReference;
import java.util.List;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = QuestionsRecyclerViewAdapter.class.getSimpleName();

    List<RecyclerViewQuestionItem> questions;
    WeakReference<Context> contextWeakReference;

    public QuestionsRecyclerViewAdapter(WeakReference<Context> contextWeakReference, List<RecyclerViewQuestionItem> questions) {
        this.contextWeakReference = contextWeakReference;
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
        setDifficultyColor(holder.difficultyCircle, item.getQuestionDifficulty());
    }

    private void setDifficultyColor(ImageView imageView, QuestionDifficulty difficulty){
        switch (difficulty){
            case EASY:
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_green));
                break;
            case MEDIUM:
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_yellow));
                break;
            case HARD:
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_red));
                break;
            default:
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_yellow));
                break;
        }
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
