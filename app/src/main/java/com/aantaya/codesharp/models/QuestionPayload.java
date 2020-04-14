package com.aantaya.codesharp.models;

import com.aantaya.codesharp.enums.ProgrammingLanguage;

import java.util.List;

/**
 * This model defines what an actual question is. This is required because a given Question
 * can be asked in multiple different programming languages. This allows us to map a specific
 * question to a QuestionPayload based on it's ProgrammingLanguage. Thus, any question can have
 * multiple question payloads associated to it; one for each language the question was written
 * in.
 *
 * See the attribute questionPayload in QuestionModel
 *
 */
public class QuestionPayload {
    private ProgrammingLanguage programmingLanguage;
    private String question;
    private List<String> hints;
    private List<String> wrongAnswers;
    private String answer;

    public QuestionPayload() {
        //blank constructor
    }

    public QuestionPayload(ProgrammingLanguage programmingLanguage, String question, List<String> hints, List<String> wrongAnswers, String answer) {
        this.programmingLanguage = programmingLanguage;
        this.question = question;
        this.hints = hints;
        this.wrongAnswers = wrongAnswers;
        this.answer = answer;
    }

    public ProgrammingLanguage getProgrammingLanguage() {
        return programmingLanguage;
    }

    public void setProgrammingLanguage(ProgrammingLanguage programmingLanguage) {
        this.programmingLanguage = programmingLanguage;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<String> getHints() {
        return hints;
    }

    public void setHints(List<String> hints) {
        this.hints = hints;
    }

    public List<String> getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(List<String> wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
