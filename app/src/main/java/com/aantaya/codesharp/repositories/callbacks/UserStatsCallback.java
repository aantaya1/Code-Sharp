package com.aantaya.codesharp.repositories.callbacks;

import com.aantaya.codesharp.models.UserStatsModel;

public interface UserStatsCallback {
    void onSuccess(UserStatsModel stats);
    void onFailure(String failureString);
}
