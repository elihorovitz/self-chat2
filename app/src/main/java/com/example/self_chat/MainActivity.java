package com.example.self_chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
    private final int MESSAGE_DETAILS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String userName = getIntent().getStringExtra(getString(R.string.username));
        if (userName != null)
        {
            android.support.v7.widget.AppCompatTextView textView = findViewById(R.id.hi_user);
            textView.setText(userName);
            textView.setVisibility(View.VISIBLE);
        }
        db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
//        downloadFromDB();
        new downloadFromFirebase().execute();
        RecyclerView recyclerView = findViewById(R.id.msg_recycler);
        LinearLayoutManager ll= new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false);
            ll.setStackFromEnd(true);

        recyclerView.setLayoutManager(ll);
        recyclerView.setAdapter(adapter);

        adapter.callback = this;





    }

    private void updateLocalDB(Boolean updateFire)
    {
        editor.putString(MSG_LIST, gson.toJson(msgs));
        editor.apply();
        if (updateFire)
        {
            updateFireDB();
        }
        adapter.submitList(msgs);
    }



    private void updateFireDB()
    {
        String msg_list_json = sp.getString(MSG_LIST, "");
        if (!msg_list_json.equals("")) {
            msgs = gson.fromJson(msg_list_json, new TypeToken<ArrayList<Message>>() {
            }.getType());

            for (Message msg : msgs) {
                Map<String, Object> messageObject = new HashMap<>();
                messageObject.put("content", msg.content);
                messageObject.put("time", msg.getTime());
                messageObject.put("model", msg.getModel());
                messageObject.put("manufacturer", msg.getManufacturer());
                db.collection("messages")
                        .document(String.valueOf(msg.getId())).set(messageObject, SetOptions.merge());
            }
        }
    }


    @Override
    public void onMessageClick(Message message) {
        currMsg = message;
        Intent message_details = new Intent(this, message_details.class);
        message_details.putExtra(getString(R.string.message),  currMsg);


        startActivityForResult(message_details,MESSAGE_DETAILS);

//        new AlertDialog.Builder(this)
//                .setTitle("Positive?")
//                .setMessage("Are you sure?")
//                .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        ArrayList<Message> msgsCopy = new ArrayList<>(MainActivity.this.msgs);
//                        msgsCopy.remove(MainActivity.this.currMsg);
//                        MainActivity.this.msgs = msgsCopy;
//                        editor.putString(MSG_LIST, gson.toJson(MainActivity.this.msgs));
//                        editor.apply();
//                        db.collection("messages").document(String.valueOf(MainActivity.this.currMsg.getId())).delete();
////                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messages");
////                        reference.child(String.valueOf(MainActivity.this.currMsg.getId())).removeValue();
//                        MainActivity.this.adapter.submitList(MainActivity.this.msgs);
//                    }})
//                .setNegativeButton(android.R.string.no, null).show();


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case (MESSAGE_DETAILS) : {
                if (resultCode == Activity.RESULT_OK) {
                    Message msg = (Message) data.getSerializableExtra(getString(R.string.message));
                    ArrayList<Message> msgsCopy = new ArrayList<>(MainActivity.this.msgs);
                    Log.d("msgsLen", String.valueOf(msgs.size()));
                    for(Iterator<Message> iterator = msgsCopy.iterator(); iterator.hasNext(); ) {
                        if(iterator.next().getId() == msg.getId())
                            iterator.remove();
                    }

                    MainActivity.this.msgs = msgsCopy;
                    Log.d("msgsLen", String.valueOf(msgs.size()));
                    editor.putString(MSG_LIST, gson.toJson(MainActivity.this.msgs));
                    editor.apply();
                    db.collection("messages").document(String.valueOf(MainActivity.this.currMsg.getId())).delete();
                    MainActivity.this.adapter.submitList(MainActivity.this.msgs);
                }
                break;
            }
        }
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
        updateLocalDB(true);
        input.setText("");
//        adapter.submitList(msgs);
    }

    public void resetSp(View view) {
        editor.clear().apply();
    }


    private class downloadFromFirebase extends AsyncTask<Void, Void, Void> {
//        private ProgressBar pb = findViewById(R.id.pBar);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pb.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            db.collection("messages")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(FIRETAG, document.getId() + " => " + document.getData().get("content"));
//                                Toast.makeText(MainActivity.this, document.getData().toString(), Toast.LENGTH_SHORT).show();
                                    MainActivity.this.msgs.add(new Message(document.getData().get("content").toString(), Integer.parseInt(document.getId()), document.getData().get("time").toString()
                                    ,document.getData().get("manufacturer").toString(), document.getData().get("model").toString()));
                                }
                                updateLocalDB(false);
                            } else {
                                Log.w(FIRETAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
//            pb.setVisibility(View.INVISIBLE);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }







}
