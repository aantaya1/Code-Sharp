package com.aantaya.codesharp.repositories;

import androidx.lifecycle.MutableLiveData;

import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.ui.home.RecyclerViewQuestionItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implemented as a singleton since we don't want duplicate connections to our
 * remote/local data sources
 */
public class QuestionRepository {

    private static QuestionRepository questionRepository;

    //todo: this will probably be removed once we implement firebase
    private List<QuestionModel> questionItems = new ArrayList<>();

    public static QuestionRepository getInstance(){
        if (questionRepository == null){
            questionRepository = new QuestionRepository();
        }

        return questionRepository;
    }

    public MutableLiveData<List<QuestionModel>> getQuestions(){
        setQuestionItems();

        MutableLiveData<List<QuestionModel>> data = new MutableLiveData<>();
        data.setValue(questionItems);
        return data;
    }

    public MutableLiveData<List<RecyclerViewQuestionItem>> getQuestionsForRecyclerView(){
        List<RecyclerViewQuestionItem> items = new ArrayList<>();
        items.add(new RecyclerViewQuestionItem(1, "Hello", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(2, "World", QuestionDifficulty.MEDIUM));
        items.add(new RecyclerViewQuestionItem(3, "This", QuestionDifficulty.HARD));
        items.add(new RecyclerViewQuestionItem(4, "Cool ubjh av skfdv h vkhjbksdkhfksjdfh Cool ubjh av skfdv h vkhjbksdkhfksjdfh Cool ubjh av skfdv h vkhjbksdkhfksjdfh", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(4, "Cool ubjh av skfdv h vkhjbksdkhfksjdfh", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(4, "Cool ubjh av skfdv h vkhjbksdkhfksjdfh", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(4, "Cool ubjh av skfdv h vkhjbksdkhfksjdfh", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(4, "Cool ubjh av skfdv h vkhjbksdkhfksjdfh", QuestionDifficulty.EASY));
        items.add(new RecyclerViewQuestionItem(4, "Cool ubjh av skfdv h vkhjbksdkhfksjdfh", QuestionDifficulty.EASY));

        MutableLiveData<List<RecyclerViewQuestionItem>> data = new MutableLiveData<>();
        data.setValue(items);
        return data;
    }

    /**
     * TODO: Remove/replace this with firebase API call
     */
    private void setQuestionItems(){

        QuestionModel question;
        QuestionPayload questionPayload;
        Map<ProgrammingLanguage, QuestionPayload> map;

        questionPayload = new QuestionPayload();

        questionPayload.setQuestion("How many times does this for loop iterate?");
        questionPayload.setAnswer("15");
        questionPayload.setHints(new ArrayList<String>());
        questionPayload.setWrongAnswers(new ArrayList<String>());

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA, questionPayload);

        question = new QuestionModel();
        question.setId(1);
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("For Loops");
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);

        //--------------------------------

        questionPayload = new QuestionPayload();

        questionPayload.setQuestion("How many times does this while loop iterate?");
        questionPayload.setAnswer("38");
        questionPayload.setHints(new ArrayList<String>());
        questionPayload.setWrongAnswers(new ArrayList<String>());

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA, questionPayload);

        question = new QuestionModel();
        question.setId(2);
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("While Loops");
        question.setDifficulty(QuestionDifficulty.MEDIUM);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();

        questionPayload.setQuestion("What is Java?");
        questionPayload.setAnswer("A Programming Language");
        questionPayload.setHints(new ArrayList<String>());
        questionPayload.setWrongAnswers(new ArrayList<String>());

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA, questionPayload);

        question = new QuestionModel();
        question.setId(1);
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("What is Java?");
        question.setDifficulty(QuestionDifficulty.HARD);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);
    }
}
