package com.example.cooking;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiThread extends Thread {

    String mealName;
    MainActivity activity;

    public ApiThread(String mealName, MainActivity activity) {
        this.mealName = mealName;
        this.activity = activity;
    }

    @Override
    public void run() {
        try {

            String apiUrl =
                    "https://www.themealdb.com/api/json/v1/1/search.php?s=" + mealName;

            URL url = new URL(apiUrl);

            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            StringBuilder response = new StringBuilder();
            String line;

            while((line = reader.readLine()) != null){
                response.append(line);
            }

            reader.close();

            JSONObject jsonObject = new JSONObject(response.toString());

            JSONArray meals = jsonObject.getJSONArray("meals");

            JSONObject meal = meals.getJSONObject(0);

            String name = meal.getString("strMeal");
            String category = meal.getString("strCategory");
            String area = meal.getString("strArea");
            String instruction = meal.getString("strInstructions");

            String result =
                    "Tên món: " + name +
                            "\nLoại: " + category +
                            "\nQuốc gia: " + area +
                            "\n\nHướng dẫn:\n" + instruction;

            activity.runOnUiThread(() -> {
                activity.textViewResult.setText(result);
            });

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

