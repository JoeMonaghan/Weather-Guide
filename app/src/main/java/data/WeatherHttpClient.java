package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import utilities.Utility;

/**
 * Created by joe on 01/03/2017.
 */

public class WeatherHttpClient {

    public String getWeatherData(String lat, String log){

        URL url = null;

        HttpURLConnection connection = null;

        InputStream inputStream = null;


        try {

            url = new URL(Utility.BASE_URL + "lat="+lat+"&lon="+lat + "&units=metric&appid=f1c919321e6e4559ac228b900faafb60");



            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            // done twice in video.
            connection.setDoInput(true);
            connection.connect();

            StringBuffer stringBuffer = new StringBuffer();
            inputStream = connection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line + "\r\n");
            }

            inputStream.close();

            return stringBuffer.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
