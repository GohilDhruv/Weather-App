package com.example.weatherapp;
import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

public class WeatherApiClient {

    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?appid=039f6017d1f7dded8111f9b584583f5b&q=";

    public interface Callback {
        void onSuccess(String weatherData);

        void onFailure(String errorMessage);
    }

    @SuppressLint("StaticFieldLeak")
    public void getWeatherData(String city, Callback callback) {
        String url = API_BASE_URL + city;

        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                try {
                    return fetchDataFromApi(params[0]);
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result != null) {
                    try {
                        JSONObject json = new JSONObject(result);
                        double temperature = json.getJSONObject("main").getDouble("temp");
                        temperature = temperature - 273.15;
                        DecimalFormat decimalFormat = new DecimalFormat("#.##");
                        String formattedTemperature = decimalFormat.format(temperature);
                        int humidity = json.getJSONObject("main").getInt("humidity");
                        double windSpeed = json.getJSONObject("wind").getDouble("speed");
                        String weatherInfo = "Temperature: " + formattedTemperature + "°C\nHumidity: " + humidity + "%\nWind Speed: " + windSpeed + " km/h";
                        callback.onSuccess(weatherInfo);
                    } catch (JSONException e) {
                        callback.onFailure("Failed to parse weather data");
                    }
                } else {
                    callback.onFailure("Failed to fetch weather data");
                }
            }
        }.execute(url);
    }

    private String fetchDataFromApi(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try {
            InputStream inputStream = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            return stringBuilder.toString();
        } finally {
            connection.disconnect();
        }
    }
}
