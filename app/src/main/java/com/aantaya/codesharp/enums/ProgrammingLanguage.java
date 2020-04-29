package com.aantaya.codesharp.enums;

/**
 * The supported programming languages
 */
public enum ProgrammingLanguage {
    //Note: if we add languages here, they must also be added to the settings page
    // and the string value of the enums MUST match the string value of the settings
    // items for programming languages

    JAVA(1, "Java"),
    PYTHON(2, "Python"),
    MARKDOWN(3, "Markdown"),
    C_P_P(4, "C++"),
    C(5, "C"),
    C_SHARP(6, "C#");

    int code;
    String name;

    ProgrammingLanguage(int code, String name){
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName(){
        return name;
    }
}
