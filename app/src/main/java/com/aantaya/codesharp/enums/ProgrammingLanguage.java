package com.aantaya.codesharp.enums;

/**
 * The supported programming languages
 */
public enum ProgrammingLanguage {
    //Note: if we add languages here, they must also be added to the settings page
    // and the string value of the enums MUST match the string value of the settings
    // items for programming languages

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
