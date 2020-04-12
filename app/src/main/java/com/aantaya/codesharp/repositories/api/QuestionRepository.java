package com.aantaya.codesharp.repositories.api;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.repositories.callbacks.IdQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;

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
     * todo: i can probably remove userId as an argument because the repo will have that info already
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
     * @param questionIds list of questions to retrieve or null to get all questions
     * @param callback will be called on the conclusion of query
     */
    void getQuestions(@Nullable List<String> questionIds, QuestionQueryCallback callback);

    /**
     * Mark a question as being completed in the repository impl.
     *
     * @param questionId question id that was successfully completed
     */
    void uploadCompletedQuestion(@NonNull String questionId);
}
