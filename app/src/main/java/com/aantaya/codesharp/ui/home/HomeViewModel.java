package com.aantaya.codesharp.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<List<RecyclerViewQuestionItem>> mQuestionsLiveData;
    private QuestionRepository questionRepo;

    public HomeViewModel() {

    }

    public void init(){
        if (questionRepo != null){
            return;
        }

        questionRepo = QuestionRepositoryFirestoreImpl.getInstance();
        mQuestionsLiveData = new MutableLiveData<>();

        questionRepo.getQuestions(null, new QuestionQueryCallback() {
            @Override
            public void onSuccess(Set<QuestionModel> questionModels) {
                List<RecyclerViewQuestionItem> items = new ArrayList<>();

                for (QuestionModel model : questionModels){
                    items.add(new RecyclerViewQuestionItem(model.getId(), model.getQuestionTitle(), model.getDifficulty()));
                }

                mQuestionsLiveData.setValue(items);
            }

            @Override
            public void onFailure(String failureString) {

            }
        });
    }

    public LiveData<List<RecyclerViewQuestionItem>> getQuestions() {
        return mQuestionsLiveData;
    }
}