package com.aantaya.codesharp.enums;

import com.aantaya.codesharp.R;

public enum QuestionDifficulty {
    EASY (1),
    MEDIUM (2),
    HARD (3);

    int code;

    QuestionDifficulty(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    /**
     * static helper method for getting the color that is related to the given difficulty level
     * This is primarily used for determining UI elements associated to questions
     *
     * @param difficulty the difficulty we would like to map to a color
     * @return the color that is mapped to the provided difficulty
     */
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
