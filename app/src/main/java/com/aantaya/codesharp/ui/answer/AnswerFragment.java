package com.aantaya.codesharp.ui.answer;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.R;
import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.utils.IntentUtils;
import com.aantaya.codesharp.utils.MyTextUtils;

import io.github.kbiakov.codeview.CodeView;
import io.github.kbiakov.codeview.OnCodeLineClickListener;
import io.github.kbiakov.codeview.adapters.Format;
import io.github.kbiakov.codeview.adapters.Options;
import io.github.kbiakov.codeview.highlight.CodeHighlighter;
import io.github.kbiakov.codeview.highlight.ColorTheme;
import io.github.kbiakov.codeview.highlight.ColorThemeData;
import io.github.kbiakov.codeview.highlight.FontCache;

public class AnswerFragment extends Fragment {

    private AnswerViewModel mViewModel;

    private TextView mQuestionTitle;
    private TextView mQuestionDescription;
    private CodeView mCodeView;
    private LinearLayout mQuestionAnswersContainer;
    private Button mSubmitButton;

    private ColorThemeData mCodeViewColorTheme;

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
        mViewModel = ViewModelProviders.of(this).get(AnswerViewModel.class);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        boolean useDarkMode = prefs.getBoolean("dark_mode", true);
        if (useDarkMode){
            mCodeViewColorTheme = ColorTheme.MONOKAI.theme();
        }else {
            mCodeViewColorTheme = ColorTheme.SOLARIZED_LIGHT.theme();
        }

        //If for some reason getArguments is null and we can't load the question the user clicked,
        //then init viewmodel w/o an initial question (we will just start at some random question)
        String initialQuestionId = (getArguments() != null) ? getArguments().getString(IntentUtils.CLICKED_QUESTION_ID_EXTRA) : null;
        mViewModel.init(initialQuestionId);

        mViewModel.getQuestion().observe(getViewLifecycleOwner(), new Observer<QuestionModel>() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onChanged(QuestionModel questionModel) {
                mQuestionTitle.setText(MyTextUtils.getText(getContext(), R.string.question_title, questionModel.getQuestionTitle()));

                //todo: check the user's prefs and load the language from there
                QuestionPayload payload = questionModel.getQuestionPayloadMap().get(ProgrammingLanguage.JAVA.toString());

                //todo: if requested language is not supported pick another one
                if (payload == null) return;

                //todo: need to set this properly in payload...
                mQuestionDescription.setText(MyTextUtils.getText(getContext(), R.string.question_description, "Select the best answer below and submit!"));

//                mCodeView.setCode(payload.getQuestion(), "md");

                mCodeView.setOptions(new Options(
                        getContext(),
                        "public class HomeViewModel extends ViewModel {\n" +
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
                                "}",
                        "java",
                        mCodeViewColorTheme,
                        FontCache.get(getContext()).getTypeface(getContext()),
                        Format.Default.getCompact(),
                        true,
                        true,
                        false,
                        "",
                        100,
                        new OnCodeLineClickListener() {
                            @Override
                            public void onCodeLineClicked(int i, String s) {
                                //todo: for some questions this is important
                                Log.i("ListingsActivity", "On " + (i + 1) + " line clicked");
                            }
                        }));

                mCodeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i=0;
                        int j = 1 + i;

                    }
                });

                mCodeView.setOnDragListener(new View.OnDragListener() {
                    @Override
                    public boolean onDrag(View v, DragEvent event) {
                        return false;
                    }
                });

                mCodeView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        int action = event.getAction();
                        switch (action) {
                            case MotionEvent.ACTION_DOWN:
                                // Disallow ScrollView to intercept touch events.
                                v.getParent().requestDisallowInterceptTouchEvent(true);
                                break;

                            case MotionEvent.ACTION_UP:
                                // Allow ScrollView to intercept touch events.
                                v.getParent().requestDisallowInterceptTouchEvent(false);
                                break;
                        }

                        // Handle ListView touch events.
                        v.onTouchEvent(event);
                        return true;
                    }
                });

                //todo: programmatically create buttons with answers

                mQuestionAnswersContainer.removeAllViews();

                int i=0;

                for (String response : payload.getWrongAnswers()){
                    //set the properties for button
                    Button btnTag = new Button(getContext());
                    btnTag.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    btnTag.setText(response);
                    btnTag.setId(i++);

                    mQuestionAnswersContainer.addView(btnTag);
                }
            }
        });

        //Set an onclick listener because we can't use the onclick attribute for
        // buttons inside of Fragments
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo: need to implement
                mViewModel.loadNextQuestion();
                Toast.makeText(getContext(), "Submit clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
