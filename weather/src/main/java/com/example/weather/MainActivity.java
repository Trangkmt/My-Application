package com.example.weather;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private MapView mapView;
    private GoogleMap mGoogleMap;
    private TextView locationTextView, textView, tempTextView, humidTextView, cloudTextView, rainTextView, windTextView, apparentTempTextView, pressureTextView;
    private ProgressBar progressBar;
    private FloatingActionButton fabRefresh;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyLocale(); 
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews(savedInstanceState);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        setupMap();

        fabRefresh.setOnClickListener(v -> {
            if (mGoogleMap != null) {
                LatLng currentCenter = mGoogleMap.getCameraPosition().target;
                Toast.makeText(this, R.string.updating_weather, Toast.LENGTH_SHORT).show();
                showInfo(currentCenter);
            }
        });

        checkLocationPermission();
    }

    private void initViews(Bundle savedInstanceState) {
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        locationTextView = findViewById(R.id.locationTextView);
        textView = findViewById(R.id.textView);
        tempTextView = findViewById(R.id.tempTextView);
        humidTextView = findViewById(R.id.humidTextView);
        cloudTextView = findViewById(R.id.cloudTextView);
        rainTextView = findViewById(R.id.rainTextView);
        windTextView = findViewById(R.id.windTextView);
        apparentTempTextView = findViewById(R.id.apparent_temperature);
        pressureTextView = findViewById(R.id.surface_pressure);
        progressBar = findViewById(R.id.progressBar);
        fabRefresh = findViewById(R.id.floatingActionButton);
    }

    private void setupMap() {
        mapView.getMapAsync(googleMap -> {
            mGoogleMap = googleMap;
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            float lastLat = prefs.getFloat("last_lat", -1000f);
            float lastLng = prefs.getFloat("last_lng", -1000f);

            if (lastLat != -1000f && lastLng != -1000f) {
                LatLng lastLoc = new LatLng(lastLat, lastLng);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLoc, 12));
                showInfo(lastLoc);
            } else {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mGoogleMap.setMyLocationEnabled(true);
                    fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                        if (location != null) {
                            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
                            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 12));
                            showInfo(myLoc);
                        }
                    });
                }
            }

            mGoogleMap.setOnCameraIdleListener(() -> {
                LatLng target = mGoogleMap.getCameraPosition().target;
                showInfo(target);
                prefs.edit()
                        .putFloat("last_lat", (float) target.latitude)
                        .putFloat("last_lng", (float) target.longitude)
                        .apply();
            });
        });
    }

    private void showInfo(LatLng location) {
        if (location == null) return;
        textView.setText(convertToDegreeMinutesSeconds(location.latitude) + ", " + convertToDegreeMinutesSeconds(location.longitude));
        locationTextView.setText(getCityName(location.latitude, location.longitude));
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + location.latitude +
                        "&longitude=" + location.longitude +
                        "&current=temperature_2m,relative_humidity_2m,apparent_temperature,precipitation,rain,showers,snowfall,is_day,cloud_cover,surface_pressure,wind_speed_10m,wind_direction_10m,wind_gusts_10m";
                
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);

                JSONObject json = new JSONObject(response.toString());
                JSONObject current = json.getJSONObject("current");

                runOnUiThread(() -> updateWeatherUI(current));
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            }
        }).start();
    }

    private void updateWeatherUI(JSONObject current) {
        try {
            progressBar.setVisibility(View.GONE);
            
            double temp = current.getDouble("temperature_2m");
            int humid = current.getInt("relative_humidity_2m");
            int cloud = current.getInt("cloud_cover");
            double rain = current.getDouble("rain");
            double wind = current.getDouble("wind_speed_10m");
            double apparent = current.getDouble("apparent_temperature");
            double pressure = current.getDouble("surface_pressure");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String lang = prefs.getString("language", "vi");
            String unit = prefs.getString("unit", "km");

            if ("en".equals(lang)) {
                double tempF = temp * 1.8 + 32;
                double apparentF = apparent * 1.8 + 32;
                tempTextView.setText("🌡️" + (int) tempF + "°F");
                apparentTempTextView.setText(String.format("%.1f", apparentF) + "°F");
            } else {
                tempTextView.setText("🌡️" + (int) temp + "°C");
                apparentTempTextView.setText(apparent + "°C");
            }

            humidTextView.setText("💧" + humid + "%");
            cloudTextView.setText("☁️\n" + cloud + "%");
            rainTextView.setText("🌧️\n" + rain + "mm");

            if ("mile".equals(unit)) {
                double miles = wind * 0.621371;
                windTextView.setText("🌪️\n" + String.format("%.1f", miles) + " mph");
            } else {
                windTextView.setText("🌪️\n" + (int) wind + " km/h");
            }
            pressureTextView.setText(pressure + " hPa");
        } catch (Exception e) { e.printStackTrace(); }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private String getCityName(double lat, double lon) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (addresses != null && !addresses.isEmpty()) {
                String adminArea = addresses.get(0).getAdminArea();
                return adminArea != null ? adminArea : addresses.get(0).getLocality();
            }
        } catch (Exception e) { e.printStackTrace(); }
        return getString(R.string.unknown_location);
    }

    private String convertToDegreeMinutesSeconds(double coordinate) {
        int degree = (int) coordinate;
        coordinate = Math.abs(coordinate - degree) * 60;
        int minute = (int) coordinate;
        int second = (int) ((coordinate - minute) * 60);
        return degree + "°" + minute + "'" + second + "\"";
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void applyLocale() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String lang = prefs.getString("language", "vi");
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Resources res = getResources();
        Configuration config = res.getConfiguration();
        config.setLocale(locale);
        res.updateConfiguration(config, res.getDisplayMetrics());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("language".equals(key)) {
            recreate();
        } else if ("unit".equals(key) && mGoogleMap != null) {
            showInfo(mGoogleMap.getCameraPosition().target);
        }
    }

    @Override protected void onResume() { super.onResume(); mapView.onResume(); }
    @Override protected void onPause() { super.onPause(); mapView.onPause(); }
    @Override protected void onDestroy() { super.onDestroy(); mapView.onDestroy(); }
    
    // Sửa lỗi truy cập: Phải để public
    @Override public void onLowMemory() { super.onLowMemory(); mapView.onLowMemory(); }
}
