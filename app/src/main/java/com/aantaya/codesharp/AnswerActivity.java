package com.aantaya.codesharp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.aantaya.codesharp.ui.answer.AnswerFragment;
import com.aantaya.codesharp.utils.IntentUtils;

public class AnswerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.answer_activity);

        //Setup the toolbar
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
