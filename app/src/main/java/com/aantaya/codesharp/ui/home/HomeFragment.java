package com.aantaya.codesharp.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.models.QuestionDifficulty;
import com.aantaya.codesharp.ui.adapters.QuestionsRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.recyclerview_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<RecyclerViewQuestionItem> items = new ArrayList<>();
        items.add(new RecyclerViewQuestionItem(1, "Hello", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(2, "World", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(3, "This", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(4, "Cool", QuestionDifficulty.EASY));

        recyclerView.setAdapter(new QuestionsRecyclerViewAdapter(items));

        return root;
    }
}
