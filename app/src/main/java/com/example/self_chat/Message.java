package com.example.self_chat;

import android.os.Build;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

class Message implements Serializable {
    private static int IdCounter = 0;
    private int id;
    private String time;
    private String manufacturer;
    private String model;


    final String content;

    Message(String msg)
    {
        this.content = msg;
        setIdCounter();
        this.time = initliazeTime();
        this.manufacturer = Build.MANUFACTURER;
        this.model = Build.MODEL;
    }

    Message(String msg, int id, String timestamp, String manufacturer, String model)
    {
        this.content = msg;
        this.id = id;
        this.time = timestamp;
        this.model = model;
        this.manufacturer = manufacturer;
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
        return String.valueOf(millis);
    }

    String getTime() {
        return time;
    }

    int getId() {
        return id;
    }

    String getManufacturer()
    {
        return manufacturer;
    }

    String getModel()
    {
        return model;
    }
}
