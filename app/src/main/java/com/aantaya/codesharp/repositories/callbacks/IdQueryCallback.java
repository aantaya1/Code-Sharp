package com.aantaya.codesharp.repositories.callbacks;

import java.util.Set;

public interface IdQueryCallback {
    void onSuccess(Set<String> ids);
    void onFailure(String failureString);
}
