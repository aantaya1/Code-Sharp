package com.aantaya.codesharp.ui.answer;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionSearchFilter;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.callbacks.IdQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;
import com.aantaya.codesharp.utils.PreferenceUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public class AnswerViewModel extends AndroidViewModel {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_COMPLETED_ALL_QUESTIONS = 2;
    public static final int STATE_FAILED = 3;

    private QuestionSearchFilter questionSearchFilter = new QuestionSearchFilter();
    private List<String> mQuestionIds = new ArrayList<>();
    private int currentQuestionIdx = 0;

    private Set<String> mCompletedQuestionIds = new HashSet<>();
    private MutableLiveData<Integer> mState = new MutableLiveData<>();

    //The question that we are currently displaying
    private MutableLiveData<QuestionModel> mQuestion = new MutableLiveData<>();

    private QuestionRepository mQuestionRepo;
    private int numQueried = 2;
    private int numFinishedQueried = 0;

    public AnswerViewModel(@NonNull Application application) {
        super(application);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
        questionSearchFilter.setIncludeCompleted(prefs.getBoolean(PreferenceUtils.QUESTION_FILTER_INCLUDE_COMPLETED, false));
        questionSearchFilter.setIncludeNotCompleted(true);
    }

    public void init(@NonNull String initialQuestionId){
        //If the ViewModel has already been initialized, no need to re-init
        if (mQuestionRepo != null) return;

        mState.setValue(STATE_LOADING);

        //todo: we might want to replace this with DI (Dagger)
        //init the repo
        mQuestionRepo = QuestionRepositoryFirestoreImpl.getInstance(new WeakReference<>(getApplication()));

        loadQuestions(initialQuestionId);
        loadCompletedQuestions();
    }

    private void loadQuestions(@Nullable final String initialQuestionId){
        mQuestionRepo.getQuestions(questionSearchFilter, new QuestionQueryCallback() {
            @Override
            public void onSuccess(Set<QuestionModel> questionModels) {

                //note: in the future, we could consider leaving all the
                //questions in memory and not needing to re-query them
                //add all of the ids to the list of ids
                for (QuestionModel questionModel : questionModels){
                    //Skip the initial question (we don't want to add it twice)
                    if (questionModel.getId().equals(initialQuestionId)) continue;

                    mQuestionIds.add(questionModel.getId());
                }

                //Add the initial question to the beginning of the list
                mQuestionIds.add(0, initialQuestionId);

                if (++numFinishedQueried == numQueried){
                    finishInit();
                }
            }

            @Override
            public void onFailure(String failureString) {
                mState.setValue(STATE_FAILED);
            }
        });
    }

    private void loadCompletedQuestions(){
        mQuestionRepo.getCompletedQuestions(new IdQueryCallback() {
            @Override
            public void onSuccess(Set<String> ids) {
                mCompletedQuestionIds.addAll(ids);

                if (++numFinishedQueried == numQueried){
                    finishInit();
                }
            }

            @Override
            public void onFailure(String failureString) {
                mState.setValue(STATE_FAILED);
            }
        });
    }

    private void finishInit(){
        //Once we have loaded the question ids, load the first question
        loadNextQuestion();
    }

    public void loadNextQuestion(){
        if (currentQuestionIdx == mQuestionIds.size()){
            mState.setValue(STATE_COMPLETED_ALL_QUESTIONS);
            return;
        }

        mState.setValue(STATE_LOADING);

        String nextQuestionId = mQuestionIds.get(currentQuestionIdx++);

        mQuestionRepo.getQuestion(nextQuestionId, new QuestionQueryCallback() {
            @Override
            public void onSuccess(Set<QuestionModel> questionModels) {
                for (QuestionModel model : questionModels){
                    //there will only be one model in the set
                    mQuestion.setValue(model);

                    //notify the ui once we are done loading the question
                    mState.setValue(STATE_NORMAL);
                    break;
                }
            }

            @Override
            public void onFailure(String failureString) {
                //todo: use state object to terminate activity
            }
        });
    }

    public void uploadCorrectQuestion(@NonNull String questionId, @NonNull QuestionDifficulty difficulty){
        //If the user completes the question more than once, we don't need
        // to re-upload the completion of the question
        if (mCompletedQuestionIds.contains(questionId)) return;

        mQuestionRepo.uploadCompletedQuestion(questionId, difficulty);
    }

    public LiveData<QuestionModel> getQuestion(){
        return mQuestion;
    }

    public LiveData<Integer> getState(){
        return mState;
    }
}
