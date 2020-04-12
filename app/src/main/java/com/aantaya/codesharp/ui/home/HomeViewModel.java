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

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FAILED = 2;

    private MutableLiveData<List<RecyclerViewQuestionItem>> mQuestionsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> mState = new MutableLiveData<>();
    private QuestionRepository questionRepo;

    public HomeViewModel() {

    }

    public void init(){
        if (questionRepo != null){
            return;
        }

        mState.setValue(STATE_LOADING);

        questionRepo = QuestionRepositoryFirestoreImpl.getInstance();

        questionRepo.getQuestions(null, new QuestionQueryCallback() {
            @Override
            public void onSuccess(Set<QuestionModel> questionModels) {
                List<RecyclerViewQuestionItem> items = new ArrayList<>();

                for (QuestionModel model : questionModels){
                    items.add(new RecyclerViewQuestionItem(model.getId(), model.getQuestionTitle(), model.getDifficulty()));
                }

                mQuestionsLiveData.setValue(items);
                mState.setValue(STATE_NORMAL);
            }

            @Override
            public void onFailure(String failureString) {
                mState.setValue(STATE_FAILED);
            }
        });
    }

    public LiveData<List<RecyclerViewQuestionItem>> getQuestions() {
        return mQuestionsLiveData;
    }

    public LiveData<Integer> getState(){
        return mState;
    }
}