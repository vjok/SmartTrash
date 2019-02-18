package com.example.smarttrash2;

import android.os.AsyncTask;
import android.widget.TextView;

import com.google.android.libraries.places.api.model.Place;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class JSONReaderTask extends AsyncTask<String, Void, JSONObject> {

    @Override
    protected JSONObject doInBackground(String... urls) {
        StringBuffer response = new StringBuffer();
        URL url = null;
        try
        {
            url = new URL(urls[0]);
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        HttpURLConnection conn = null;

        try{
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

            int status = conn.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("Post failed with error code " + status);
            } else {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        String returnString = response.toString();
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(returnString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        JSONArray jsonArray = null;


        try {
            jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                PlaceSearchResult result = new PlaceSearchResult();

                if(object.has("name")) {
                    result.setName(object.getString("name"));
                }
                if(object.has("formatted_address")){
                    result.setAddress(object.getString("formatted_address"));
                }
                if(object.has("rating")){
                    result.setRating(object.getString("rating"));
                }

                result.setPlaceId(object.getString("place_id"));
                MainActivity.places.add(result);
            }
            MainActivity.placeSearch.setPhoneNumbers();
        }catch (JSONException e) {
                e.printStackTrace();
            }
    }


}
