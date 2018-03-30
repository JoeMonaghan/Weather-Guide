package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import model.Weather;
import utilities.Utility;

/**
 * REQUIREMENT 6
 * This class contains the required functionality to
 * convert the string obtained in the weather HTTP client into
 * JSON objects and then a set the corresponding weather objects.
 *
 */


public class JSONWeatherparser {

    /**
     * REQUIREMENT 6
     * Converts the string into a weather instance based on the
     * obtained information from the url connection to API.
     * @param data stream from open weather map API.
     * @return weather instance with current weather information.
     */
    public static Weather getWeather(String data){

        Weather weather = new Weather();

        try{

            // root json object.
            JSONObject jsonObject = new JSONObject(data);

            // get and set and places information.
            JSONObject coordObject = Utility.getJSONObject("coord", jsonObject);
            weather.place.setLat(Utility.getFloat("lon", coordObject));
            weather.place.setLon(Utility.getFloat("lat", coordObject));
            JSONObject sysOject = Utility.getJSONObject("sys", jsonObject);
            weather.place.setCountry(Utility.getString("country", sysOject));
            weather.place.setLastupdate(Utility.getInt("dt", jsonObject));
            weather.place.setSunrise(Utility.getInt("sunrise", sysOject));
            weather.place.setSunset(Utility.getInt("sunset", sysOject));
            weather.place.setCity(Utility.getString("name", jsonObject));

            // get and set the current condition and temperature information.
            JSONArray jsonArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherID(Utility.getInt("id", weatherObject));
            weather.currentCondition.setDescription(Utility.getString("description", weatherObject));
            weather.currentCondition.setCondition(Utility.getString("main", weatherObject));
            weather.currentCondition.setIcon(Utility.getString("icon", weatherObject));
            JSONObject mainObject = Utility.getJSONObject("main", jsonObject);
            weather.currentCondition.setPressure(Utility.getFloat("pressure", mainObject));
            weather.currentCondition.setHumidity(Utility.getFloat("humidity", mainObject));
            weather.temperature.setTemp(Utility.getDouble("temp", mainObject));
            weather.temperature.setMinTemp(Utility.getFloat("temp_min", mainObject));
            weather.temperature.setMaxTemp(Utility.getFloat("temp", mainObject));

            // get and set wind information.
            JSONObject windOject = jsonObject.getJSONObject("wind");
            weather.wind.setDeg(Utility.getFloat("deg", windOject));
            weather.wind.setSpeed(Utility.getFloat("speed", windOject));

            // get and set the clouds information.
            JSONObject cloudOject = jsonObject.getJSONObject("clouds");
            weather.cloud.setPrecipitation(Utility.getInt("all", cloudOject));

            // weather now contains all the necessary information.
            return weather;

        } catch (JSONException e){
            e.printStackTrace();
        }

        return null;

    }
}
