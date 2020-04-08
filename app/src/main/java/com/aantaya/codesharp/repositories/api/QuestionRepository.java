package com.aantaya.codesharp.repositories.api;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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
     * Get the user's completed question IDs
     *
     * @return a set of question IDs that represent the questions the user has completed
     */
    MutableLiveData<Set<String>> getUsersCompletedQuestions();

    /**
     * Get the ids for all questions
     *
     * @return a set of ids that represent all of the questions in the datastore
     */
    MutableLiveData<Set<String>> getAllQuestionIds();

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
