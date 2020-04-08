package com.aantaya.codesharp.enums;

/**
 * The supported programming languages
 */
public enum ProgrammingLanguage {
    JAVA(1),
    PYTHON(2),
    MD(3);

    int code;

    ProgrammingLanguage(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
