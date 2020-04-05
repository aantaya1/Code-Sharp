package com.aantaya.codesharp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.aantaya.codesharp.ui.answer.AnswerFragment;
import com.aantaya.codesharp.ui.home.HomeFragment;
import com.aantaya.codesharp.utils.IntentUtils;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        //Pass the ID of the question that was clicked through to the fragment
        String currentQuestionId = getIntent().getStringExtra(IntentUtils.CLICKED_QUESTION_ID_EXTRA);
        Bundle argBundle = new Bundle();
        argBundle.putString(IntentUtils.CLICKED_QUESTION_ID_EXTRA, currentQuestionId);

        AnswerFragment answerFragment = AnswerFragment.newInstance();
        answerFragment.setArguments(argBundle);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, answerFragment)
                    .commitNow();
        }
    }
}
