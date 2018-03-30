package com.example.joe.weatherguide;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.TextView;

public class DisplayPlacesActivity extends Activity {

    /**
     * REQUIREMENT 10.
     * New activity to display all the stored information in
     * the local database
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_places);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.PLACES_MESSAGE);

        // create the layout's TextView and set the string as its text
        TextView textView = (TextView) findViewById(R.id.placesText);
        textView.setText(message);

    }

}
