package com.example.joe.weatherguide;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import data.PreferenceOpenHelper;
import model.Weather;
import data.JSONWeatherparser;
import data.WeatherHttpClient;
import utilities.Utility;

public class MainActivity extends AppCompatActivity {




    public static final String PLACES_MESSAGE = "com..example.weatherguide2.MESSAGE";

    /**
     * Create a view object for each weather information returned.
     *
     *
     *
     */

    private TextView cityText;
    private TextView temp;
    private ImageView iconView;
    private TextView description;
    private TextView humidity;
    private TextView pressure;
    private TextView wind;
    private TextView sunrise;
    private TextView sunset;
    private TextView updated;


    /**
     * The weather instance will contain the
     *
     *
     */
    Weather weather = new Weather();


    /**
     * Database
     */

    PreferenceOpenHelper db;

    public static final int MY_PERMISSIONS_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        db = new PreferenceOpenHelper(this);


        /**
         * Insert the information for the
         */


        cityText = (TextView) findViewById(R.id.cityText);
        temp = (TextView) findViewById(R.id.tempText);
        iconView = (ImageView) findViewById(R.id.thumbnailIcon);
        description = (TextView) findViewById(R.id.cloudText);
        humidity = (TextView) findViewById(R.id.humidityText);
        pressure = (TextView) findViewById(R.id.pressureText);
        wind = (TextView) findViewById(R.id.windText);
        sunrise = (TextView) findViewById(R.id.sunriseText);
        sunset = (TextView) findViewById(R.id.sunsetText);
        updated = (TextView) findViewById(R.id.lastupdateText);


        /**
         * If permission to use network services or not provide then a
         * prompt must be persented to the user asking for permission.
         * If the user rejects permssion then the application must close,
         * as there no point continueing i.e need location to work and may
         * not be in may be different city
         *
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);

        }

        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {


            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //  makeUseOfNewLocation(location);
                // query the for weather data for new weather in location.

                // NOT NEEDED P
                String newProvider = location.getProvider();

                String lat = Double.toString(location.getLatitude());

                String lon = Double.toString(location.getLongitude());

                renderWeatherData(lat,lon);


            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };





        String provider = locationManager.getBestProvider(new Criteria(), false);

        /**
         * Get a cache version of the location
         *
         */
        // get the cached location
        Location location = locationManager.getLastKnownLocation(provider);




        /**
         * Need updates for change in user location. If a user leaves a city, the application
         * must be notified to change the location to get weather based on their location.
         * First paramenter which service to request update from, this is the network provider
         * given this is the source of our location alreay justified earilier. 0 0 is the time
         *
         * The second is the ferquencey the minium time in interval for notifactions and the third is the minoium change
         * in distance CHANGE FORM 0 TO HIGHER FOR EACH FIND OUT WAHT EACH INCREMENT IS.
         *
         * pass the created location listener which will recive the update.
         * 4 km and 30 minuutes
         */
        locationManager.requestLocationUpdates(provider, (1000 * 60 * 30), 4000, locationListener);


        /**
         * The api only takes the latitude and longitude to two decimal places.
         * Round the two coordinates to appropriate values.
         */
        Double latitude = Math.round(location.getLatitude() * 100) / 100.0;

        Double longitude = Math.round(location.getLongitude() * 100) / 100.0;

        /**
         * Convert the values to string
         *
         */
        String lat = Double.toString(latitude);

        String lon = Double.toString(longitude);

        /**
         * Get and render the weather information.
         *
         */
        renderWeatherData(lat, lon);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST : {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    return;

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }



    public void renderWeatherData(String lat, String log) {

        WeatherTask weatherTask = new WeatherTask();

        weatherTask.execute(new String[]{lat ,log });




    }


    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        URL url = null;

        HttpURLConnection httpURLConnection = null;


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //super.onPostExecute(bitmap);

