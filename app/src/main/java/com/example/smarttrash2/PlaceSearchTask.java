package com.example.omistaja.trashcan;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceSearchTask extends AsyncTask<String, Void, ArrayList<PlaceSearchResult>> {

    ArrayList<PlaceSearchResult> foundPlaces = new ArrayList<>();
    WeakReference<MainActivity> contextRef;
    final String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    final String URL_KEY_PART = "&key=AIzaSyA1QE0uid23m3srDFFi1gfnLW8GzXpTGFU";


    PlaceSearchTask(MainActivity context){
        contextRef = new WeakReference<>(context);
    }

    @Override
    protected ArrayList<PlaceSearchResult> doInBackground(String... strings) {
        String location = strings[0];
        String urlForPlaces = BASE_URL +
                "textsearch/json?query=J%C3%A4tehuolto&location="+ location + "&radius=10000" +
                "&language=fi" + URL_KEY_PART;
        URL url = null;
        try {
            url = new URL(urlForPlaces);
        } catch(MalformedURLException e){
            e.printStackTrace();
        }

        searchForPlaces(url);
        return foundPlaces;
    }

    @Override
    protected void onPostExecute(ArrayList<PlaceSearchResult> placeSearchResults) {
        super.onPostExecute(placeSearchResults);
        MainActivity context = contextRef.get();
        final ArrayList<PlaceSearchResult> foundPlacesFinal = foundPlaces;
        context.runOnUiThread(() -> {
            context.setPlaces(foundPlacesFinal);
        });
    }

    private String readJSONFromURL(URL url){
        StringBuffer response = new StringBuffer();

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

        String resultStr = response.toString();

        return resultStr;
    }

    private void searchForPlaces(URL url){
        String JSONStr = readJSONFromURL(url);

        JSONArray jsonArray;
        try {
            JSONObject jsonObject = new JSONObject(JSONStr);
            jsonArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                PlaceSearchResult result = new PlaceSearchResult();

                if (object.has("name")) {
                    result.setName(object.getString("name"));
                }
                if (object.has("formatted_address")) {
                    result.setAddress(object.getString("formatted_address"));
                }
                if (object.has("rating")) {
                    result.setRating(object.getString("rating"));
                }
                String placeId = object.getString("place_id");
                result.setPlaceId(placeId);
                result.setPhoneNumber(searchForPhoneNumber(placeId));
                foundPlaces.add(result);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String searchForPhoneNumber(String placeId){
        StringBuffer response = new StringBuffer();

        String urlForPhoneNumbers = BASE_URL + "details/json?" +
                "placeid="+ placeId +
                "&language=fi" +
                "&fields=formatted_phone_number" + URL_KEY_PART;

        URL url = null;
        try {
            url = new URL(urlForPhoneNumbers);
        } catch(MalformedURLException e){
            e.printStackTrace();
        }

        String JSONStr = readJSONFromURL(url);

        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(JSONStr).getJSONObject("result");
            if(jsonObject.has("formatted_phone_number")){
                return jsonObject.getString("formatted_phone_number");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Can't find number";
    }
}
