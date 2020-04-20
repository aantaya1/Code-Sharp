package com.aantaya.codesharp.repositories.api;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionSearchFilter;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.repositories.callbacks.IdQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.SystemStatsCallback;
import com.aantaya.codesharp.repositories.callbacks.UserStatsCallback;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * API that defines the operations that a question repository implementation should provide.
 */
public interface QuestionRepository {

    /**
     * Get a set of question ids that represent the questions a user has successfully
     * finished
     *
     * @param callback will be called on the conclusion of query
     */
    void getCompletedQuestions(IdQueryCallback callback);

    /**
     * Get a question from it's id.
     *
     * @param id the id of the question
     * @return the question model if one can be found, else null
     */
    @Nullable
    void getQuestion(String id, QuestionQueryCallback callback);

    /**
     * Get questions that match the given question ids. If questionIds is null, get all of the
     * questions.
     *
     * @param filter for filtering the questions retrieved from query
     * @param callback will be called on the conclusion of query
     */
    void getQuestions(@NonNull QuestionSearchFilter filter, @NonNull QuestionQueryCallback callback);

    /**
     * Mark a question as being completed in the repository impl.
     *
     * @param questionId question id that was successfully completed
     * @param difficulty the difficulty of the question completed
     */
    void uploadCompletedQuestion(@NonNull String questionId, @NonNull QuestionDifficulty difficulty);

    /**
     * Get systems stats such as the number of questions in our datastore
     *
     * @param callback will be called upon completion of the query
     */
    void getSystemStats(SystemStatsCallback callback);

    /**
     * Get user stats such as the number of easy/med/hard questions the user has finished
     *
     * @param callback will be called upon completion of the query
     */
    void getUserStats(UserStatsCallback callback);
}
