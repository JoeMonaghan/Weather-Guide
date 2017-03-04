package utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by joe on 01/03/2017.
 */

public class Utility {

    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?";

    public static final String ICON_URL = "http://openweathermap.org/img/w/";

    public static JSONObject getJSONObject(String tagname, JSONObject jsonObject) throws JSONException {
        JSONObject jsonObj = jsonObject.getJSONObject(tagname);
        return jsonObj;
    }


    public static String getString(String tagname, JSONObject jsonObject) throws JSONException {
        return jsonObject.getString(tagname);
    }


    public static float getFloat(String tagname, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagname);
    }


    public static double getDouble(String tagname, JSONObject jsonObject) throws JSONException {
        return (float) jsonObject.getDouble(tagname);
    }


    public static int getInt (String tagname, JSONObject jsonObject) throws JSONException {
        return jsonObject.getInt(tagname);
    }

}
