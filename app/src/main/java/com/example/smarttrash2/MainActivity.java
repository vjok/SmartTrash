package com.example.omistaja.trashcan;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    ImageView imageViewFill = null;
    ImageView imageViewBorder = null;
    TextView textViewStatus = null;
    TextView textViewFullness = null;
    ListView listViewPlaces = null;
    TextView editTextPhoneNumber = null;
    TextView textViewName = null;
    ClipDrawable clipDrawable = null;
    Drawable fillImg = null;
    int trashcanSpaceIndicatorLevel = 0;

    ArrayList<PlaceSearchResult> places = new ArrayList<PlaceSearchResult>();
    PlaceArrayAdapter adapter;
    Trashcan trashcan;

    String location = "65.0051767,25.5114207";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        trashcan = new Trashcan(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            //update the Trashcan information
            trashcan = new Trashcan(this);
            pullToRefresh.setRefreshing(false);
        });

        listViewPlaces = findViewById(R.id.listViewPlaces);
        listViewPlaces.setVisibility(View.INVISIBLE);
        listViewPlaces.setOnItemClickListener((parent, view, position, id) -> {
            String phoneNumber = places.get(position).getPhoneNumber();
            if(!phoneNumber.equals("Can't find number")) {
                editTextPhoneNumber.setText(phoneNumber);
            }
        });

        imageViewFill = findViewById(R.id.imageViewTrashCanFill);
        imageViewBorder = findViewById(R.id.imageViewTrashCanBorder);

        textViewStatus = findViewById(R.id.textViewStatus);
        textViewFullness = findViewById(R.id.textViewContent);
        textViewName = findViewById(R.id.textViewName);

        editTextPhoneNumber = findViewById(R.id.editTextCall);

        findViewById(R.id.buttonCall).setOnClickListener(this);

        //Setting up the indicator
        fillImg = ContextCompat.getDrawable(this, R.drawable.ic_trash_fill);
        clipDrawable = (ClipDrawable) ContextCompat.getDrawable(this, R.drawable.clip_source);
        Drawable border = ContextCompat.getDrawable(this,R.drawable.ic_trash_border);
        border.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));

        //Searching for places nearby
        new PlaceSearchTask(this).execute(location);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonCall){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + editTextPhoneNumber.getText().toString()));
            startActivity(intent);
        }
    }

    public void setTrashcanIndicatorProgress(int level) {
        ClipDrawable drawable = (ClipDrawable) imageViewFill.getDrawable();

        if( level >= 10000){ level = 10000; }
        if( level <= 0){ level = 0; }
        drawable.setLevel(level);
        this.trashcanSpaceIndicatorLevel = level;

        int percent = (int)((double)level/10000*100);
        textViewStatus.setText(Integer.toString(percent)+ "%");

        int content = (int) ((double)percent/100 * 200);
        textViewFullness.setText(Integer.toString(content)+ "L");

        if (level <= 5000){
            listViewPlaces.setVisibility(View.INVISIBLE);
        }else{
            listViewPlaces.setVisibility(View.VISIBLE);
        }
    }

    public void updateTrashcanData(Trashcan trashcan) {
        setTrashcanIndicatorProgress(trashcan.getPercentFull()*100);
        textViewName.setText(trashcan.getName());

    }

    public void setPlaces(ArrayList<PlaceSearchResult> results){
        places = results;
        adapter = new PlaceArrayAdapter(this, places);
        listViewPlaces.setAdapter(adapter);
    }
}