package com.aantaya.codesharp.models;

public class QuestionFilterConfig {
    private boolean includeCompletedQuestions;
    private boolean includeIncompleteQuestions;

    public QuestionFilterConfig(){

    }

    public boolean includeCompletedQuestions() {
        return includeCompletedQuestions;
    }

    public void setIncludeCompletedQuestions(boolean includeCompletedQuestions) {
        this.includeCompletedQuestions = includeCompletedQuestions;
    }

    public boolean includeIncompleteQuestions() {
        return includeIncompleteQuestions;
    }

    public void setIncludeIncompleteQuestions(boolean includeIncompleteQuestions) {
        this.includeIncompleteQuestions = includeIncompleteQuestions;
    }
}
