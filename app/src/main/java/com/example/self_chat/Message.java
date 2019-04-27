package com.example.self_chat;

import java.util.ArrayList;
import java.util.List;

class Message {

    static List<Message> getall()
    {
        ArrayList<Message> all = new ArrayList<>();
        all.add(new Message("first"));
        all.add(new Message("second"));
        all.add(new Message("third"));

        return all;
    }

    final String content;

    Message(String msg)
    {
        this.content = msg;
    }


}
