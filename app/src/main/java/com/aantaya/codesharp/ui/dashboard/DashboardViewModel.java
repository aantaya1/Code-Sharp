package com.aantaya.codesharp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.aantaya.codesharp.models.ProgressModel;

import javax.annotation.Nullable;

public class DashboardViewModel extends ViewModel {

    private MutableLiveData<ProgressModel> mTotalProgress;
    private MutableLiveData<Integer> mEasyCompleted;
    private MutableLiveData<Integer> mMediumCompleted;
    private MutableLiveData<Integer> mHardCompleted;

    public DashboardViewModel() {

    }

    /**
     * Initialize the viewmodel to start loading data
     */
    public void init(){
        mTotalProgress = new MutableLiveData<>();
        mTotalProgress.setValue(new ProgressModel(130, 1300));

        mEasyCompleted = new MutableLiveData<>();
        mEasyCompleted.setValue(50);

        mMediumCompleted = new MutableLiveData<>();
        mMediumCompleted.setValue(50);

        mHardCompleted = new MutableLiveData<>();
        mHardCompleted.setValue(30);
    }

    public LiveData<ProgressModel> getTotalProgress() {
        return mTotalProgress;
    }

    public LiveData<Integer> getEasyCompleted() {
        return mEasyCompleted;
    }

    public LiveData<Integer> getMediumCompleted() {
        return mMediumCompleted;
    }

    public LiveData<Integer> getHardCompleted() {
        return mHardCompleted;
    }
}
