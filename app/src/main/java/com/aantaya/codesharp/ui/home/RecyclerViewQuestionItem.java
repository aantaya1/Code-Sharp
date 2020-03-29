package com.aantaya.codesharp.ui.home;

import com.aantaya.codesharp.enums.QuestionDifficulty;

public class RecyclerViewQuestionItem {
    private int questionId;
    private String questionTitle;
    private QuestionDifficulty questionDifficulty;

    /**
     * Default constructor
     *
     * @param questionId if of the question (will be used to open question onclick)
     * @param questionTitle what will be displayed in recyclerview
     * @param questionDifficulty used for determining the color to display next to question
     */
    public RecyclerViewQuestionItem(int questionId, String questionTitle,
                                    QuestionDifficulty questionDifficulty) {
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.questionDifficulty = questionDifficulty;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
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
}
