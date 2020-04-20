package com.aantaya.codesharp.models;

public class QuestionSearchFilter {
    private boolean includeCompleted = false;
    private boolean includeNotCompleted = false;

    public QuestionSearchFilter(){}

    public QuestionSearchFilter(boolean includeCompleted, boolean includeNotCompleted) {
        this.includeCompleted = includeCompleted;
        this.includeNotCompleted = includeNotCompleted;
    }

    public boolean includeCompleted() {
        return includeCompleted;
    }

    public void setIncludeCompleted(boolean includeCompleted) {
        this.includeCompleted = includeCompleted;
    }

    public boolean includeNotCompleted() {
        return includeNotCompleted;
    }

    public void setIncludeNotCompleted(boolean includeNotCompleted) {
        this.includeNotCompleted = includeNotCompleted;
    }
}
