package com.aantaya.codesharp.ui.answer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.utils.IntentUtils;
import com.aantaya.codesharp.utils.MyTextUtils;

import java.util.ArrayList;
import java.util.List;

import br.tiagohm.codeview.CodeView;
import br.tiagohm.codeview.Language;
import br.tiagohm.codeview.Theme;

public class AnswerFragment extends Fragment {

    private AnswerViewModel mViewModel;

    private TextView mQuestionTitle;
    private TextView mQuestionDescription;
    private CodeView mCodeView;
    private LinearLayout mQuestionAnswersContainer;
    private Button mSubmitButton;

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

                //todo: need to set this properly in payload...
                mQuestionDescription.setText(MyTextUtils.getText(getContext(), R.string.question_description, "Select the best answer below and submit!"));

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
                    codeTheme = Theme.SOLARIZED_LIGHT;
                }

                mCodeView.setOnHighlightListener(listener)
                        .setTheme(codeTheme)
                        .setCode("public class HomeViewModel extends ViewModel {\n" +
                                "\n" +
                                "    private MutableLiveData<List<RecyclerViewQuestionItem>> mQuestionsLiveData;\n" +
                                "    private QuestionRepository questionRepository;\n" +
                                "\n" +
                                "    public HomeViewModel() {\n" +
                                "\n" +
                                "    }\n" +
                                "\n" +
                                "    public void init(){\n" +
                                "        if (questionRepository != null){\n" +
                                "            return;\n" +
                                "        }\n" +
                                "\n" +
                                "        questionRepository = QuestionRepositoryFirestoreImpl.getInstance();\n" +
                                "        mQuestionsLiveData = questionRepository.getQuestionsForRecycleView(null);\n" +
                                "    }\n" +
                                "\n" +
                                "    public LiveData<List<RecyclerViewQuestionItem>> getQuestions() {\n" +
                                "        return mQuestionsLiveData;\n" +
                                "    }\n" +
                                "}")
                        .setLanguage(Language.JAVA)
                        .setWrapLine(false)
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

                QuestionPayload payload = QuestionModel.getPayloadWithPreferredLanguage(mViewModel.getQuestion().getValue(), getContext());

                if (payload.getAnswer().equals(mSelectedAnswer)){
                    //todo: update the user's correct answers
                    //todo: display something better than a toast
                    Toast.makeText(getContext(), "Correct!", Toast.LENGTH_SHORT).show();
                    mViewModel.loadNextQuestion();
                }else {
                    //todo: display something better than a toast
                    Toast.makeText(getContext(), "Incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
