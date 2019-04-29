package com.example.self_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements MessageRecyclerUtils.MessageClickCallback {
    private static final String MSG_LIST = "message list";

    private MessageRecyclerUtils.MessageAdapter adapter
            = new MessageRecyclerUtils.MessageAdapter();
    //todo room
    private ArrayList<Message> msgs = new ArrayList<>();
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    Gson gson = new Gson();
    Message currMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();
        String msg_list_json = sp.getString(MSG_LIST, "");
        if (!msg_list_json.equals(""))
        {
            msgs = gson.fromJson(msg_list_json,new TypeToken<ArrayList<Message>>(){}.getType());
        }
        Log.d("OnCreate", "length of messages: " + msgs.size());
        RecyclerView recyclerView = findViewById(R.id.msg_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.callback = this;

        adapter.submitList(msgs);
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
                        //Toast.makeText(MainActivity.this, "Yaay", Toast.LENGTH_SHORT).show();
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
