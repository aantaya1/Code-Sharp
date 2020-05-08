package com.aantaya.codesharp.repositories.callbacks;

public interface SyncCacheCallback {
    void onSuccess();
    void onFailure(String failureString);
}
