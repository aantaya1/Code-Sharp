package com.aantaya.codesharp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.QuestionDifficulty;
import com.aantaya.codesharp.ui.home.RecyclerViewQuestionItem;
import com.aantaya.codesharp.ui.listeners.MyItemClickListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = QuestionsRecyclerViewAdapter.class.getSimpleName();

    private List<RecyclerViewQuestionItem> questions;
    private WeakReference<Context> contextWeakReference;
    private MyItemClickListener listener;

    public QuestionsRecyclerViewAdapter(WeakReference<Context> contextWeakReference,
                                        List<RecyclerViewQuestionItem> questions,
                                        MyItemClickListener listener) {
        this.contextWeakReference = contextWeakReference;
        this.questions = questions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questions_recyclerview_item, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerViewQuestionItem item = questions.get(position);
        holder.questionTitle.setText(item.getQuestionTitle());
        setDifficultyColor(holder.difficultyCircle, item.getQuestionDifficulty());
    }

    /**
     * Private helper for setting the correct difficulty circle.
     *
     * @param imageView should be a ref to the imageview for the difficulty circle
     * @param difficulty questions difficulty
     */
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

    /**
     * For now, the only time we cannot reuse a view is when the question difficult is different,
     * since we need to redraw the difficulty circle
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return questions.get(position).getQuestionDifficulty().getCode();
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView difficultyCircle;
        TextView questionTitle;

        MyItemClickListener viewClickListener;

        ViewHolder(@NonNull View itemView, MyItemClickListener _listener) {
            super(itemView);

            difficultyCircle = itemView.findViewById(R.id.question_recyclerview_item_image);
            questionTitle = itemView.findViewById(R.id.question_recyclerview_item_title);

            viewClickListener = _listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            viewClickListener.onClick(getLayoutPosition());
        }
    }
}
