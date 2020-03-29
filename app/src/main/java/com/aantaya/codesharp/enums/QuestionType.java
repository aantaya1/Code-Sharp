package com.aantaya.codesharp.enums;

public enum QuestionType {
    COMPLEXITY_ANAYSIS(1),
    TOPIC_QUESTION(2),
    FIND_THE_BUG(3);

    private int code;

    QuestionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
