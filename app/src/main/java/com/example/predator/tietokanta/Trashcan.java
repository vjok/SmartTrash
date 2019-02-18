package com.example.predator.tietokanta;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Trashcan {

    String location;
    int id;
    String name;
    int percentFull;


    public int getPercentFull() {
        return percentFull;
    }

    public void setPercentFull(int percentFull) {
        this.percentFull = percentFull;
    }

    public Trashcan() {}


    public void setLocation(String location) {
        this.location = location;
    }


    public String getLocation() {
        return location;
    }



    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
