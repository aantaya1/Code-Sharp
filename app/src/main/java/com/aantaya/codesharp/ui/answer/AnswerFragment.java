package com.aantaya.codesharp.ui.answer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionType;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.utils.IntentUtils;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.radiobutton.MaterialRadioButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.tiagohm.codeview.CodeView;
import br.tiagohm.codeview.Language;
import br.tiagohm.codeview.Theme;

import static com.aantaya.codesharp.ui.answer.AnswerViewModel.STATE_COMPLETED_ALL_QUESTIONS;
import static com.aantaya.codesharp.ui.answer.AnswerViewModel.STATE_FAILED;
import static com.aantaya.codesharp.ui.answer.AnswerViewModel.STATE_LOADING;
import static com.aantaya.codesharp.ui.answer.AnswerViewModel.STATE_NORMAL;

public class AnswerFragment extends Fragment {

    private AnswerViewModel mViewModel;

    private TextView mQuestionTitle;
    private TextView mQuestionDescription;
    private CodeView mCodeView;
    private RadioGroup mQuestionAnswersContainer;
    private Button mSubmitButton;
    private ScrollView mScrollviewLayout;
    private ShimmerFrameLayout mShimmerLayout;

    private String mSelectedAnswer = "";
    private int mSelectedLine = -1;

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
        mScrollviewLayout = root.findViewById(R.id.scroll_view_layout);
        mShimmerLayout = root.findViewById(R.id.shimmer_layout);

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(AnswerViewModel.class);

        //If for some reason getArguments is null and we can't load the question the user clicked,
        //then init viewmodel w/o an initial question (we will just start at some random question)
        String initialQuestionId = (getArguments() != null) ? getArguments().getString(IntentUtils.CLICKED_QUESTION_ID_EXTRA) : null;
        mViewModel.init(initialQuestionId);

        mViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            switch (state){
                case STATE_NORMAL:
                    hideLoading();
                    break;
                case STATE_LOADING:
                    displayLoading();
                    break;
                case STATE_COMPLETED_ALL_QUESTIONS:
                    //todo: need to create a custom toast for this

                    //Once the user has finished all the questions finish the activity
                    getActivity().finish();
                    break;
                case STATE_FAILED:
                    //todo: need to implement
                    Toast.makeText(getContext(), "State Failed!", Toast.LENGTH_LONG).show();
                    break;
            }
        });

        mViewModel.getQuestion().observe(getViewLifecycleOwner(), questionModel -> {
            mQuestionTitle.setText(questionModel.getQuestionTitle());

            QuestionPayload payload = QuestionModel.getPayloadWithPreferredLanguage(questionModel, getContext());

            if (payload == null) {
                //This should never happen but if it does...
                //todo: display a msg to user that something went wrong
                mViewModel.loadNextQuestion();
                return;
            }

            mQuestionDescription.setText(questionModel.getDescription());

            CodeView.OnHighlightListener listener = new CodeView.OnHighlightListener() {
                @Override
                public void onStartCodeHighlight() {

                }

                @Override
                public void onFinishCodeHighlight() {

                }

                @Override
                public void onLanguageDetected(Language language, int i) {

                }

                @Override
                public void onFontSizeChanged(int i) {

                }

                @Override
                public void onLineClicked(int lineNumber, String lineContent) {
                    mSelectedLine = lineNumber;
                }
            };

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
            boolean useDarkMode = prefs.getBoolean("dark_mode", true);
            Theme codeTheme;
            if (useDarkMode){
                codeTheme = Theme.AGATE;
            }else {
                codeTheme = Theme.ATELIER_LAKESIDE_LIGHT;
            }

            mCodeView.setOnHighlightListener(listener)
                    .setTheme(codeTheme)
                    .setCode(payload.getQuestion() + "\n")
                    .setLanguage(Language.valueOf(payload.getProgrammingLanguage().toString().toUpperCase()))
                    .setWrapLine(payload.getProgrammingLanguage().equals(ProgrammingLanguage.MARKDOWN))
                    .setFontSize(14)
                    .setZoomEnabled(false)
                    .setShowLineNumber(true)
                    .setStartLineNumber(1)
                    .apply();

            List<String> possibleResponses = new ArrayList<>();

            //find-the-bug questions will not have answers (users just select line numbers)
            if (!questionModel.getQuestionType().equals(QuestionType.FIND_THE_BUG)){
                // We want to add all of the wrong answers and insert the correct answer at
                // some random index in range [0, len(payload.getWrongAnswers()))
                Random random = new Random();
                int randomPosition = random.nextInt(payload.getWrongAnswers().size());
                for (int i=0; i< payload.getWrongAnswers().size(); i++){
                    if (i == randomPosition) possibleResponses.add(payload.getAnswer());
                    possibleResponses.add(payload.getWrongAnswers().get(i));
                }
            }

            //It is very important that we remove all of the buttons
            // from the prev question!
            mQuestionAnswersContainer.removeAllViews();

            for (String response : possibleResponses){

                //set the properties for button
                MaterialRadioButton btnTag = new MaterialRadioButton(getContext());

                btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                btnTag.setTransformationMethod(null);
                btnTag.setText(response);

                btnTag.setOnClickListener(view -> {
                    //todo: do i need to do anything else?
                    mSelectedAnswer = ((MaterialRadioButton) view).getText().toString();
                });

                mQuestionAnswersContainer.addView(btnTag);
            }
        });

        //Set an onclick listener because we can't use the onclick xml attribute for
        // buttons inside of Fragments
        mSubmitButton.setOnClickListener(v -> {

            QuestionModel currentQuestion = mViewModel.getQuestion().getValue();

            //todo: we should make sure we handle NPE
            QuestionPayload payload = QuestionModel.getPayloadWithPreferredLanguage(currentQuestion, getContext());

            View toastView = getLayoutInflater().inflate(R.layout.question_feedback_toast_layout, null);
            ImageView toastIcon = toastView.findViewById(R.id.custom_toast_icon);
            TextView toastText = toastView.findViewById(R.id.custom_toast_text);

            //Note, we need to decrement mSelectedLine because our answers are zero-based
            // indexed, while the displayed line numbers are not
            if (QuestionModel.answerIsCorrect(currentQuestion.getQuestionType(), payload,
                    mSelectedAnswer, mSelectedLine-1)){
                toastIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_correct));
                toastText.setText(getResources().getString(R.string.correct_answer));

                mViewModel.uploadCorrectQuestion(currentQuestion.getId(), currentQuestion.getDifficulty());
                mViewModel.loadNextQuestion();
            }else {
                toastIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong));
                toastText.setText(getResources().getString(R.string.incorrect_answer));
            }

            // Initiate the Toast instance.
            Toast toast = new Toast(getContext());
            // Set custom view in toast.
            toast.setView(toastView);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0,0);
            toast.show();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.answer_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.answer_menu_skip:
                mViewModel.loadNextQuestion();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayLoading(){
        mShimmerLayout.startShimmer();
        mShimmerLayout.setVisibility(View.VISIBLE);
        mScrollviewLayout.setVisibility(View.INVISIBLE);
    }

    private void hideLoading(){
        mShimmerLayout.stopShimmer();
        mShimmerLayout.setVisibility(View.GONE);
        mScrollviewLayout.setVisibility(View.VISIBLE);
    }
}
