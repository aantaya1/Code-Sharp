package com.aantaya.codesharp.repositories.callbacks;

import com.aantaya.codesharp.models.SystemStatsModel;

public interface SystemStatsCallback {
    void onSuccess(SystemStatsModel stats);
    void onFailure(String failureString);
}
