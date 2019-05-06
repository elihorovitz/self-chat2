package com.example.self_chat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

class Message {
    private static int IdCounter = 0;
    private int id;
    private String time;


    final String content;

    Message(String msg)
    {
        this.content = msg;
        setIdCounter();
        this.time = initliazeTime();
    }

    Message(String msg, int id, String timestamp)
    {
        this.content = msg;
        this.id = id;
        this.time = timestamp;
        updateIdcounter();
    }

    private void updateIdcounter() {
        if (this.id > IdCounter)
            this.IdCounter = this.id;
    }


    private void setIdCounter() {
        IdCounter++;
        id = IdCounter;
    }



    private String initliazeTime() {
        long millis = System.currentTimeMillis()/1000;
//        Calendar c = Calendar.getInstance();
//        c.setTimeZone(TimeZone.getTimeZone("GMT+2"));
//        c.setTimeInMillis(millis);
//        int hours = c.get(Calendar.HOUR_OF_DAY);
//        int minutes = c.get(Calendar.MINUTE);
//        int seconds = c.get(Calendar.SECOND);
//        String minutesFormat, secondsFormat;
//        if (minutes < 10) {
//            minutesFormat = "0" + minutes;
//        }
//        else {
//            minutesFormat = String.valueOf(minutes);
//        }
//        if (seconds < 10) {
//            secondsFormat = "0" + seconds;
//        }
//        else {
//            secondsFormat = String.valueOf(seconds);
//        }
//        return String.format("%d:%s:%s", hours, minutesFormat, secondsFormat);
        return String.valueOf(millis);
    }

    String getTime() {
        return time;
    }

    int getId() {
        return id;
    }
}
