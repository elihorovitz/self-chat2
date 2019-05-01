package com.example.self_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements MessageRecyclerUtils.MessageClickCallback {
    private static final String MSG_LIST = "message list";
    private static final String FIRETAG = "firebaseTag";

    private MessageRecyclerUtils.MessageAdapter adapter
            = new MessageRecyclerUtils.MessageAdapter();
    //todo room
    private ArrayList<Message> msgs = new ArrayList<>();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private FirebaseFirestore db;
    private Gson gson = new Gson();
    private Message currMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        getData();
        accessFirebase();
        RecyclerView recyclerView = findViewById(R.id.msg_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.callback = this;

        adapter.submitList(msgs);
    }


    private void accessFirebase()
    {
        db.collection("messages")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(FIRETAG, document.getId() + " => " + document.getData());
                                Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.w(FIRETAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void getData()
    {
        String msg_list_json = sp.getString(MSG_LIST, "");
        if (!msg_list_json.equals("")) {
            msgs = gson.fromJson(msg_list_json, new TypeToken<ArrayList<Message>>() {
            }.getType());

            for (Message msg : msgs) {
                Map<String, Object> messageObject = new HashMap<>();
                messageObject.put("id", msg.getId());
                messageObject.put("content", msg.content);
                messageObject.put("time", msg.getTime());


                db.collection("messages")
                        .add(messageObject)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d("tag", "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("tag", "Error adding document", e);
                            }
                        });
            }
        }
        Log.d("OnCreate", "length of messages: " + msgs.size());
    }

    @Override
    public void onMessageClick(Message message) {
        currMsg = message;
        new AlertDialog.Builder(this)
                .setTitle("Title")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        ArrayList<Message> msgsCopy = new ArrayList<>(MainActivity.this.msgs);
                        msgsCopy.remove(MainActivity.this.currMsg);
                        MainActivity.this.msgs = msgsCopy;
                        editor.putString(MSG_LIST, gson.toJson(msgs));
                        editor.apply();
                        MainActivity.this.adapter.submitList(MainActivity.this.msgs);
                    }})
                .setNegativeButton(android.R.string.no, null).show();


    }




    public void updateEntry(View view) {
        EditText input = findViewById(R.id.entryinput);
        String inputText = input.getText().toString();
        if (inputText.matches(""))
        {
            Toast.makeText(getApplicationContext(),"you can't send an empty message, oh silly!",Toast.LENGTH_LONG).show();
            return;
        }
        msgs.add(new Message(inputText));
        editor.putString(MSG_LIST, gson.toJson(msgs));
        editor.apply();
        input.setText("");
        adapter.submitList(msgs);
    }

}
