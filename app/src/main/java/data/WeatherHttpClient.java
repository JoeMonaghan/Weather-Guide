package data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * REQUIREMENT 4.
 * This class contains the required functionality to connect with
 * the open weather map API.
 */

public class WeatherHttpClient {

    /**
     * Connect with the API by setting up a http connection.
     *
     * @param lat
     * @param log
     * @return a string representation of the returned message from API.
     */
    public String getWeatherData(String lat, String log) {

        URL url = null;

        HttpURLConnection connection = null;

        InputStream inputStream = null;


        try {

            url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat +
                    "&lon=" + log + "&units=metric&appid=f1c919321e6e4559ac228b900faafb60");
            connection = (HttpURLConnection) url.openConnection();

            // use  a get request.
            connection.setRequestMethod("GET");
            // done twice in video.
            connection.setDoInput(true);

            // try to connect
            connection.connect();

            StringBuffer stringBuffer = new StringBuffer();

            // get the return data.
            inputStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;

            // close the connection
            connection.disconnect();

            // keep adding to string buffer until the end is reached.
            while ((line = bufferedReader.readLine()) != null) {
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
