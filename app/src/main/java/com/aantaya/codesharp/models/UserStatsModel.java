package com.aantaya.codesharp.models;

public class UserStatsModel {
    int numEasyCompleted;
    int numMediumCompleted;
    int numHardCompleted;

    public UserStatsModel() {
    }

    public UserStatsModel(int numEasyCompleted, int numMediumCompleted, int numHardCompleted) {
        this.numEasyCompleted = numEasyCompleted;
        this.numMediumCompleted = numMediumCompleted;
        this.numHardCompleted = numHardCompleted;
    }

    public int getNumEasyCompleted() {
        return numEasyCompleted;
    }

    public void setNumEasyCompleted(int numEasyCompleted) {
        this.numEasyCompleted = numEasyCompleted;
    }

    public int getNumMediumCompleted() {
        return numMediumCompleted;
    }

    public void setNumMediumCompleted(int numMediumCompleted) {
        this.numMediumCompleted = numMediumCompleted;
    }

    public int getNumHardCompleted() {
        return numHardCompleted;
    }

    public void setNumHardCompleted(int numHardCompleted) {
        this.numHardCompleted = numHardCompleted;
    }
}
