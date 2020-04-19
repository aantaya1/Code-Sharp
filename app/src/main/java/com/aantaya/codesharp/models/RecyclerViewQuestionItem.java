package com.aantaya.codesharp.models;

import androidx.annotation.NonNull;

import com.aantaya.codesharp.enums.QuestionDifficulty;

import java.util.Objects;

public class RecyclerViewQuestionItem{
    private String questionId;
    private String questionTitle;
    private QuestionDifficulty questionDifficulty;

    /**
     * Default constructor
     *
     * @param questionId if of the question (will be used to open question onclick)
     * @param questionTitle what will be displayed in recyclerview
     * @param questionDifficulty used for determining the color to display next to question
     */
    public RecyclerViewQuestionItem(@NonNull String questionId, @NonNull String questionTitle,
                                    @NonNull QuestionDifficulty questionDifficulty) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionDifficulty = questionDifficulty;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionDifficulty getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(QuestionDifficulty questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecyclerViewQuestionItem)) return false;
        RecyclerViewQuestionItem that = (RecyclerViewQuestionItem) o;
        return getQuestionId().equals(that.getQuestionId()) &&
                getQuestionTitle().equals(that.getQuestionTitle()) &&
                getQuestionDifficulty() == that.getQuestionDifficulty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQuestionId(), getQuestionTitle(), getQuestionDifficulty());
    }
}
