package data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import model.Place;
import model.Weather;
import utilities.Utility;

/**
 * Created by joe on 01/03/2017.
 * DATA PROCESSING
 *
 *
 */

public class JSONWeatherparser {

    public static Weather getWeather(String data){

        Weather weather = new Weather();


        try{

            JSONObject jsonObject = new JSONObject(data);

            Place place = new Place();

            JSONObject coordObject = Utility.getJSONObject("coord", jsonObject);

            place.setLat(Utility.getFloat("lon", coordObject));
            place.setLon(Utility.getFloat("lat", coordObject));

            // sys object
            JSONObject sysOject = Utility.getJSONObject("sys", jsonObject);
            place.setCountry(Utility.getString("country", sysOject));
            place.setLastupdate(Utility.getInt("dt", jsonObject));
            place.setSunrise(Utility.getInt("sunrise", sysOject));
            place.setSunset(Utility.getInt("sunset", sysOject));
            place.setCity(Utility.getString("name", jsonObject));

            weather.place = place;

            // create the weather object
            JSONArray jsonArray = jsonObject.getJSONArray("weather");

            JSONObject weatherObject = jsonArray.getJSONObject(0);
            weather.currentCondition.setWeatherID(Utility.getInt("id", weatherObject));
            weather.currentCondition.setDescription(Utility.getString("description", weatherObject));
            weather.currentCondition.setCondition(Utility.getString("main", weatherObject));
            weather.currentCondition.setIcon(Utility.getString("icon", weatherObject));

            // create a main obejecf
            JSONObject mainObject = Utility.getJSONObject("main", jsonObject);
            weather.currentCondition.setPressure(Utility.getFloat("pressure", mainObject));
            weather.currentCondition.setHumidity(Utility.getFloat("humidity", mainObject));
            weather.temperature.setTemp(Utility.getDouble("temp", mainObject));
            weather.temperature.setMinTemp(Utility.getFloat("temp_min", mainObject));
            weather.temperature.setMaxTemp(Utility.getFloat("temp", mainObject));

            // create a wind object
            JSONObject windOject = jsonObject.getJSONObject("wind");
            weather.wind.setDeg(Utility.getFloat("deg", windOject));
            weather.wind.setSpeed(Utility.getFloat("speed", windOject));

            // create a cloud
            JSONObject cloudOject = jsonObject.getJSONObject("clouds");
            weather.cloud.setPrecipitation(Utility.getInt("all", cloudOject));

            return weather;

        } catch (JSONException e){
            e.printStackTrace();
        }

        return null;

    }
}
