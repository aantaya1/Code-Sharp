package com.aantaya.codesharp.models;

import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class QuestionModel {
    private String id;
    private String questionTitle;
    private QuestionType questionType;
    private Map<String, QuestionPayload> questionPayloadMap;
    private QuestionDifficulty difficulty;
    private List<String> tags;
    private Date modified;

    public QuestionModel() {
        //blank constructor
    }

    public QuestionModel(String questionTitle, QuestionType questionType, Map<String, QuestionPayload> questionPayloadMap, QuestionDifficulty difficulty, List<String> tags, Date modified) {
        this.questionTitle = questionTitle;
        this.questionType = questionType;
        this.questionPayloadMap = questionPayloadMap;
        this.difficulty = difficulty;
        this.tags = tags;
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Map<String, QuestionPayload> getQuestionPayloadMap() {
        return questionPayloadMap;
    }

    public void setQuestionPayloadMap(Map<String, QuestionPayload> questionPayloadMap) {
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
