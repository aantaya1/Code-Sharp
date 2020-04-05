package com.aantaya.codesharp.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.listeners.MyItemClickListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder>{

    private static final String TAG = QuestionsRecyclerViewAdapter.class.getSimpleName();

    private List<RecyclerViewQuestionItem> questions = new ArrayList<>();
    private WeakReference<Context> contextWeakReference;
    private ItemClickListener listener;

    /**
     * Default constructor
     *
     * @param contextWeakReference weak reference to calling context
     * @param listener item click listener
     */
    public QuestionsRecyclerViewAdapter(WeakReference<Context> contextWeakReference,
                                        ItemClickListener listener) {
        this.contextWeakReference = contextWeakReference;
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
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_blue));
                break;
            case HARD:
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_red));
                break;
            default:
                imageView.setImageDrawable(contextWeakReference.get().getDrawable(R.drawable.circle_blue));
                break;
        }
    }

    /**
     * public method for updating the items that the recyclerview displays. Uses the DiffUtils library
     * for efficient and simple dispatching of updated to the recyclerview.
     *
     * https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
     *
     * @param newItems list of the new items we would like to display
     */
    public void updateItems(List<RecyclerViewQuestionItem> newItems){
        final QuestionsRecyclerViewAdapter.DiffUtilCallback callback = new DiffUtilCallback(this.questions, newItems);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

        this.questions.clear();
        this.questions.addAll(newItems);

        diffResult.dispatchUpdatesTo(this);
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

        ItemClickListener viewClickListener;

        ViewHolder(@NonNull View itemView, ItemClickListener _listener) {
            super(itemView);

            difficultyCircle = itemView.findViewById(R.id.question_recyclerview_item_image);
            questionTitle = itemView.findViewById(R.id.question_recyclerview_item_title);

            viewClickListener = _listener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            viewClickListener.onClick(questions.get(getLayoutPosition()), getLayoutPosition());
        }
    }


    public interface ItemClickListener{
        void onClick(RecyclerViewQuestionItem item, int position);
        void onLongClick(RecyclerViewQuestionItem item, int position);
    }

    static class DiffUtilCallback extends DiffUtil.Callback{

        private final List<RecyclerViewQuestionItem> oldList;
        private final List<RecyclerViewQuestionItem> newlist;

        public DiffUtilCallback(@NonNull List<RecyclerViewQuestionItem> oldList,
                                @NonNull List<RecyclerViewQuestionItem> newlist) {

            this.oldList = oldList;
            this.newlist = newlist;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newlist.size();
        }

        /**
         * Two items are the same if and only if they have the same id
         *
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getQuestionId().equals(newlist.get(newItemPosition).getQuestionId());
        }

        /**
         * This method is called only if areItemsTheSame returns true. In our case, if the items
         * have the same id then they will also have the same content so just return true.
         *
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    }
}
