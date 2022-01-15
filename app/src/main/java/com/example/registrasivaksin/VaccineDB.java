package com.example.registrasivaksin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.registrasivaksin.ui.inputdata.ModelInput;

@Database(entities = {ModelInput.class}, version = 1)
public abstract class VaccineDB extends RoomDatabase {

    private static VaccineDB instance;

    public abstract InputDao inputDao();

    public static synchronized VaccineDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    VaccineDB.class, "vaccine_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
        }
    };

}