package com.aantaya.codesharp.models;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class QuestionModel {
    private String id;
    private String questionTitle;
    private String description;
    private QuestionType questionType;
    private Map<String, QuestionPayload> questionPayloadMap;
    private QuestionDifficulty difficulty;
    private List<String> tags;
    private Date modified;

    public QuestionModel() {
        //blank constructor
    }

    public QuestionModel(String questionTitle, String description, QuestionType questionType, Map<String, QuestionPayload> questionPayloadMap, QuestionDifficulty difficulty, List<String> tags, Date modified) {
        this.questionTitle = questionTitle;
        this.description = description;
        this.questionType = questionType;
        this.questionPayloadMap = questionPayloadMap;
        this.difficulty = difficulty;
        this.tags = tags;
        this.modified = modified;
    }

    /**
     * Static helper method for getting the QuestionPayload from a question model that is the
     * user's preferred programming language of if that is not available, then return a random
     * one.
     *
     * @param questionModel the QuestionModel we would like to extract the payload from
     * @param context context so we can get shared prefs
     * @return QuestionPayload associated to user's preferred language, or if that's not available, a random one
     */
    @Nullable
    public static QuestionPayload getPayloadWithPreferredLanguage(@NonNull QuestionModel questionModel, @NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String preferredLanguage = prefs.getString("prog_lang_pref", "java");
        ProgrammingLanguage preferred = ProgrammingLanguage.valueOf(preferredLanguage.toUpperCase().trim());

        if (questionModel.getQuestionPayloadMap().containsKey(preferred.toString())){
            return questionModel.getQuestionPayloadMap().get(preferred.toString());
        }else {
            Iterator<QuestionPayload> i = questionModel.getQuestionPayloadMap().values().iterator();
            return i.next();
        }
    }

    /**
     * Checks if an answer is correct or not
     *
     * @param type the question type for the question payload
     * @param payload the question payload that contains the answers to validate against
     * @param selectedAnswerString the users selected answer string (only
     *                             used if question type != FIND_THE_BUG)
     * @param selectedLineNumber the line the user selected (only used if question type == FIND_THE_BUG)
     * @return
     */
    public static boolean answerIsCorrect(QuestionType type, QuestionPayload payload,
                                          String selectedAnswerString, int selectedLineNumber){
        if (type.equals(QuestionType.FIND_THE_BUG)){
            return payload.getBugLineNumber() == selectedLineNumber;
        }else {
            return payload.getAnswer().equals(selectedAnswerString);
        }
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
