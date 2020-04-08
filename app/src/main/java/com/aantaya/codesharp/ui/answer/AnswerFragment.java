package com.aantaya.codesharp.ui.answer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.utils.IntentUtils;

import io.github.kbiakov.codeview.CodeView;

public class AnswerFragment extends Fragment {

    private AnswerViewModel mViewModel;

    private TextView mQuestionTitle;
    private TextView mQuestionDescription;
    private CodeView mCodeView;
    private View mQuestionAnswersContainer;
    private Button mSubmitButton;

    public static AnswerFragment newInstance() {
        return new AnswerFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.answer_fragment, container, false);

        mQuestionTitle = root.findViewById(R.id.answer_question_title);
        mQuestionDescription = root.findViewById(R.id.answer_question_description);
        mCodeView = root.findViewById(R.id.answer_question_content);
        mQuestionAnswersContainer = root.findViewById(R.id.answer_question_answers_container);
        mSubmitButton = root.findViewById(R.id.answer_submit_button);

        //Set an onclick listener because we can't use the onclick attribute for
        // buttons inside of Fragments
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: need to implement
                Toast.makeText(getContext(), "Submit clicked!", Toast.LENGTH_SHORT).show();
            }
        });

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


    }

}
