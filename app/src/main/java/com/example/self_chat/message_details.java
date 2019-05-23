package com.example.self_chat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;

public class message_details extends AppCompatActivity {
    Message currmsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        currmsg =(Message) getIntent().getSerializableExtra(getString(R.string.message));
        TextView content = findViewById(R.id.msg_content);
        TextView time = findViewById(R.id.msg_time);
        TextView model = findViewById(R.id.msg_model);
        TextView manufacturer = findViewById(R.id.msg_manufacturer);
        content.setText(currmsg.content);
        String date = (String) DateFormat.format("dd-MM-yyyy", Integer.parseInt(currmsg.getTime()));
        time.setText(date);
        model.setText(currmsg.getModel());
        manufacturer.setText(currmsg.getManufacturer());
    }

    public void delete(View view) {
        Intent mainintent = new Intent(this, MainActivity.class);
        mainintent.putExtra(getString(R.string.message),  currmsg);
        setResult(Activity.RESULT_OK, mainintent);
        finish();
    }

    public void back(View view) {
        Intent mainintent = new Intent(this, MainActivity.class);
        setResult(Activity.RESULT_CANCELED, mainintent);
        finish();
    }
}