            iconView.setImageBitmap(bitmap);

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downLoadImage(params[0]);
        }

        private Bitmap downLoadImage(String code){

            try{

                url = new URL(Utility.ICON_URL+code+".png");

                httpURLConnection = (HttpURLConnection) url.openConnection();

                final Bitmap weatherImage = BitmapFactory.decodeStream(httpURLConnection.getInputStream());

                // close the connection
                httpURLConnection.disconnect();

                return weatherImage;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e){
                e.printStackTrace();

            }

            return null;
        }
    }

    private class WeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {

            String data = ((new WeatherHttpClient()).getWeatherData(params[0],params[1]));

            weather = JSONWeatherparser.getWeather(data);

            weather.iconData = weather.currentCondition.getIcon();


            new DownloadImage().execute(weather.iconData);

            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {


            super.onPostExecute(weather);

            DateFormat dateFormat = DateFormat.getTimeInstance();

            String sunRaise = dateFormat.format(new Date(weather.place.getSunrise()));

            String sunSet = dateFormat.format(new Date(weather.place.getSunset()));

            String upDate = dateFormat.format(new Date(weather.place.getLastupdate()));

            DecimalFormat decimalFormat = new DecimalFormat("#.#");

            String tempFormat = decimalFormat.format(weather.temperature.getTemp());

            String humidiytFormat = decimalFormat.format(weather.currentCondition.getHumidity());

            String pressureFormat = decimalFormat.format(weather.currentCondition.getPressure());

            /**
             *
             *
             *
             */
            cityText.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText(tempFormat + "°C");
            //iconView add a picture
            description.setText("Condition : " + weather.currentCondition.getCondition()+ "("+ weather.currentCondition.getDescription() +")");
            humidity.setText("Humidity : " + humidiytFormat + "%");
            pressure.setText("Pressure : " + pressureFormat + " hPa");
            wind.setText("Wind : " + weather.wind.getSpeed()+" mph");
            sunrise.setText("Sunrise : " + sunRaise);
            sunset.setText("Sunset : " + sunSet);
            updated.setText("Last update : " + upDate);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        String itemSelected = (String) item.getTitle();


        switch (itemSelected) {

            case "Light Blue and White" :
                setTheme("#5895ef");
                break;
            case "Dark Blue and White":
                setTheme("#09295e");
                break;
            case "Green and White":
                setTheme("#24b764");
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    public void showDialog(View view){


        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        final View dialog = getLayoutInflater().inflate(R.layout.dialog_details, null);


        final EditText nameDialog = (EditText) dialog.findViewById(R.id.editName);

        final TextView locationView = (TextView) dialog.findViewById(R.id.locationTxt);

        // show the current location and the one that will be stored.
        locationView.setText(cityText.getText().toString());

        Button storeBtnDialog = (Button) dialog.findViewById(R.id.storeBtn);


        /**
         * If the store button is clicked
         *
         */
        storeBtnDialog.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                String name = nameDialog.getText().toString();
                Context context = getApplicationContext();

                String message;

                if(name.isEmpty()){

                    message = "A name must contain at least one character";

                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT );

                    toast.show();

                } else {

                    String city = cityText.getText().toString();

                   boolean inserted =  db.insertLocationData(name, city);

                    if(inserted){

                        message = "Name and location stored";


                    } else {

                        message = "Unable to inserted information!";
                    }

                    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT );

                    toast.show();

                }


            }
        });


        builder.setView(dialog);
        final AlertDialog dialog1 = builder.create();
        dialog1.show();

    }

    public void showPlaces(View view){




        Cursor result = db.getData();

        if(result == null || result.getCount() == 0){
            String message = "No places to show";

            Toast toast = Toast.makeText(getApplicationContext(), message , Toast.LENGTH_SHORT );

            toast.show();

            return;
        }

        Intent intent  = new Intent(this, DisplayPlacesActivity.class);

        StringBuffer places = new StringBuffer();

        while(result.moveToNext()){
            places.append(result.getString(1) + " : " + result.getString(2) + "\n");
        }


        places.toString();

        intent.putExtra(PLACES_MESSAGE, (CharSequence) places);

        startActivity(intent);

    }


     public void setTheme(String backgroundColor){

        View child = findViewById(R.id.cloudText);

        View parent = child.getRootView();

        int color = Color.parseColor(backgroundColor);

        parent.setBackgroundColor(color);

    }

}
