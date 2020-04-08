package com.aantaya.codesharp.ui.answer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

public class AnswerViewModel extends ViewModel {

    private List<String> mQuestionIds = new ArrayList<>();
    private int currentQuestionIdx = 0;

    private MutableLiveData<QuestionModel> question;
    private MutableLiveData<Boolean> isInitialising;
    private MutableLiveData<Boolean> loadingQuestion;

    //We need to keep references to these observers so we can remove them
    // when the ViewModel is destroyed and prevent a memory leak
    private LiveData<Set<String>> mRepoQuestionIds;
    private Observer<Set<String>> mRepoQuestionIdsObserver;
    private LiveData<QuestionModel> mRepoQuestion;
    private Observer<QuestionModel> mRepoQuestionObserver;

    private QuestionRepository questionRepo;

    public void init(@Nullable String initialQuestionId){
        //If the ViewModel has already been initialized, no need to re-init
        if (questionRepo != null) return;

        //Keep track of whether or not we have finished loading the question ids
        isInitialising = new MutableLiveData<>();
        isInitialising.setValue(true);

        //Keep track of whether or not we are loading a question
        loadingQuestion = new MutableLiveData<>();
        loadingQuestion.setValue(true);

        question = new MutableLiveData<>();

        questionRepo = QuestionRepositoryFirestoreImpl.getInstance();

        //todo: eventually we will want to check the user's shared prefs for filtering questions
        QuestionFilterConfig config = new QuestionFilterConfig();
        config.setIncludeCompletedQuestions(false);

        String finalInitialQuestionId = initialQuestionId;
        mRepoQuestionIds = questionRepo.getQuestionIds(config);
        mRepoQuestionIdsObserver = new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> questionIds) {
                if (!mQuestionIds.isEmpty()) return;

                // If the initial question is contained in the set (it should be)
                // then we need to remove it since we don't want to display the
                // same question twice
                questionIds.remove(finalInitialQuestionId);

                mQuestionIds.addAll(questionIds);
                isInitialising.setValue(false);
            }
        };

        mRepoQuestionIds.observeForever(mRepoQuestionIdsObserver);

        if (initialQuestionId == null){
            initialQuestionId = mQuestionIds.get(currentQuestionIdx++);
        }

        //todo: we cannot throw a NPE in production...we should finish the activity if this happens
        // by using a state obj that is observed in the activity
        mRepoQuestion = questionRepo.getQuestion(initialQuestionId);
        mRepoQuestionObserver = new Observer<QuestionModel>() {
            @Override
            public void onChanged(QuestionModel questionModel) {
                question.setValue(questionModel);
                loadingQuestion.setValue(false);
            }
        };

        mRepoQuestion.observeForever(mRepoQuestionObserver);
    }

    public LiveData<QuestionModel> getQuestion(){
        return question;
    }

    public LiveData<Boolean> getIsInitialising(){
        return isInitialising;
    }

    public LiveData<Boolean> getIsQuestionLoading(){
        return loadingQuestion;
    }

    public void loadNextQuestion(){
        //todo: we should finish the activity if this happens by using a state obj that is observed in the activity
        if (currentQuestionIdx == mQuestionIds.size()) return;

        this.loadingQuestion.setValue(true);

        String nextQuestionId = mQuestionIds.get(currentQuestionIdx++);

        //todo: is this right??
        //Before we add the next observer, we need to remove the previous one
        mRepoQuestion.removeObserver(mRepoQuestionObserver);

        //todo: we cannot throw a NPE in production...we should finish the activity if this happens
        // by using a state obj that is observed in the activity
        mRepoQuestion = questionRepo.getQuestion(nextQuestionId);
        mRepoQuestionObserver = new Observer<QuestionModel>() {
            @Override
            public void onChanged(QuestionModel questionModel) {
                question.setValue(questionModel);
                loadingQuestion.setValue(false);
            }
        };

        mRepoQuestion.observeForever(mRepoQuestionObserver);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        //We need to remove the observers when the ViewModel is destroyed
        // to prevent a memory leak
        mRepoQuestionIds.removeObserver(mRepoQuestionIdsObserver);
        mRepoQuestion.removeObserver(mRepoQuestionObserver);
    }
}
