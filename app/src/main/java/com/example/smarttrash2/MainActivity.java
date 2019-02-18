package com.example.smarttrash2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.smarttrash2.Trashcan.Trashcan;
import com.example.smarttrash2.Trashcan.TrashcanRetrieved;
import com.google.android.gms.common.util.NumberUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TrashcanRetrieved {
    private ImageView imageViewFill = null;
    private ImageView imageViewBorder = null;
    private TextView textViewStatus = null;
    private TextView textViewFullness = null;
    private static ListView listViewPlaces = null;
    private TextView editTextPhoneNumber = null;
    private TextView textViewName = null;

    ClipDrawable clipDrawable = null;
    Drawable fillImg = null;
    private int level = 0;

    public static PlaceSearch placeSearch = new PlaceSearch();
    public static ArrayList<PlaceSearchResult> places = new ArrayList<PlaceSearchResult>();
    static PlaceArrayAdapter adapter;
    public static Context context = null;

    public Trashcan trashcan;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        // tämä kutsuu updateUI funktiota kun se on "valmis"
        trashcan = new Trashcan(this);

        // eli jos tässä teet
        //trashcan.getName() -- se palauttaa NULL, koska Trashcan oloion asettaminen tapahtuu eri threadissa.

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(() -> {
            //update from database
            setLevel(level+500);
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
        editTextPhoneNumber = findViewById(R.id.editTextCall);
        textViewName = findViewById(R.id.textViewName);

        //Setting up the indicator
        fillImg = ContextCompat.getDrawable(this, R.drawable.ic_trash_fill);
        clipDrawable = (ClipDrawable) ContextCompat.getDrawable(this, R.drawable.clip_source);
        Drawable border = ContextCompat.getDrawable(this,R.drawable.ic_trash_border);
        border.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));

        placeSearch.searchPlaces(context,"");
        findViewById(R.id.buttonCall).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.buttonCall){
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + editTextPhoneNumber.getText().toString()));
            startActivity(intent);
        }
    }

    public void setLevel(int level) {
        final int YELLOW = 3500;
        final int RED = 6500;
        ClipDrawable drawable = (ClipDrawable) imageViewFill.getDrawable();

        if( level >= 10000){ level = 10000; }
        drawable.setLevel(level);
        /*
        //Change color, if supported
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if( level >= 10000){
                level = 10000;
            } else if (level > RED ) {
                fillImg.setColorFilter(new PorterDuffColorFilter(Color.RED, PorterDuff.Mode.SRC_ATOP));
            } else if(level > YELLOW) {
                fillImg.setColorFilter(new PorterDuffColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP));
            } else {
                fillImg.setColorFilter(new PorterDuffColorFilter(Color.parseColor("#7fb22c"), PorterDuff.Mode.SRC_ATOP));
            }
            clipDrawable.setDrawable(fillImg);
            imageViewFill.setImageDrawable(clipDrawable);
        }
        */

        int percent = (int)((double)level/10000*100);
        textViewStatus.setText(Integer.toString(percent)+ "%");
        int content = (int) ((double)percent/100 * 200);
        textViewFullness.setText(Integer.toString(content)+ "L");

        if (level <= 5000){
            listViewPlaces.setVisibility(View.INVISIBLE);
        }else{
            placeSearch.searchPlaces(context,"");
            listViewPlaces.setVisibility(View.VISIBLE);
        }
        this.level = level;
    }

    public static void updatePlaces(){
        adapter = new PlaceArrayAdapter(context, places);
        adapter.notifyDataSetChanged();
        listViewPlaces.setAdapter(adapter);
    }

    @Override
    public void updateUI(Trashcan trashcan) {
        Log.d("trascanApp", trashcan.getName());

        //update from database
        setLevel(trashcan.getPercentFull()*100);
        textViewName.setText(trashcan.getName());

    }
}
