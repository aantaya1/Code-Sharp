package com.aantaya.codesharp.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class QuestionsRecyclerViewAdapter extends RecyclerView.Adapter<QuestionsRecyclerViewAdapter.ViewHolder> implements Filterable {

    private static final String TAG = QuestionsRecyclerViewAdapter.class.getSimpleName();

    private List<RecyclerViewQuestionItem> mQuestions = new ArrayList<>();
    private List<RecyclerViewQuestionItem> mQuestionsPrevious = new ArrayList<>();
    private WeakReference<Context> mContextWeakReference;
    private ItemClickListener mItemClickListener;
    private boolean mFiltering = false;

    /**
     * Default constructor
     *
     * @param mContextWeakReference weak reference to calling context
     * @param mItemClickListener item click listener
     */
    public QuestionsRecyclerViewAdapter(WeakReference<Context> mContextWeakReference,
                                        ItemClickListener mItemClickListener) {
        this.mContextWeakReference = mContextWeakReference;
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.questions_recyclerview_item, parent, false);
        return new ViewHolder(view, mItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerViewQuestionItem item = mQuestions.get(position);
        holder.questionTitle.setText(item.getQuestionTitle());
        setDifficultyColor(holder.difficultyCircle, item.getQuestionDifficulty());
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
        return mQuestions.get(position).getQuestionDifficulty().getCode();
    }

    @Override
    public int getItemCount() {
        return mQuestions.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<RecyclerViewQuestionItem> res = new ArrayList<>();

                if (charSequence == null || charSequence.length() == 0){
                    res.addAll(mQuestionsPrevious);
                }else {
                    String input = charSequence.toString().toLowerCase();

                    for (RecyclerViewQuestionItem item : mQuestions){
                        if (item.getQuestionTitle().toLowerCase().contains(input)){
                            res.add(item);
                        }
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = res;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mQuestions.clear();
                mQuestions.addAll((List<RecyclerViewQuestionItem>) filterResults.values);
                notifyDataSetChanged();
            }
        };
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
                imageView.setImageDrawable(mContextWeakReference.get().getDrawable(R.drawable.circle_easy));
                break;
            case MEDIUM:
                imageView.setImageDrawable(mContextWeakReference.get().getDrawable(R.drawable.circle_medium));
                break;
            case HARD:
                imageView.setImageDrawable(mContextWeakReference.get().getDrawable(R.drawable.circle_hard));
                break;
            default:
                imageView.setImageDrawable(mContextWeakReference.get().getDrawable(R.drawable.circle_medium));
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
        final QuestionsRecyclerViewAdapter.DiffUtilCallback callback = new DiffUtilCallback(this.mQuestions, newItems);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(callback);

        mQuestions.clear();
        mQuestions.addAll(newItems);

        mQuestionsPrevious.clear();
        mQuestionsPrevious.addAll(newItems);

        diffResult.dispatchUpdatesTo(this);
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
            viewClickListener.onClick(mQuestions.get(getLayoutPosition()), getLayoutPosition());
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

    class SortedListCallback extends SortedList.Callback<RecyclerViewQuestionItem>{
        /**
         * Similar to {@link Comparator#compare(Object, Object)}, should compare two and
         * return how they should be ordered.
         *
         * @param o1 The first object to compare.
         * @param o2 The second object to compare.
         * @return a negative integer, zero, or a positive integer as the
         * first argument is less than, equal to, or greater than the
         * second.
         */
        @Override
        public int compare(RecyclerViewQuestionItem o1, RecyclerViewQuestionItem o2) {
            return o1.getQuestionTitle().compareTo(o2.getQuestionTitle());
        }

        /**
         * Called by the SortedList when the item at the given position is updated.
         *
         * @param position The position of the item which has been updated.
         * @param count    The number of items which has changed.
         */
        @Override
        public void onChanged(int position, int count) {
            QuestionsRecyclerViewAdapter.this.notifyItemRangeChanged(position, count);
        }

        /**
         * Called by the SortedList when it wants to check whether two items have the same data
         * or not. SortedList uses this information to decide whether it should call
         * {@link #onChanged(int, int)} or not.
         * <p>
         * SortedList uses this method to check equality instead of {@link Object#equals(Object)}
         * so
         * that you can change its behavior depending on your UI.
         * <p>
         * For example, if you are using SortedList with a
         * {@link RecyclerView.Adapter RecyclerView.Adapter}, you should
         * return whether the items' visual representations are the same or not.
         *
         * @param oldItem The previous representation of the object.
         * @param newItem The new object that replaces the previous one.
         * @return True if the contents of the items are the same or false if they are different.
         */
        @Override
        public boolean areContentsTheSame(RecyclerViewQuestionItem oldItem, RecyclerViewQuestionItem newItem) {
            return oldItem.equals(newItem);
        }

        /**
         * Called by the SortedList to decide whether two objects represent the same Item or not.
         * <p>
         * For example, if your items have unique ids, this method should check their equality.
         *
         * @param item1 The first item to check.
         * @param item2 The second item to check.
         * @return True if the two items represent the same object or false if they are different.
         */
        @Override
        public boolean areItemsTheSame(RecyclerViewQuestionItem item1, RecyclerViewQuestionItem item2) {
            return item1.getQuestionId().equals(item2.getQuestionId());
        }

        /**
         * Called when {@code count} number of items are inserted at the given position.
         *
         * @param position The position of the new item.
         * @param count    The number of items that have been added.
         */
        @Override
        public void onInserted(int position, int count) {
            QuestionsRecyclerViewAdapter.this.notifyItemRangeInserted(position, count);
        }

        /**
         * Called when {@code count} number of items are removed from the given position.
         *
         * @param position The position of the item which has been removed.
         * @param count    The number of items which have been removed.
         */
        @Override
        public void onRemoved(int position, int count) {
            QuestionsRecyclerViewAdapter.this.notifyItemRangeRemoved(position, count);
        }

        /**
         * Called when an item changes its position in the list.
         *
         * @param fromPosition The previous position of the item before the move.
         * @param toPosition   The new position of the item.
         */
        @Override
        public void onMoved(int fromPosition, int toPosition) {
            QuestionsRecyclerViewAdapter.this.notifyItemMoved(fromPosition, toPosition);
        }
    }
}
