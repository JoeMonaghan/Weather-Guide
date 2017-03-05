package com.example.joe.weatherguide;

import android.Manifest;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import data.PreferenceOpenHelper;
import model.Weather;
import data.JSONWeatherparser;
import data.WeatherHttpClient;
import utilities.Utility;

public class MainActivity extends AppCompatActivity {



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


    Weather weather = new Weather();


    PreferenceOpenHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        db = new PreferenceOpenHelper(this);


        /**
         * Insert the information for the
         */

        if(!(db.isInserted())){

            db.insertThemeData(101, "Light Blue and White", "#5892ef", 1);
            db.insertThemeData(102, "Dark Blue and White", "#09295e", 0);
            db.insertThemeData(103, "White and Dark Blue", "#ffffff", 0);
        }

        String color = db.getData("Light Blue and White");


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


        // NEED UPDATES FOR THE LOCAITON CASE THERE IS A CHANGE IN LOCATION OR LOST OF SERVICE
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.v("Need to get permissions", "Not working");

            return;
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



        String lat = Double.toString(location.getLatitude());

        String lon = Double.toString(location.getLongitude());

        Log.v("Lat : "+lat,lon);

        renderWeatherData(lat,lon);

        Log.v("After", "data call");


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





            Log.v("from renderWeather: ", weather.place.getCity() +" & " + weather.place.getCountry());


            cityText.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText(tempFormat + "°C");
            //iconView add a picture
            description.setText("Condition : " + weather.currentCondition.getCondition()+ "("+ weather.currentCondition.getDescription() +")");
            humidity.setText("Humidity : " + humidiytFormat + "%");
            pressure.setText("Pressure : " + pressureFormat + "hPa");
            wind.setText("Wind : " + weather.wind.getSpeed()+"mph");
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

        String color = db.getData(itemSelected);


        switch (itemSelected) {

            case "Light Blue and White" :
                setTheme(color);
                break;
            case "Dark Blue and White":
                setTheme("#09295e");
                break;
            case "White and Dark Blue":
                setTheme("#ffffff");
                break;

        }


        return super.onOptionsItemSelected(item);
    }


     public void setTheme(String backgroundColor){

        View child = findViewById(R.id.cloudText);

        View parent = child.getRootView();

        int color = Color.parseColor(backgroundColor);

        parent.setBackgroundColor(color);

    }

}
