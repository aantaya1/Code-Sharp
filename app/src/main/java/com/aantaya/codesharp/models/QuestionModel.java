package com.aantaya.codesharp.models;

import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuestionModel {
    private int id;
    private String questionTitle;
    private QuestionType questionType;
    private Map<ProgrammingLanguage, QuestionPayload> questionPayloadMap;
    private QuestionDifficulty difficulty;
    private List<String> tags;
    private Date modified;

    public QuestionModel() {
        //blank constructor
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public Map<ProgrammingLanguage, QuestionPayload> getQuestionPayloadMap() {
        return questionPayloadMap;
    }

    public void setQuestionPayloadMap(Map<ProgrammingLanguage, QuestionPayload> questionPayloadMap) {
        this.questionPayloadMap = questionPayloadMap;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<java.lang.String> getTags() {
        return tags;
    }

    public void setTags(List<java.lang.String> tags) {
        this.tags = tags;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
