package com.aantaya.codesharp.models;

public class ProgressModel {
    private int numCompleted;
    private int total;

    public ProgressModel(int numCompleted, int total) {
        this.numCompleted = numCompleted;
        this.total = total;
    }

    public int getNumCompleted() {
        return numCompleted;
    }

    public void setNumCompleted(int numCompleted) {
        this.numCompleted = numCompleted;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
