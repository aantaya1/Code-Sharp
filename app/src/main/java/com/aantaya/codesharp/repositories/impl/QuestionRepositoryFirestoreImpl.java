package com.aantaya.codesharp.repositories.impl;

import android.util.Log;

import androidx.annotation.NonNull;

import com.aantaya.codesharp.BuildConfig;
import com.aantaya.codesharp.enums.ProgrammingLanguage;
import com.aantaya.codesharp.enums.QuestionDifficulty;
import com.aantaya.codesharp.enums.QuestionType;
import com.aantaya.codesharp.models.QuestionModel;
import com.aantaya.codesharp.models.QuestionPayload;
import com.aantaya.codesharp.models.QuestionSearchFilter;
import com.aantaya.codesharp.models.SystemStatsModel;
import com.aantaya.codesharp.models.UserStatsModel;
import com.aantaya.codesharp.repositories.api.QuestionRepository;
import com.aantaya.codesharp.repositories.callbacks.IdQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.QuestionQueryCallback;
import com.aantaya.codesharp.repositories.callbacks.SystemStatsCallback;
import com.aantaya.codesharp.repositories.callbacks.UserStatsCallback;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;
import javax.security.auth.callback.Callback;

public class QuestionRepositoryFirestoreImpl implements QuestionRepository {
    private static final String TAG = QuestionRepositoryFirestoreImpl.class.getSimpleName();
    private static final String QUESTION_COLLECTION = "questions";
    private static final String STATS_COLLECTION = "stats";
    private static final String COMPLETED_QUESTION_COLLECTION = "completed_questions";

    private static final String STATS_QUESTION_DOC = "questions";

    private static final String QUESTION_ID_FIELD = "question_ids";
    private static final String STATS_NUM_QUESTIONS_FIELD = "numQuestions";
    private static final String USER_STATS_NUM_EASY_FIELD = "num_easy";
    private static final String USER_STATS_NUM_MED_FIELD = "num_med";
    private static final String USER_STATS_NUM_HARD_FIELD = "num_hard";

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

                        normalizeQuestionStringEol(questionModel);

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
     * @param filter for filtering the questions retrieved from query
     * @param callback    will be called on the conclusion of query
     */
    @Override
    public void getQuestions(@NonNull QuestionSearchFilter filter, QuestionQueryCallback callback) {

        if (filter.includeCompleted() && filter.includeNotCompleted()){
            getAllQuestions(callback);
        }else if (filter.includeCompleted()){
            //todo: in the future we might want to get just the completed questions
        } else if (filter.includeNotCompleted()){
            //We want to only load the questions that the user has NOT completed so we need to
            // first get all the questions in the db, and then filter OUT the questions that
            // the user has finished (which we will get from another [nested] query)
            getAllQuestions(new QuestionQueryCallback() {
                @Override
                public void onSuccess(Set<QuestionModel> questionModels) {
                    getCompletedQuestions(new IdQueryCallback() {
                        @Override
                        public void onSuccess(Set<String> ids) {
                            Set<QuestionModel> res = new HashSet<>();

                            for (QuestionModel question : questionModels){
                                //Only include the questions that have not been completed
                                if (!ids.contains(question.getId())){
                                    res.add(question);
                                }
                            }

                            callback.onSuccess(res);
                        }

                        @Override
                        public void onFailure(String failureString) {
                            callback.onFailure("Something went wrong.");
                        }
                    });
                }

                @Override
                public void onFailure(String failureString) {
                    callback.onFailure("Something went wrong.");
                }
            });
        }else {

            if (BuildConfig.DEBUG){
                throw new IllegalArgumentException("Cannot get questions because all filter " +
                        "params are false!");
            }

            callback.onFailure("Something went wrong.");
        }
    }

