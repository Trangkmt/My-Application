package com.example.unitconverter;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        EditText editTextCelsius = findViewById(R.id.editTextCelsius);
        Button buttonConvert = findViewById(R.id.buttonConvert);

        buttonConvert.setOnClickListener(v-> {
            double celsius = Double.parseDouble(editTextCelsius.getText().toString());
            double farenheit = celsius * 1.8 + 32;

            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            intent.putExtra("farenheit", farenheit);
            startActivity(intent);
        });
    }
}

