package com.aantaya.codesharp.ui.home;

import android.app.Application;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionSearchFilter;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.callbacks.IdQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;
import com.aantaya.codesharp.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class HomeViewModel extends AndroidViewModel {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FAILED = 2;

    private QuestionSearchFilter questionSearchFilter = new QuestionSearchFilter();
    private MutableLiveData<List<RecyclerViewQuestionItem>> mQuestionsLiveData = new MutableLiveData<>();
    private MutableLiveData<Integer> mState = new MutableLiveData<>();
    private QuestionRepository questionRepo;

    public HomeViewModel(@NonNull Application application) {
        super(application);
    }

    public void init(){
        mState.setValue(STATE_LOADING);

        if (questionRepo == null){
            questionRepo = QuestionRepositoryFirestoreImpl.getInstance();
        }

        // We cant do this in the constructor because these values might change after the view
        // model has been updated
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication());
        questionSearchFilter.setIncludeCompleted(prefs.getBoolean(PreferenceUtils.QUESTION_FILTER_INCLUDE_COMPLETED, false));
        questionSearchFilter.setIncludeNotCompleted(true);

        questionRepo.getQuestions(questionSearchFilter, new QuestionQueryCallback() {
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