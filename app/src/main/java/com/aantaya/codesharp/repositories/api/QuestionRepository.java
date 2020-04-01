package com.aantaya.codesharp.repositories.api;

import com.aantaya.codesharp.models.QuestionModel;

import java.util.Set;

/**
 * API that defines the operations that a question repository implementation should provide.
 */
public interface QuestionRepository {
    /**
     * Get an instance of the repository.
     *
     * @return an instance of the QuestionRepository
     */
    QuestionRepository getInstance();

    /**
     * Get a question using it's id.
     *
     * @param id the id if the question
     * @return the question model if one can be found, else null
     */
    QuestionModel getQuestion(String id);

    /**
     * Get all of the questions that match the set of ids.
     *
     * @param ids a set of ids to retrieve
     * @return a set of question models that were retrieved in the datastore
     */
    Set<QuestionModel> getQuestions(Set<String> ids);

    /**
     * Get the ids for all questions
     *
     * @return a set of ids that represent all of the questions in the datastore
     */
    Set<String> getQuestionIds();
}
