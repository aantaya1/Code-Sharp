package com.aantaya.codesharp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.listeners.MyItemClickListener;
import com.aantaya.codesharp.ui.adapters.QuestionsRecyclerViewAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private HomeViewModel mHomeViewModel;
    private RecyclerView mRecyclerView;
    private QuestionsRecyclerViewAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mRecyclerView = root.findViewById(R.id.recyclerview_home);

        initRecyclerView();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get the view model
        mHomeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);

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

    /**
     * helper function to init the recyclerview along with all the adapter and onClick logic for
     * items in the recyclerview
     */
    private void initRecyclerView(){
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mAdapter = new QuestionsRecyclerViewAdapter(new WeakReference<>(getContext()),
                new MyItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        //todo: launch the answer question activity
                        Toast.makeText(getContext(), "Position: " + position, Toast.LENGTH_SHORT).show();
                    }
                });

        mRecyclerView.setAdapter(mAdapter);
    }
}
