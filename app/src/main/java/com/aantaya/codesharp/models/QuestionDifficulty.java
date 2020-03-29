package com.aantaya.codesharp.models;

import com.aantaya.codesharp.R;

public enum QuestionDifficulty {
    EASY,
    MEDIUM,
    HARD;

    public static int getColor(QuestionDifficulty difficulty){
        switch (difficulty){
            case EASY:
                return R.color.green;
            case MEDIUM:
                return R.color.yellow;
            case HARD:
                return R.color.red;
            default:
                return R.color.yellow;
        }
    }
}
