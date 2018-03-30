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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.Date;

import data.ThemeOpenHelper;
import model.Weather;
import data.JSONWeatherparser;
import data.WeatherHttpClient;
import utilities.Utility;

public class MainActivity extends AppCompatActivity {


    public static final String PLACES_MESSAGE = "com..example.weatherguide2.MESSAGE";

    /**
     * Create a view object for each weather information returned.
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
     */
    Weather weather = new Weather();


    /**
     * REQUIREMENT 10
     * db instance
     */
    ThemeOpenHelper db;

    public static final int MY_PERMISSIONS_REQUEST = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        /**
         * REQUIREMENT 10
         * db instance
         */
        db = new ThemeOpenHelper(this);

        /*
         * REQUIREMENT 8.
         * Create text view control for each weather information.
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

        /* REQUIREMENT 3.
         * If permission is not already granted by the user to use the internet and location services
         * prompt user to grant them.
         */
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // Display the prompt
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST);

        }

        /* REQUIREMENT 3.
         * Acquire a reference to the system Location Manager to access the
         * system location services. This is needed to get user location.
         */
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        /*
         * REQUIREMENT 3 AND REQUIREMENT 5.
         * Define a listener that will receive notifications from the location manager
         * and respond to the specific changes.
         */
        LocationListener locationListener = new LocationListener() {


            /*
             * REQUIREMENT 5.
             * if the location changes get the new provider and
             * render the weather again.
             */
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                // location will contain the new coordinates.
                String lat = Double.toString(location.getLatitude());

                String lon = Double.toString(location.getLongitude());

                renderWeatherData(lat, lon);

            }

            /*
             * when their is a change in status.
             */
            public void onStatusChanged(String provider, int status, Bundle extras) {
                return;
            }

            /*
             * when the provider is enabled
             */
            public void onProviderEnabled(String provider) {
                return;
            }

            /*
             * when the user disables the provider.
             *
             */
            public void onProviderDisabled(String provider) {
                return;
            }
        };


        /*
         * REQUIREMENT 3.
         * get the provider that the system determines is the best.
         */
        String provider = locationManager.getBestProvider(new Criteria(), false);

        /*
         * REQUIREMENT 3.
         * get the last known location from the provider acquired above.
         * if the provider is disabled then null will be returned.
         */

        Location location = locationManager.getLastKnownLocation(provider);


        /*
         *
         * REQUIREMENT 3.
         * Need updates for change in user location. If a user leaves a city, the application
         * must be notified to change the location to get weather based on their location.
         * The second is the frequency in the time in interval for updates and the third is the minimum change
         * in distance. Pass the created location listener which will receive the update.
         * 4 km and 30 minutes
         */
        locationManager.requestLocationUpdates(provider, (1000 * 60 * 30), 4000, locationListener);


        /*
         * REQUIREMENT 3.
         * Round the two coordinates to two decimal places.
         */
        Double latitude = Math.round(location.getLatitude() * 100) / 100.0;

        Double longitude = Math.round(location.getLongitude() * 100) / 100.0;

        /*
         * REQUIREMENT 3.
         * Convert the values to string.
         */
        String lat = Double.toString(latitude);

        String lon = Double.toString(longitude);

        /*
         * REQUIREMENT 8.
         *
         */
        renderWeatherData(lat, lon);


    }

    /**
     * REQUIREMENT 3.
     * Deal with the user's input for access to system location and internet access.
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // simply return to caller.
                    return;

                } else {

                    // permission denied, so stop the application.
                    android.os.Process.killProcess(android.os.Process.myPid());

                }
                return;
            }


        }
    }


    /**
     * REQUIREMENT 8.
     * Create instance of the weather task.
     * @param lat
     * @param log
     */
    public void renderWeatherData(String lat, String log) {

        WeatherTask weatherTask = new WeatherTask();

        weatherTask.execute(new String[]{lat, log});


    }


    /**
     * REQUIREMENT 7.
     * Downloads image in the background in the UI thread by
     * extending the AsyncTask.
     */
    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
        URL url = null;

        HttpURLConnection httpURLConnection = null;


        @Override
        protected void onPostExecute(Bitmap bitmap) {

            iconView.setImageBitmap(bitmap);

        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downLoadImage(params[0]);
        }

        /**
         * REQUIREMENT 7.
         * Create a connection with the open weather map website and downloads the
         * contents of the url, which is a bitmap image.
         *
         * @param code of image to get.
         * @return a bitmap object of image.
         */
        private Bitmap downLoadImage(String code) {

            try {

                url = new URL(Utility.ICON_URL + code + ".png");

                httpURLConnection = (HttpURLConnection) url.openConnection();

                // convert data into a bitmap instance.
                final Bitmap weatherImage = BitmapFactory.decodeStream(httpURLConnection.getInputStream());

                // close the connection
                httpURLConnection.disconnect();

                return weatherImage;

                // deal with any exceptions.
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * REQUIREMENT 8.
     * Using an async task to allow the connection with
     * the Open Weather Map API to take place in a different thread.
     * This will improve improve of applicaiton
     */
    private class WeatherTask extends AsyncTask<String, Void, Weather> {

        /**
         * REQUIREMENT 8.
         * This task is done in the worker thread this is NOT
         * the same thread as PostExecute which is done in the UIT thread.
         * @param params latitude and longitude.
         * @return weather instance with current weather information.
         */
        @Override
        protected Weather doInBackground(String... params) {


            // create the connection to API and pass in latitude and longitude.
            String data = ((new WeatherHttpClient()).getWeatherData(params[0], params[1]));

            // now parse information from a string to JSON and then a weather instance.
            weather = JSONWeatherparser.getWeather(data);

            // code for image
            weather.iconData = weather.currentCondition.getIcon();

            // get the image by calling execute image.execute
            DownloadImage image = new DownloadImage();
            image.execute(weather.iconData);

            return weather;
        }

        /**
         * REQUIREMENT 8.
         * Once the doInBackground is finished. That is when the
         * weather instance contains all the weather values. Present
         * the information to the user.
         *
         * @param weather instance with uo to date values.
         */
        @Override
        protected void onPostExecute(Weather weather) {


            super.onPostExecute(weather);

            // format for the date information.
            DateFormat dateFormat = DateFormat.getTimeInstance();

            // format and convert all the weather information to a string instances.
            String sunRaise = dateFormat.format(new Date(weather.place.getSunrise()));
            String sunSet = dateFormat.format(new Date(weather.place.getSunset()));
            String upDate = dateFormat.format(new Date(weather.place.getLastupdate()));
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String tempFormat = decimalFormat.format(weather.temperature.getTemp());
            String humidiytFormat = decimalFormat.format(weather.currentCondition.getHumidity());
            String pressureFormat = decimalFormat.format(weather.currentCondition.getPressure());

            // set the text of all text controls in res/layout/content_main.xml to view
            // their corresponding data.
            cityText.setText(weather.place.getCity() + "," + weather.place.getCountry());
            temp.setText(tempFormat + "°C");
            description.setText("Condition : " + weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescription() + ")");
            humidity.setText("Humidity : " + humidiytFormat + "%");
            pressure.setText("Pressure : " + pressureFormat + " hPa");
            wind.setText("Wind : " + weather.wind.getSpeed() + " mph");
            sunrise.setText("Sunrise : " + sunRaise);
            sunset.setText("Sunset : " + sunSet);
            updated.setText("Last update : " + upDate);

        }
    }


    /**
     * REQUIREMENT 9 CHANGE THEME FEATURE.
     * The method is called when the user clicks the option menu icon.
     * The xml file menu_main.xml will be inflated in the top right hand corner
     * of the screen.
     *
     * @param menu
     * @return
     * @see res/menu/menu_main.xml
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * REQUIREMENT 9 CHANGE THEME FEATURE.
     * When a single item is selected, the item's object that is selected will be passed in.
     * Depending on this item by getting its title change the background colour to the
     * necessary colour.
     *
     * @param item
     * @return
     * @see res/menu/menu_main.xml
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // get the title of the selected item.
        String itemSelected = (String) item.getTitle();

        // determine the background colour.
        switch (itemSelected) {

            case "Light Blue":
                setTheme("#5895ef");
                break;
            case "Dark Blue":
                setTheme("#09295e");
                break;
            case "Green":
                setTheme("#24b764");
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * REQUIREMENT 10.
     * When showDialogBtn is clicked this method will be executed.
     * Presents an alert dialog to the user and inflates the necessary xml file.
     *
     * @param view the button clicked
     * @see res/layout/dialog_details.xml
     */
    public void showDialog(View view) {

        // create an instance of the alertDialog class.
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        // crate a view object based on the dialog_details.xml.
        final View dialog = getLayoutInflater().inflate(R.layout.dialog_details, null);

        // the input object to obtain the user's name.
        final EditText nameDialog = (EditText) dialog.findViewById(R.id.editName);

        // a text view object to display the user's curent location based on the
        // open weather map api.
        final TextView locationView = (TextView) dialog.findViewById(R.id.locationTxt);
        locationView.setText(cityText.getText().toString());

        // a instance of the store button.
        Button storeBtnDialog = (Button) dialog.findViewById(R.id.storeBtn);

        // create a listener for the button to listen to its onClick call.
        storeBtnDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // get the imput name,
                String name = nameDialog.getText().toString();

                // for toast used later.
                Context context = getApplicationContext();
                String message;

                // check if the user has input their name.
                if (name.isEmpty()) {
                    message = "A name must contain at least one character";

                } else {
                    // when there is name inserted try to inserted data
                    // and display the appropriate message in the form of
                    // a toast object.
                    String city = cityText.getText().toString();

                    boolean inserted = db.insertNameLocation(name, city);

                    if (inserted) {
                        message = "Name and location stored";

                    } else {
                        message = "Unable to inserted information!";
                    }

                }

                // toast object to inform user of the outcome
                Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        // build, create the view and then display it.
        builder.setView(dialog);
        final AlertDialog dialog1 = builder.create();
        dialog1.show();

    }

    /**
     * REQUIRMENT 10.
     * Event handler for when the user clicks the
     * show places button.
     *
     * @param view
     */
    public void showPlaces(View view) {

        // get the contents of the database.
        Cursor result = db.getData();

        // if the user has not stored any places yet
        // inform them and return.
        if (result == null || result.getCount() == 0) {
            String message = "No places to show";
            Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }

        // Create an intent to pass information between the two activities.
        Intent intent = new Intent(this, DisplayPlacesActivity.class);

        StringBuffer places = new StringBuffer();

        while (result.moveToNext()) {
            places.append(result.getString(1) + " : " + result.getString(2) + "\n");
        }

        places.toString();
        intent.putExtra(PLACES_MESSAGE, (CharSequence) places);
        startActivity(intent);

    }


    /**
     * REQUIREMENT 9 CHANGE THEME FEATURE
     * Changes the background colour based on the backgroundColor.
     *
     * @param backgroundColor
     */
    private void setTheme(String backgroundColor) {

        // get any child view in activity main layout.
        View child = findViewById(R.id.cloudText);

        // the parent will be the Relative layout object
        View parent = child.getRootView();

        // parse the colour
        int color = Color.parseColor(backgroundColor);

        // set the background.
        parent.setBackgroundColor(color);

    }

}
