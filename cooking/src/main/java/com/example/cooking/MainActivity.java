package com.example.cooking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {

    EditText editTextSearch;

    Button buttonSearch;

    TextView textViewResult;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        editTextSearch = findViewById(R.id.editTextSearch);

        buttonSearch = findViewById(R.id.buttonSearch);

        textViewResult = findViewById(R.id.textViewResult);


        buttonSearch.setOnClickListener(v -> {


            String mealName = editTextSearch.getText().toString();


            ApiThread thread = new ApiThread(mealName, this);

            thread.start();


        });
    }
}

