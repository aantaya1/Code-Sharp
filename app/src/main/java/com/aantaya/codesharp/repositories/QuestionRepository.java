package com.aantaya.codesharp.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.ui.home.RecyclerViewQuestionItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implemented as a singleton since we don't want duplicate connections to our
 * remote/local data sources
 *
 * TODO: Make this an interface and implement the interface instead (Dagger??)
 *
 */
public class QuestionRepository {

    private static final String TAG = QuestionRepository.class.getSimpleName();

    private static QuestionRepository questionRepository;

    //todo: this will probably be removed once we implement firebase
    private List<QuestionModel> questionItems = new ArrayList<>();
    private FirebaseFirestore db;//todo: revisit this, maybe we don't need to keep this connection open

    private QuestionRepository(){
        db = FirebaseFirestore.getInstance();
    }

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
        MutableLiveData<List<RecyclerViewQuestionItem>> data = new MutableLiveData<>();

        db.collection("questions")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //todo: parse this and update the livedata!
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        data.setValue(new ArrayList<RecyclerViewQuestionItem>());

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
