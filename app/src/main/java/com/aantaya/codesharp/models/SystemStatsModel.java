package com.aantaya.codesharp.models;

public class SystemStatsModel {
    int numTotalQuestions;

    public SystemStatsModel() {
    }

    public SystemStatsModel(int numTotalQuestions) {
        this.numTotalQuestions = numTotalQuestions;
    }

    public int getNumTotalQuestions() {
        return numTotalQuestions;
    }

    public void setNumTotalQuestions(int numTotalQuestions) {
        this.numTotalQuestions = numTotalQuestions;
    }
}
