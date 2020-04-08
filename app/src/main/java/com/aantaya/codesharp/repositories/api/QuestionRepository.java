package com.aantaya.codesharp.repositories.api;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * API that defines the operations that a question repository implementation should provide.
 */
public interface QuestionRepository {

    /**
     * Get the ids for questions that match the given filter.
     *
     * @param filter a config object for filtering questions returned by the method or null to
     *               return all question ids
     * @return a set of ids that represent all of the questions in the datastore that match the
     * given filter
     */
    MutableLiveData<Set<String>> getQuestionIds(@Nullable QuestionFilterConfig filter);

    /**
     * Get a question from it's id.
     *
     * todo: i can probably remove userId as an argument because the repo will have that info already
     *
     * @param id the id of the question
     * @return the question model if one can be found, else null
     */
    @Nullable
    MutableLiveData<QuestionModel> getQuestion(String id);

    /**
     * Get a set of RecyclerViewQuestionItem for displaying on the UI. If the given user id
     * is null, we will return all of the questions, else we will return just the questions that
     * the user has not finished yet.
     *
     * todo: consider removing this method and just using a method that retrieves QuestionModels
     *
     * @param userId the user's id or null
     * @return a set of RecyclerViewQuestionItem
     */
    MutableLiveData<List<RecyclerViewQuestionItem>> getQuestionsForRecycleView(@Nullable String userId);
}
