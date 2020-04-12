package com.aantaya.codesharp.repositories.impl;

import android.util.ArraySet;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;
import com.aantaya.codesharp.models.QuestionFilterConfig;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.models.RecyclerViewQuestionItem;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.callbacks.IdQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

public class QuestionRepositoryFirestoreImpl implements QuestionRepository {
    private static final String TAG = QuestionRepositoryFirestoreImpl.class.getSimpleName();
    private static final String QUESTION_COLLECTION = "questions";
    private static final String COMPLETED_QUESTION_COLLECTION = "completed_questions";
    private static final String QUESTION_ID_FIELD = "question_ids";

    private static QuestionRepositoryFirestoreImpl questionRepository;

    private FirebaseFirestore db;//todo: revisit this, maybe we don't need to keep this connection open
    private FirebaseUser user;

    private QuestionRepositoryFirestoreImpl(){
        db = FirebaseFirestore.getInstance();

        // The default cache size threshold is 100 MB. Configure "setCacheSizeBytes"
        // for a different threshold (minimum 1 MB) or set to "CACHE_SIZE_UNLIMITED"
        // to disable clean-up.
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build();
        db.setFirestoreSettings(settings);

        user = FirebaseAuth.getInstance().getCurrentUser();

//        sendQuestionsToFirebase();
    }

    public static QuestionRepositoryFirestoreImpl getInstance(){
        if (questionRepository == null){
            questionRepository = new QuestionRepositoryFirestoreImpl();
        }

        return questionRepository;
    }

    /**
     * Get the ids for questions that match the given filter.
     *
     * @param filter a config object for filtering questions returned by the method or null to
     *               return all question ids
     * @return a set of ids that represent all of the questions in the datastore that match the
     * given filter
     */
    public MutableLiveData<Set<String>> getQuestionIds(@Nullable QuestionFilterConfig filter) {

        final MutableLiveData<Set<String>> data = new MutableLiveData<>();

        if (filter == null || filter.includeCompletedQuestions()){
            db.collection(QUESTION_COLLECTION)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Set<String> items = new HashSet<>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                items.add(document.getId());
                            }

                            data.postValue(items);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });
        }else {
            db.collection(COMPLETED_QUESTION_COLLECTION)
                    .document(user.getUid())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            List<String> ids = (document != null) ? (List<String>) document.get("question_ids") : new ArrayList<>();

                            Set<String> completedQuestionIds = new HashSet<>();

                            //add all of the completed question ids
                            if (ids != null) completedQuestionIds.addAll(ids);

                            db.collection(QUESTION_COLLECTION)
                                    .get()
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            Set<String> questionIds = new HashSet<>();

                                            // only return the question ids that the user has not completed yet
                                            for (QueryDocumentSnapshot document2 : task2.getResult()) {
                                                if (!completedQuestionIds.contains(document2.getId())){
                                                    questionIds.add(document2.getId());
                                                }
                                            }

                                            data.postValue(questionIds);
                                        } else {
                                            Log.w(TAG, "Error getting documents.", task.getException());
                                        }
                                    });
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });
        }

        return data;
    }

    /**
     * Get a set of question ids that represent the questions a user has successfully
     * finished
     *
     * @param callback will be called on the conclusion of query
     */
    @Override
    public void getCompletedQuestions(IdQueryCallback callback) {
        db.collection(COMPLETED_QUESTION_COLLECTION)
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        List<String> ids = (document != null) ? (List<String>) document.get("question_ids") : new ArrayList<>();

                        Set<String> items = new HashSet<>();

                        if (ids != null) items.addAll(ids);

                        callback.onSuccess(items);
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Get a question from it's id.
     *
     * @param id the id of the question
     * @return the question model if one can be found, else null
     */
    @Nullable
    @Override
    public void getQuestion(String id, QuestionQueryCallback callback) {
        db.collection(QUESTION_COLLECTION).document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        QuestionModel questionModel = document.toObject(QuestionModel.class);
                        questionModel.setId(document.getId());

                        Set<QuestionModel> res = new HashSet<>();
                        res.add(questionModel);

                        callback.onSuccess(res);
                    } else {
                        callback.onFailure("");
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Get questions that match the given question ids. If questionIds is null, get all of the
     * questions.
     *
     * @param questionIds list of questions to retrieve or null to get all questions
     * @param callback    will be called on the conclusion of query
     */
    @Override
    public void getQuestions(@Nullable List<String> questionIds, QuestionQueryCallback callback) {
        db.collection(QUESTION_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<QuestionModel> res = new HashSet<>();

                        for(QueryDocumentSnapshot document : task.getResult()){
                            QuestionModel questionModel = document.toObject(QuestionModel.class);
                            questionModel.setId(document.getId());

                            res.add(questionModel);
                        }

                        callback.onSuccess(res);
                    } else {
                        callback.onFailure("");
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Mark a question as being completed in the repository impl.
     *
     * @param questionId question id that was successfully completed
     */
    @Override
    public void uploadCompletedQuestion(@NonNull String questionId) {
        Map<Object, Object> data = new HashMap<>();
        data.put(QUESTION_ID_FIELD, FieldValue.arrayUnion(questionId));

        db.collection(COMPLETED_QUESTION_COLLECTION)
                .document(user.getUid())
                .set(data, SetOptions.merge());
    }

    /**
     * TODO: Remove/replace this with firebase API call
     */
    private void sendQuestionsToFirebase(){
        List<QuestionModel> questionItems = new ArrayList<>();

        QuestionModel question;
        QuestionPayload questionPayload;
        Map<String, QuestionPayload> map;

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
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

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
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

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
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Java Garbage Collection");
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<String>());

        questionItems.add(question);

        for (QuestionModel questionModel : questionItems){
            db.collection("questions").add(questionModel);
        }
    }
}
