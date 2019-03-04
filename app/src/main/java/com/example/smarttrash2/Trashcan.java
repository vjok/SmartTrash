package com.example.omistaja.trashcan;

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
    Gson gson = new Gson();

    public int getPercentFull() {
        return percentFull;
    }

    public void setPercentFull(int percentFull) {
        this.percentFull = percentFull;
    }

    public Trashcan(MainActivity context) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://6y0hioo0zc.execute-api.eu-central-1.amazonaws.com/test?id=1";
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("out", "fail");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                {
                    final String myresponse = response.body().string();
                    final String myresponseTrimmed = myresponse.substring(1,myresponse.length()-1);

                    context.runOnUiThread(() -> {
                        Trashcan trashcan = gson.fromJson(myresponseTrimmed, Trashcan.class);
                        context.updateTrashcanData(trashcan);
                    });
                }
            }
        });
    }

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