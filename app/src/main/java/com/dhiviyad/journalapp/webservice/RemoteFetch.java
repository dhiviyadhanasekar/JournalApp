package com.dhiviyad.journalapp.webservice;

/**
 * Created by dhiviyad on 12/5/16.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.dhiviyad.journalapp.R;

//Ref: https://code.tutsplus.com/tutorials/create-a-weather-app-on-android--cms-21587
public class RemoteFetch {

//    private static final String LATLONG = "&lat=37.3382&lon=-121.8863";
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?units=imperial"; //+ LATLONG;


    public static JSONObject getJSON(Context context, double lat, double lon){
        try {
            URL url = new URL(OPEN_WEATHER_MAP_API + "&lat=" + lat + "&lon=" + lon);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key", context.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

//            String text = String.format("%.2f", main.getDouble("temp")+ "\\u00B0" + "F");

            // This value will be 404 if the request was not
            // successful
            if(data.getInt("cod") != 200){
                return null;
            }

            JSONObject main = data.getJSONObject("main");
//            System.out.println("JSON data ===" +main.getDouble("temp")+ "\u00B0" + "F");
            return main;
        }catch(Exception e){
            return null;
        }
    }
}
