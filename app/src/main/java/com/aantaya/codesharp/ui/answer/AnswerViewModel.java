package com.aantaya.codesharp.ui.answer;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

public class AnswerViewModel extends ViewModel {

    private List<String> mQuestionIds = new ArrayList<>();
    private int currentQuestionIdx = 0;

    private Set<String> mCompletedQuestionIds = new HashSet<>();

    private MutableLiveData<QuestionModel> mQuestion;
    private MutableLiveData<Boolean> mIsInitialising;
    private MutableLiveData<Boolean> mLoadingQuestion;

    //We need to keep references to these observers so we can remove them
    // when the ViewModel is destroyed and prevent a memory leak
    private LiveData<Set<String>> mRepoQuestionIds;
    private Observer<Set<String>> mRepoQuestionIdsObserver;
    private LiveData<Set<String>> mRepoCompletedQuestionIds;
    private Observer<Set<String>> mRepoCompletedQuestionIdsObserver;
    private LiveData<QuestionModel> mRepoQuestion;
    private Observer<QuestionModel> mRepoQuestionObserver;

    private QuestionRepository mQuestionRepo;

    public void init(@Nullable String initialQuestionId){
        //If the ViewModel has already been initialized, no need to re-init
        if (mQuestionRepo != null) return;

        //Keep track of whether or not we have finished loading the question ids
        mIsInitialising = new MutableLiveData<>();
        mIsInitialising.setValue(true);

        //Keep track of whether or not we are loading a question
        mLoadingQuestion = new MutableLiveData<>();
        mLoadingQuestion.setValue(true);

        mQuestion = new MutableLiveData<>();

        mQuestionRepo = QuestionRepositoryFirestoreImpl.getInstance();

        QuestionFilterConfig config = new QuestionFilterConfig();
        config.setIncludeCompletedQuestions(true);
        config.setIncludeIncompleteQuestions(false);

        mRepoCompletedQuestionIds = mQuestionRepo.getQuestionIds(config);
        mRepoCompletedQuestionIdsObserver = new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> questionIds) {
                if (!mCompletedQuestionIds.isEmpty()) return;

                mCompletedQuestionIds.addAll(questionIds);
            }
        };

        mRepoCompletedQuestionIds.observeForever(mRepoCompletedQuestionIdsObserver);

        //todo: eventually we will want to check the user's shared prefs for filtering questions
        config = new QuestionFilterConfig();
        config.setIncludeCompletedQuestions(false);

        String finalInitialQuestionId = initialQuestionId;
        mRepoQuestionIds = mQuestionRepo.getQuestionIds(config);
        mRepoQuestionIdsObserver = new Observer<Set<String>>() {
            @Override
            public void onChanged(Set<String> questionIds) {
                if (!mQuestionIds.isEmpty()) return;

                // If the initial question is contained in the set (it should be)
                // then we need to remove it since we don't want to display the
                // same question twice
                questionIds.remove(finalInitialQuestionId);

                mQuestionIds.addAll(questionIds);
                mIsInitialising.setValue(false);
            }
        };

        mRepoQuestionIds.observeForever(mRepoQuestionIdsObserver);

        if (initialQuestionId == null){
            initialQuestionId = mQuestionIds.get(currentQuestionIdx++);
        }

        //todo: we cannot throw a NPE in production...we should finish the activity if this happens
        // by using a state obj that is observed in the activity
        mRepoQuestion = mQuestionRepo.getQuestion(initialQuestionId);
        mRepoQuestionObserver = new Observer<QuestionModel>() {
            @Override
            public void onChanged(QuestionModel questionModel) {
                mQuestion.setValue(questionModel);
                mLoadingQuestion.setValue(false);
            }
        };

        mRepoQuestion.observeForever(mRepoQuestionObserver);
    }

    public LiveData<QuestionModel> getQuestion(){
        return mQuestion;
    }

    public LiveData<Boolean> getIsInitialising(){
        return mIsInitialising;
    }

    public LiveData<Boolean> getIsQuestionLoading(){
        return mLoadingQuestion;
    }

    public void loadNextQuestion(){
        //todo: we should finish the activity if this happens by using a state obj that is observed in the activity
        if (currentQuestionIdx == mQuestionIds.size()) return;

        this.mLoadingQuestion.setValue(true);

        String nextQuestionId = mQuestionIds.get(currentQuestionIdx++);

        //todo: is this right??
        //Before we add the next observer, we need to remove the previous one
        mRepoQuestion.removeObserver(mRepoQuestionObserver);

        //todo: we cannot throw a NPE in production...we should finish the activity if this happens
        // by using a state obj that is observed in the activity
        mRepoQuestion = mQuestionRepo.getQuestion(nextQuestionId);
        mRepoQuestionObserver = new Observer<QuestionModel>() {
            @Override
            public void onChanged(QuestionModel questionModel) {
                mQuestion.setValue(questionModel);
                mLoadingQuestion.setValue(false);
            }
        };

        mRepoQuestion.observeForever(mRepoQuestionObserver);
    }

    public void uploadCorrectQuestion(@NonNull String questionId){
        //If the user completes the question more than once, we don't need
        // to re-upload the completion of the question
        //todo: need to uncomment this once I implement filtering correctly on repo
//        if (mCompletedQuestionIds.contains(questionId)) return;

        mQuestionRepo.uploadCompletedQuestion(questionId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();

        //We need to remove the observers when the ViewModel is destroyed
        // to prevent a memory leak
        mRepoQuestionIds.removeObserver(mRepoQuestionIdsObserver);
        mRepoCompletedQuestionIds.removeObserver(mRepoCompletedQuestionIdsObserver);
        mRepoQuestion.removeObserver(mRepoQuestionObserver);
    }
}
