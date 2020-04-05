package com.aantaya.codesharp.ui.answer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.utils.IntentUtils;

public class AnswerFragment extends Fragment {

    private AnswerViewModel mViewModel;

    public static AnswerFragment newInstance() {
        return new AnswerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.answer_fragment, container, false);



        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AnswerViewModel.class);

        //If for some reason getArguments is null and we can't load the question the user clicked,
        //then init viewmodel w/o an initial question (we will just start at some random question)
        String initialQuestionId = (getArguments() != null) ? getArguments().getString(IntentUtils.CLICKED_QUESTION_ID_EXTRA) : "";
        mViewModel.init(initialQuestionId);

        // TODO: Use the ViewModel
    }

}
