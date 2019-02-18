package com.example.omistaja.trashcan;

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class PlaceSearch {
    private String apiKey = "AIzaSyA1QE0uid23m3srDFFi1gfnLW8GzXpTGFU";
    PlacesClient placesClient;
    private static boolean updateOnProgress = false;


    public void searchPlaces(Context context, String location) {
        setUpdateOnProgress(true);
        Locale locale = new Locale("fi");

        if(location.equals("")){
            location = "65.0051767,25.5114207";
        }

        Places.initialize(context, apiKey, locale);

        placesClient = Places.createClient(context);
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/" +
                "json?query=J%C3%A4tehuolto&location="+ location + "&radius=10000&" +
                "language=fi&fields=formatted_address,name,rating,opening_hours&" +
                "key=AIzaSyA1QE0uid23m3srDFFi1gfnLW8GzXpTGFU";
        new JSONReaderTask().execute(url);
    }


    public void setPhoneNumbers(){
        List<Place.Field> placeFields = Arrays.asList(Place.Field.PHONE_NUMBER);

        for(int i = 0; i < MainActivity.places.size(); i++)
        {
            final int index = i;
            PlaceSearchResult placeSearchResult = MainActivity.places.get(i);
            String placeId = placeSearchResult.getPlaceId();
            FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
            Task<FetchPlaceResponse> fetchPlaceResponseTask = placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                Place place = response.getPlace();
                String number = place.getPhoneNumber();
                if(number != null && !number.isEmpty()) {
                    MainActivity.places.get(index).setPhoneNumber(number);
                }else
                {
                    MainActivity.places.get(index).setPhoneNumber("Can't find number");
                }
                if(index == MainActivity.places.size()-1)
                {
                    setUpdateOnProgress(false);
                }
            });
        }
    }

    //  0 x
    //  1 x
    //  2
    //  3
    //  4


    public static void setUpdateOnProgress(boolean updateOnProgress) {
        if(PlaceSearch.updateOnProgress == true && updateOnProgress == false )
        {
            MainActivity.updatePlaces();
        }

        PlaceSearch.updateOnProgress = updateOnProgress;
    }

    public static boolean isUpdateOnProgress() {
        return updateOnProgress;
    }
}
