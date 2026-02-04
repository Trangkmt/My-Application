package com.example.note;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    CalendarView calendarView;
    EditText noteEditText;
    Button saveButton;
    String fileName = "";

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendarView = findViewById(R.id.calendarView);
        noteEditText = findViewById(R.id.noteEditText);
        saveButton = findViewById(R.id.saveButton);

        pref = getPreferences(MODE_PRIVATE);

        // Restore ngày đã lưu
        int savedYear = pref.getInt("year", -1);
        if (savedYear != -1) {
            Calendar cal = Calendar.getInstance();
            cal.set(savedYear,
                    pref.getInt("month", 0),
                    pref.getInt("dayOfMonth", 1));
            calendarView.setDate(cal.getTimeInMillis());
        }

        // Khi chọn ngày
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            fileName = String.format("%02d_%02d_%04d", dayOfMonth, month + 1, year);

            // Lưu ngày vào SharedPreferences
            pref.edit()
                    .putInt("year", year)
                    .putInt("month", month)
                    .putInt("dayOfMonth", dayOfMonth)
                    .apply();

            // Load note
            noteEditText.setText("");
            try {
                FileInputStream fis = openFileInput(fileName);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                fis.close();
                noteEditText.setText(sb.toString());
            } catch (Exception ignored) {}
        });

        // Save note
        saveButton.setOnClickListener(v -> {
            if (fileName.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                FileOutputStream fos = openFileOutput(fileName, MODE_PRIVATE);
                fos.write(noteEditText.getText().toString().getBytes());
                fos.close();
                Toast.makeText(this, "Đã lưu ghi chú", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Lỗi khi lưu", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
