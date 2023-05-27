package com.example.weatherapp;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Button searchButton;
    private TextView weatherTextView;
    private ProgressBar progressBar;

    private WeatherApiClient weatherApiClient;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        cityEditText = findViewById(R.id.cityEditText);
        searchButton = findViewById(R.id.searchButton);
        weatherTextView = findViewById(R.id.weatherTextView);
        progressBar = findViewById(R.id.progressBar);

        // Initialize WeatherApiClient
        weatherApiClient = new WeatherApiClient();

        // Initialize SharedPreferences
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve last searched city and display weather information
        String lastSearchedCity = sharedPreferences.getString("last_city", "");
        if (!TextUtils.isEmpty(lastSearchedCity)) {
            cityEditText.setText(lastSearchedCity);
            getWeatherData(lastSearchedCity);
        }

        // Button click listener
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEditText.getText().toString();
                if (!TextUtils.isEmpty(city)) {
                    getWeatherData(city);
                }
            }
        });
    }

    private void getWeatherData(String city) {
        // Show loading indicator
        progressBar.setVisibility(View.VISIBLE);

        // Make API request
        weatherApiClient.getWeatherData(city, new WeatherApiClient.Callback() {
            @Override
            public void onSuccess(String weatherData) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);

                // Display weather information
                weatherTextView.setText(weatherData);

                // Save last searched city
                sharedPreferences.edit().putString("last_city", city).apply();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Hide loading indicator
                progressBar.setVisibility(View.GONE);

                // Display error message
                weatherTextView.setText(errorMessage);
            }
        });
    }
}
