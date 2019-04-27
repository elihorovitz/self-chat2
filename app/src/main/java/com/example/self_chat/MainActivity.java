package com.example.self_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity
        implements MessageRecyclerUtils.MessageClickCallback {

    private MessageRecyclerUtils.MessageAdapter adapter
            = new MessageRecyclerUtils.MessageAdapter();

    private ArrayList<Message> msgs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.msg_recycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(
                this, LinearLayoutManager.VERTICAL, false));

        recyclerView.setAdapter(adapter);
        adapter.callback = this;

        adapter.submitList(msgs);
    }

    @Override
    public void onMessageClick(Message message) {

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
        input.setText("");
        adapter.submitList(msgs);
    }

}
