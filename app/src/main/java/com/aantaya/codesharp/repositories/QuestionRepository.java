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
import java.util.Arrays;
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
    private void sendQuestionsToFirebase(){

        QuestionModel question;
        QuestionPayload questionPayload;
        Map<ProgrammingLanguage, QuestionPayload> map;

        questionPayload = new QuestionPayload();
        questionPayload.setQuestion("How do you start an activity in Android?");
        questionPayload.setAnswer("Create a new intent with the current activity and the class of " +
                "the destination activity. Then call startActivity() with that intent.");
        questionPayload.setHints(new ArrayList<>(Arrays.asList("Can Intents help us?")));
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList(
                "Call setContentView() with the new layout xml that we would like to display to the user.",
                "Issue a broadcast to the activity that you would like to start.",
                "Call the onCreate method for the activity that you would like to start, and updated the " +
                        "UI with a call to setContentView()")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA, questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Starting an Android Activity");
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);

        //--------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setQuestion("What are the Android Components?");
        questionPayload.setAnswer("Activities, Services, Broadcast Receivers, and Content Providers");
        questionPayload.setHints(new ArrayList<>(Arrays.asList("App components are the essential " +
                "building blocks of an Android app. Each component is an entry point through which " +
                "the system or a user can enter your app.")));
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList(
                "Intents, Fragments, Views, and ViewModels",
                "Activities and Services",
                "onCreate(), onResume(), onPause(), and onDestroy()")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA, questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Android Components");
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setQuestion("How does the Java garbage collector know when to deallocate memory for a given object?");
        questionPayload.setAnswer("When an object is no longer referenced by any part of your program.");
        questionPayload.setHints(new ArrayList<>(Arrays.asList("Sometimes you should get rid of things " +
                "that you know you will never use again.")));
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList(
                "Once you set all the fields in the object to zero, empty, or null.",
                "Once you have finished using an object regardless of whether or not you still have a reference to the object.",
                "When the JVM runs out of memory on the heap.")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA, questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Java Garbage Collection");
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);

        for (QuestionModel questionModel : questionItems){

        }
    }
}
