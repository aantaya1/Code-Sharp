package com.aantaya.codesharp.models;

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
    private String question;
    private List<String> hints;
    private List<String> wrongAnswers;
    private String answer;

    public QuestionPayload() {
        //blank constructor
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
