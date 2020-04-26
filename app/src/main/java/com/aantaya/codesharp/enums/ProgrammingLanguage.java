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
    MARKDOWN(3),
    C_P_P(4),
    C(5),
    C_SHARP(6);

    int code;

    ProgrammingLanguage(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
