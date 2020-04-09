package com.aantaya.codesharp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.AnswerActivity;
import com.aantaya.codesharp.R;
import com.aantaya.codesharp.listeners.MyItemClickListener;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.ui.adapters.QuestionsRecyclerViewAdapter;
import com.aantaya.codesharp.utils.IntentUtils;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private HomeViewModel mHomeViewModel;
    private RecyclerView mRecyclerView;
    private QuestionsRecyclerViewAdapter mAdapter;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mRecyclerView = root.findViewById(R.id.recyclerview_home);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        // Add a divider between items in the RecyclerView
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        mRecyclerView.addItemDecoration(dividerItemDecoration);

        mAdapter = new QuestionsRecyclerViewAdapter(new WeakReference<>(getContext()), new QuestionsRecyclerViewAdapter.ItemClickListener() {
            @Override
            public void onClick(RecyclerViewQuestionItem item, int position) {
                Intent intent = new Intent(getActivity(), AnswerActivity.class);
                intent.putExtra(IntentUtils.CLICKED_QUESTION_ID_EXTRA, item.getQuestionId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(RecyclerViewQuestionItem item, int position) {
                //todo: maybe if we want to do something with this
            }
        });

        mRecyclerView.setAdapter(mAdapter);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //Get the view model
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        //Initialize the view model with data
        mHomeViewModel.init();

        //Set up the fragment to observe changes to the questions as they are updated in the viewmodel
        //and then update the adapter with the new items
        mHomeViewModel.getQuestions().observe(getViewLifecycleOwner(),
                new Observer<List<RecyclerViewQuestionItem>>() {
                    @Override
                    public void onChanged(List<RecyclerViewQuestionItem> recyclerViewQuestionItems) {
                        mAdapter.updateItems(recyclerViewQuestionItems);
                    }
                });
    }
}
