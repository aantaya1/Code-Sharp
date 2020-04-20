package com.aantaya.codesharp.enums;

/**
 * Supported question types
 */
public enum QuestionType {
    TIME_COMPLEXITY_ANALYSIS(1, "Time Complexity Analysis"),
    SPACE_COMPLEXITY_ANALYSIS(2, "Space Complexity Analysis"),
    TOPIC_QUESTION(3, "Topic Question"),
    FIND_THE_BUG(4, "Find the bug");

    private int code;
    private String name;

    QuestionType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