    private void getAllQuestions(QuestionQueryCallback callback){
        db.collection(QUESTION_COLLECTION)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Set<QuestionModel> res = new HashSet<>();

                        for(QueryDocumentSnapshot document : task.getResult()){
                            QuestionModel questionModel = document.toObject(QuestionModel.class);
                            questionModel.setId(document.getId());

                            normalizeQuestionStringEol(questionModel);

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
     * @param difficulty the difficulty of the question completed
     */
    @Override
    public void uploadCompletedQuestion(@NonNull String questionId, @NonNull QuestionDifficulty difficulty) {
        Map<Object, Object> data = new HashMap<>();
        data.put(QUESTION_ID_FIELD, FieldValue.arrayUnion(questionId));

        db.collection(COMPLETED_QUESTION_COLLECTION)
                .document(user.getUid())
                .set(data, SetOptions.merge());

        //We also need to update the number of easy/med/hard questions the user has finished
        String field;

        if (difficulty.equals(QuestionDifficulty.EASY)) field = USER_STATS_NUM_EASY_FIELD;
        else if (difficulty.equals(QuestionDifficulty.MEDIUM)) field = USER_STATS_NUM_MED_FIELD;
        else field = USER_STATS_NUM_HARD_FIELD;

        data = new HashMap<>();
        data.put(field, FieldValue.increment(1));

        db.collection(COMPLETED_QUESTION_COLLECTION)
                .document(user.getUid())
                .set(data, SetOptions.merge());
    }

    /**
     * Get systems stats such as the number of questions in our datastore
     *
     * @param callback will be called upon completion of the query
     */
    @Override
    public void getSystemStats(SystemStatsCallback callback) {
        db.collection(STATS_COLLECTION)
                .document(STATS_QUESTION_DOC)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null){
                            SystemStatsModel statsModel = new SystemStatsModel();

                            if (document.contains(STATS_NUM_QUESTIONS_FIELD)){
                                statsModel.setNumTotalQuestions(((Long) document.get(STATS_NUM_QUESTIONS_FIELD)).intValue());
                            }

                            callback.onSuccess(statsModel);
                        }else {
                            //todo: need to implement
                            callback.onFailure("");
                        }
                    } else {
                        //todo: need to implement
                        callback.onFailure("");
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Get user stats such as the number of easy/med/hard questions the user has finished
     *
     * @param callback will be called upon completion of the query
     */
    @Override
    public void getUserStats(UserStatsCallback callback) {
        db.collection(COMPLETED_QUESTION_COLLECTION)
                .document(user.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();

                        if (document != null){
                            UserStatsModel statsModel = new UserStatsModel();

                            if (document.contains(USER_STATS_NUM_EASY_FIELD)){
                                statsModel.setNumEasyCompleted(((Long) document.get(USER_STATS_NUM_EASY_FIELD)).intValue());
                            }

                            if (document.contains(USER_STATS_NUM_MED_FIELD)){
                                statsModel.setNumMediumCompleted(((Long) document.get(USER_STATS_NUM_MED_FIELD)).intValue());
                            }

                            if (document.contains(USER_STATS_NUM_HARD_FIELD)){
                                statsModel.setNumHardCompleted(((Long) document.get(USER_STATS_NUM_HARD_FIELD)).intValue());
                            }

                            callback.onSuccess(statsModel);
                        }else {
                            //todo: need to implement
                            callback.onFailure("");
                        }
                    } else {
                        //todo: need to implement
                        callback.onFailure("");
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
    }

    /**
     * Helper method for normalizing the EOL marker for the question strings in a question.
     * The web-client that creates the question might have saved the question with different
     * EOL markers.
     *
     * @param questionModel
     */
    private static void normalizeQuestionStringEol(QuestionModel questionModel){
        for (String key : questionModel.getQuestionPayloadMap().keySet()){
            QuestionPayload payload = questionModel.getQuestionPayloadMap().get(key);
            String questionWithCorrectEOL = payload.getQuestion().replaceAll("\\r\\n?", "\n");
            payload.setQuestion(questionWithCorrectEOL);
        }
    }

    private void incrementQuestionCount(){
        Map<Object, Object> data = new HashMap<>();
        data.put(STATS_NUM_QUESTIONS_FIELD, FieldValue.increment(1));

        db.collection(STATS_COLLECTION)
                .document(STATS_QUESTION_DOC)
                .set(data, SetOptions.merge());
    }

    private void decrementQuestionCount(){
        Map<Object, Object> data = new HashMap<>();
        data.put(STATS_NUM_QUESTIONS_FIELD, FieldValue.increment(-1));

        db.collection(STATS_COLLECTION)
                .document(STATS_QUESTION_DOC)
                .set(data, SetOptions.merge());
    }

    /**
     * TODO: Remove/replace this with firebase API call
     */
    private void sendQuestionsToFirebase(){

        String descriptionMultipleChoice = "Select the best answer to the question and submit.";

        List<QuestionModel> questionItems = new ArrayList<>();

        QuestionModel question;
        QuestionPayload questionPayload;
        Map<String, QuestionPayload> map;

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.MARKDOWN);
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
        map.put(ProgrammingLanguage.MARKDOWN.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Starting an Android Activity");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //--------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.MARKDOWN);
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
        map.put(ProgrammingLanguage.MARKDOWN.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Android Components");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.MARKDOWN);
        questionPayload.setQuestion("How does the Java garbage collector know when to deallocate memory for a given object?");
        questionPayload.setAnswer("When an object is no longer referenced by any part of your program.");
        questionPayload.setHints(new ArrayList<>(Arrays.asList("Sometimes you should get rid of things " +
                "that you know you will never use again.")));
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList(
                "Once you set all the fields in the object to zero, empty, or null.",
                "Once you have finished using an object regardless of whether or not you still have a reference to the object.",
                "When the JVM runs out of memory on the heap.")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.MARKDOWN.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Java Garbage Collection");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.MEDIUM);
        question.setQuestionType(QuestionType.TOPIC_QUESTION);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.JAVA);
        questionPayload.setQuestion("    /**\n" +
                "     * Prints all of the elements in the names list\n" +
                "     * @param names the names we would like to print \n" +
                "     *              to system out \n" +
                "     */\n" +
                "    private void printAllNames(List<String> names){\n" +
                "        for (int i=0; i<=names.size(); i+=1){\n" +
                "            System.out.println(names.get(i));\n" +
                "        }\n" +
                "    }");
        questionPayload.setBugLineNumber(6);
        questionPayload.setHints(new ArrayList<>());
        questionPayload.setWrongAnswers(new ArrayList<>());

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("For loops");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.FIND_THE_BUG);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.JAVA);
        questionPayload.setQuestion("    /**\n" +
                "     * Prints values to system out\n" +
                "     */\n" +
                "    private void printValues(List<String> values){\n" +
                "        System.out.println(\"values\");\n" +
                "    }");
        questionPayload.setHints(new ArrayList<>());
        questionPayload.setAnswer("O(1)");
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList("O(n)", "O(n+1)", "O(n^2)")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Printing values time complexity I");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TIME_COMPLEXITY_ANALYSIS);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.JAVA);
        questionPayload.setQuestion("    /**\n" +
                "     * Prints values to system out\n" +
                "     */\n" +
                "    private void printValues(List<String> values){\n" +
                "        for (String value : values){\n" +
                "            System.out.println(value);\n" +
                "        }\n" +
                "    }");
        questionPayload.setHints(new ArrayList<>());
        questionPayload.setAnswer("O(n)");
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList("O(1)", "O(100)", "O(n^2)")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Printing values time complexity II");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.EASY);
        question.setQuestionType(QuestionType.TIME_COMPLEXITY_ANALYSIS);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //----------------------------------

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.JAVA);
        questionPayload.setQuestion("    /**\n" +
                "     * Prints values to system out\n" +
                "     */\n" +
                "    private void printValues(List<String> values){\n" +
                "        for (int i=0; i<values.size(); i=i*2){\n" +
                "            System.out.println(values.get(i));\n" +
                "        }\n" +
                "    }");
        questionPayload.setHints(new ArrayList<>());
        questionPayload.setAnswer("O(logn)");
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList("O(1)", "O(n)", "O(n^2)", "O(nlogn)")));

        map = new HashMap<>();
        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Printing values time complexity III");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.MEDIUM);
        question.setQuestionType(QuestionType.TIME_COMPLEXITY_ANALYSIS);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        //----------------------------------

        map = new HashMap<>();

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.JAVA);
        questionPayload.setQuestion("    /**\n" +
                "     * Prints values to system out\n" +
                "     */\n" +
                "    private void printValues(List<String> values){\n" +
                "        \n" +
                "        if (values.size() < 20) return;\n" +
                "        \n" +
                "        for (int i = 1; i <= values.size(); i++){\n" +
                "            for(int j = 1; j < 8; j = j * 2) {\n" +
                "                System.out.println(values.get(j));\n" +
                "            }\n" +
                "        }\n" +
                "    }");
        questionPayload.setHints(new ArrayList<>());
        questionPayload.setAnswer("O(nlogn)");
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList("O(20)", "O(20*n)", "O(n^2)", "O(logn)")));

        map.put(ProgrammingLanguage.JAVA.toString(), questionPayload);

        questionPayload = new QuestionPayload();
        questionPayload.setProgrammingLanguage(ProgrammingLanguage.PYTHON);
        questionPayload.setQuestion("def prints_values(values):\n" +
                "    if len(values) < 20:\n" +
                "        return\n" +
                "\n" +
                "    for i in range(1, len(values)):\n" +
                "        for j in (2 ** j for j in range(3)):\n" +
                "            print(j)\n" +
                "\n" +
                "\n" +
                "prints_values([1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1])");
        questionPayload.setHints(new ArrayList<>());
        questionPayload.setAnswer("O(nlogn)");
        questionPayload.setWrongAnswers(new ArrayList<>(Arrays.asList("O(20)", "O(20*n)", "O(n^2)", "O(logn)")));

        map.put(ProgrammingLanguage.PYTHON.toString(), questionPayload);

        question = new QuestionModel();
        question.setQuestionPayloadMap(map);
        question.setQuestionTitle("Printing values time complexity IV");
        question.setDescription(descriptionMultipleChoice);
        question.setDifficulty(QuestionDifficulty.HARD);
        question.setQuestionType(QuestionType.TIME_COMPLEXITY_ANALYSIS);
        question.setTags(new ArrayList<>());

        questionItems.add(question);

        for (QuestionModel questionModel : questionItems){
            incrementQuestionCount();
            db.collection("questions").add(questionModel);
        }
    }

    /**
     * Prints values to system out
     */
    private void printValues(List<String> values){

        if (values.size() < 20) return;

        for (int i = 1; i <= values.size(); i++){
            for(int j = 1; j < 8; j = j * 2) {
                System.out.println(values.get(j));
            }
        }
    }
}
