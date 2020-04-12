package com.aantaya.codesharp.repositories.callbacks;

import com.aantaya.codesharp.models.QuestionModel;

import java.util.Set;

public interface QuestionQueryCallback {
    void onSuccess(Set<QuestionModel> questionModels);
    void onFailure(String failureString);
}
