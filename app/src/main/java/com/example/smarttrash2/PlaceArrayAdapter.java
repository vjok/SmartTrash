package com.example.smarttrash2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PlaceArrayAdapter extends ArrayAdapter<PlaceSearchResult> {
    public PlaceArrayAdapter(Context context, ArrayList<PlaceSearchResult> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        PlaceSearchResult place = getItem(position);
        int layoutId = R.layout.place_layout;
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(layoutId,parent,false);
        }
        TextView textViewName = convertView.findViewById(R.id.textViewName);
        TextView textViewAddress = convertView.findViewById(R.id.textViewAddress);
        TextView textViewPhoneNumber = convertView.findViewById(R.id.textViewNumber);
        TextView textViewRating = convertView.findViewById(R.id.textViewRating);

        textViewName.setText(place.getName());
        textViewAddress.setText(place.getAddress());
        textViewPhoneNumber.setText(place.getPhoneNumber());
        textViewRating.setText(place.getRating());

        return convertView;
    }
}
