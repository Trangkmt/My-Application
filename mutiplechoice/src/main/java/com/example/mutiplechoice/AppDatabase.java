package com.example.mutiplechoice;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Question.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase db;


    public abstract QuestionDao questionDao();

    public static AppDatabase getDatabase(final Context context) {
        if (db == null) {
            db = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "quiz_database").build();
        }
        return db;
    }
}

