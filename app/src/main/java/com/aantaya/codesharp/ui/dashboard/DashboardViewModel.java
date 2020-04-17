package com.aantaya.codesharp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.ProgressModel;
import com.aantaya.codesharp.models.SystemStatsModel;
import com.aantaya.codesharp.models.UserStatsModel;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.callbacks.SystemStatsCallback;
import com.aantaya.codesharp.repositories.callbacks.UserStatsCallback;
import com.aantaya.codesharp.repositories.impl.QuestionRepositoryFirestoreImpl;

import javax.annotation.Nullable;

public class DashboardViewModel extends ViewModel {

    public static final int STATE_NORMAL = 0;
    public static final int STATE_LOADING = 1;
    public static final int STATE_FAILED = 2;

    private MutableLiveData<ProgressModel> mTotalProgress = new MutableLiveData<>();
    private MutableLiveData<UserStatsModel> mUserStats = new MutableLiveData<>();
    private MutableLiveData<Integer> mTotalNumberOfQuestions = new MutableLiveData<>();
    private MutableLiveData<Integer> mState = new MutableLiveData<>();

    private final int totalQueries = 2;
    private int completedQueries = 0;

    QuestionRepository questionRepo;

    /**
     * Initialize the viewmodel to start loading data
     */
    public void init(){

        // We don't want to init more than once
        if (questionRepo != null) return;

        questionRepo = QuestionRepositoryFirestoreImpl.getInstance();

        mState.setValue(STATE_LOADING);

        //First we will load the the user's stats, and then we will load the system stats
        // we are nesting the callbacks because in order to
        questionRepo.getUserStats(new UserStatsCallback() {
            @Override
            public void onSuccess(UserStatsModel userStats) {
                mUserStats.setValue(userStats);

                if (++completedQueries == totalQueries){
                    finishedInit();
                }
            }

            @Override
            public void onFailure(String failureString) {
                mState.setValue(STATE_FAILED);
            }
        });

        questionRepo.getSystemStats(new SystemStatsCallback() {
            @Override
            public void onSuccess(SystemStatsModel stats) {
                mTotalNumberOfQuestions.setValue(stats.getNumTotalQuestions());

                if (++completedQueries == totalQueries){
                    finishedInit();
                }
            }

            @Override
            public void onFailure(String failureString) {
                mState.setValue(STATE_FAILED);
            }
        });
    }

    /**
     * This method should only be called once all of the callbacks have completed
     */
    private void finishedInit(){
        //this should never happen
        if (completedQueries != totalQueries) return;

        UserStatsModel userStats = mUserStats.getValue();

        int totalCompleted = userStats.getNumEasyCompleted() + userStats.getNumMediumCompleted() + userStats.getNumHardCompleted();
        mTotalProgress.setValue(new ProgressModel(totalCompleted, mTotalNumberOfQuestions.getValue()));
        mState.setValue(STATE_NORMAL);
    }

    public LiveData<ProgressModel> getTotalProgress() {
        return mTotalProgress;
    }

    public LiveData<UserStatsModel> getUserStats(){
        return mUserStats;
    }

    public LiveData<Integer> getState(){
        return mState;
    }
}
