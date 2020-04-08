package com.aantaya.codesharp.models;

public class QuestionFilterConfig {
    private boolean includeCompletedQuestions;

    public QuestionFilterConfig(){

    }

    public boolean includeCompletedQuestions() {
        return includeCompletedQuestions;
    }

    public void setIncludeCompletedQuestions(boolean includeCompletedQuestions) {
        this.includeCompletedQuestions = includeCompletedQuestions;
    }
}
