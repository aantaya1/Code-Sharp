package com.aantaya.codesharp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.repositories.QuestionRepository;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<RecyclerViewQuestionItem>> mQuestionsLiveData;
    private QuestionRepository questionRepository;

    public HomeViewModel() {

    }

    public void init(){
        if (questionRepository != null){
            return;
        }

        questionRepository = QuestionRepository.getInstance();
        mQuestionsLiveData = questionRepository.getQuestionsForRecyclerView();
    }

    public LiveData<List<RecyclerViewQuestionItem>> getQuestions() {
        return mQuestionsLiveData;
    }
}