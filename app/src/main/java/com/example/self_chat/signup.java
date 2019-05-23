package com.example.self_chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

public class signup extends AppCompatActivity {
    private static final String SIGNED = "signed in?";
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private EditText editText;
    private DatabaseReference db;
    private FirebaseFirestore store;
    private String user;
    private boolean done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseDatabase.getInstance().getReference();
        done = false;
        store = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
//        editor.clear().apply();

        user = sp.getString(SIGNED, "");
        if (user == "") {
//            new checkForUser().execute();


            DocumentReference docRef = store.collection(getString(R.string.firebaseuser)).document(getString(R.string.firebaseCurr));
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            user = document.getData().get("name").toString();
                            Log.d("getUser", "DocumentSnapshot data: " + document.getData());
                        } else {
                            Log.d("getUser", "No such document");
                        }
                    } else {
                        Log.d("getUser", "get failed with ", task.getException());
                    }
                    countinue();
                }
            });
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void countinue(){

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("getUserTiming", "now=");
        if (user != "") {

            Intent mainActivityIntent = new Intent(this, MainActivity.class);
            mainActivityIntent.putExtra(getString(R.string.username), user);
            startActivity(mainActivityIntent);
            finish(); // close the signup
        } else {
            setContentView(R.layout.activity_signup);
            editText = findViewById(R.id.input_name);
            final Button signupButton = findViewById(R.id.signupButton);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    signupButton.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
                }
            });
        }
    }




//    private class checkForUser extends AsyncTask<Void, Void, Void>
//    {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            store.collection(getString(R.string.firebaseuser).toString())
//                    .get()
//                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            if (task.isSuccessful()) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Log.d("getUserasync", document.getData().toString());
//                                    if (document.getData().get("name").toString() != null)
//                                        signup.this.user = document.getData().get("name").toString();
////                                    MainActivity.this.msgs.add(new Message(document.getData().get("content").toString(), Integer.parseInt(document.getId()), document.getData().get("time").toString()));
//                                    return;
//                                }
////                                updateLocalDB(false);
//                            } else {
//                                Log.w("getUserasync", "Error getting documents.", task.getException());
//                            }
//                        }
//                    });
//            return null;
//        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("getUser", signup.this.user);
//            signup.this.countinue();
        }
    }




    public void signUpUser(View view) {
        editText = findViewById(R.id.input_name);
        String user = editText.getText().toString();
        editor = sp.edit();
        editor.putString(SIGNED, user);
        editor.apply();
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        mainActivityIntent.putExtra(getString(R.string.username), user);
        adduserToFirebase();
        startActivity(mainActivityIntent);
        finish();
    }

    public void skipSignup(View view) {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }



    private void adduserToFirebase(){

        Map<String, Object> messageObject = new HashMap<>();
        editText = findViewById(R.id.input_name);
        String user = editText.getText().toString();
        messageObject.put("name", user);
        store.collection(getString(R.string.firebaseuser)).document(getString(R.string.firebaseCurr).toString())
                .set(messageObject, SetOptions.merge());
    }







}


