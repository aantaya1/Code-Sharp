package com.aantaya.codesharp.ui.answer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;

import java.util.ArrayList;
import java.util.List;

public class AnswerViewModel extends ViewModel {

    private List<String> mQuestionIds = new ArrayList<>();
    private int currentQuestionIdx;

    private MutableLiveData<QuestionModel> question;

    private QuestionRepository questionRepositoryFirestore;

    public void init(String initialQuestionId){

        //If the ViewModel has already been initialized, no need to re-init
        if (questionRepositoryFirestore != null) return;

        questionRepositoryFirestore = QuestionRepositoryFirestoreImpl.getInstance();

        if (initialQuestionId.isEmpty()){
            //todo: something
        }

        //todo: load all of the questions except for the initial question and exclude questions
        // that the user has already finished
    }
}
