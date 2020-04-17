package com.aantaya.codesharp.ui.answer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.utils.IntentUtils;
import com.aantaya.codesharp.utils.MyTextUtils;
import com.github.ybq.android.spinkit.SpinKitView;

import java.util.ArrayList;
import java.util.List;

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
    private LinearLayout mQuestionAnswersContainer;
    private Button mSubmitButton;
    private ScrollView mScrollviewLayout;
    private SpinKitView mLoadingAnimation;

    private String mSelectedAnswer = "";

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
        mLoadingAnimation = root.findViewById(R.id.loading_animation);

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

        mViewModel.getState().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer state) {
                switch (state){
                    case STATE_NORMAL:
                        Log.e("AnswerFragment", "shimmer: end");
                        //todo: need to implement
                        hideLoading();
                        break;
                    case STATE_LOADING:
                        Log.e("AnswerFragment", "shimmer: start");
                        //todo: need to implement
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
            }
        });

        mViewModel.getQuestion().observe(getViewLifecycleOwner(), new Observer<QuestionModel>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onChanged(QuestionModel questionModel) {
                mQuestionTitle.setText(MyTextUtils.getText(getContext(), R.string.question_title, questionModel.getQuestionTitle()));

                QuestionPayload payload = QuestionModel.getPayloadWithPreferredLanguage(questionModel, getContext());

                if (payload == null) {
                    //This should never happen but if it does...
                    //todo: display a msg to user that something went wrong
                    mViewModel.loadNextQuestion();
                    return;
                }

                mQuestionDescription.setText(MyTextUtils.getText(getContext(), R.string.question_description, questionModel.getDescription()));

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
                    public void onLineClicked(int i, String s) {

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

                //todo: programmatically create buttons with answers

                mQuestionAnswersContainer.removeAllViews();

                int i=0;

                List<String> possibleResponses = new ArrayList<>();
                possibleResponses.add(payload.getAnswer());
                possibleResponses.addAll(payload.getWrongAnswers());

                for (String response : possibleResponses){
                    //set the properties for button
                    Button btnTag = new Button(getContext());
                    btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    btnTag.setText(response);
                    btnTag.setId(i++);
                    btnTag.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mSelectedAnswer = ((Button) view).getText().toString();
                        }
                    });

                    mQuestionAnswersContainer.addView(btnTag);
                }
            }
        });

        //Set an onclick listener because we can't use the onclick xml attribute for
        // buttons inside of Fragments
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QuestionModel currentQuestion = mViewModel.getQuestion().getValue();

                //todo: we should make sure we handle NPE
                QuestionPayload payload = QuestionModel.getPayloadWithPreferredLanguage(currentQuestion, getContext());

                View toastView = getLayoutInflater().inflate(R.layout.question_feedback_toast_layout, null);
                ImageView toastIcon = toastView.findViewById(R.id.custom_toast_icon);
                TextView toastText = toastView.findViewById(R.id.custom_toast_text);

                if (payload.getAnswer().equals(mSelectedAnswer)){

                    toastIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_correct));
                    toastText.setText("Correct, good job!");

                    mViewModel.uploadCorrectQuestion(currentQuestion.getId(), currentQuestion.getDifficulty());
                    mViewModel.loadNextQuestion();
                }else {
                    toastIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_wrong));
                    toastText.setText("That's not right, try again.");
                }

                // Initiate the Toast instance.
                Toast toast = new Toast(getContext());
                // Set custom view in toast.
                toast.setView(toastView);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0,0);
                toast.show();
            }
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
        mScrollviewLayout.setVisibility(View.INVISIBLE);
        mLoadingAnimation.setVisibility(View.VISIBLE);
    }

    private void hideLoading(){
        mScrollviewLayout.setVisibility(View.VISIBLE);
        mLoadingAnimation.setVisibility(View.INVISIBLE);
    }
}
