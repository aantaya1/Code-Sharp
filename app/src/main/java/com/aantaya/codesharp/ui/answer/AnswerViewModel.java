package com.aantaya.codesharp.ui.answer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class AnswerViewModel extends ViewModel {

    private List<String> mQuestionIds = new ArrayList<>();
    private int currentQuestionIdx = 0;

    private MutableLiveData<QuestionModel> question;
    private MutableLiveData<Boolean> isInitialising;
    private MutableLiveData<Boolean> loadingQuestion;

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

        //todo: I need to make sure this lambda is only executed one time...
        String finalInitialQuestionId = initialQuestionId;
        Transformations.map(questionRepo.getQuestionIds(config), questions -> {

            // If the initial question is contained in the set (it should be)
            // then we need to remove it since we don't want to display the
            // same question twice
            questions.remove(finalInitialQuestionId);

            mQuestionIds.addAll(questions);
            isInitialising.setValue(false);
            return null;
        });

        if (initialQuestionId == null){
            initialQuestionId = mQuestionIds.get(currentQuestionIdx++);
        }

        //todo: we cannot throw a NPE in production...we should finish the activity if this happens
        // by using a state obj that is observed in the activity
        Transformations.map(Objects.requireNonNull(questionRepo.getQuestion(initialQuestionId)), question -> {
            this.question.setValue(question);
            this.loadingQuestion.setValue(false);
            return null;
        });
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
        if (currentQuestionIdx == mQuestionIds.size()-1) return;

        this.loadingQuestion.setValue(true);

        String nextQuestionId = mQuestionIds.get(currentQuestionIdx++);

        //todo: we cannot throw a NPE in production...we should finish the activity if this happens
        // by using a state obj that is observed in the activity
        Transformations.map(Objects.requireNonNull(questionRepo.getQuestion(nextQuestionId)), question -> {
            this.question.setValue(question);
            this.loadingQuestion.setValue(false);
            return null;
        });
    }
}
