package com.example.predator.tietokanta;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    TextView textView1, textView2 = null;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Trashcan trashcan = new Trashcan();
        textView1 = findViewById(R.id.tw1);

        OkHttpClient client = new OkHttpClient();

        String url = "https://6y0hioo0zc.execute-api.eu-central-1.amazonaws.com/test?id=1";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful())
                {
                    final String myresponse = (String) response.body().string();
                    final String testi = myresponse.substring(1,myresponse.length()-1);

                    //final String pitaisiOllaTallainen = "{\"id\":1, \"name\":\"hep-roskis\"}";

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            String finalString = testi.replace("\\", "");

                            Log.d("testi","Joo: "+testi);
                            Log.d("testi","Testi: "+finalString);
                            Trashcan trashcan = gson.fromJson(testi, Trashcan.class);

                        }
                    });
                }
            }
        });

        textView1.setText("ID: "+trashcan.getId()+" Name: "+trashcan.getName()+"Location: "+trashcan.getLocation());
    }
}
