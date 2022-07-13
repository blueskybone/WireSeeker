package com.example.wireseeker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Wire.class},version = 1, exportSchema = false)
public abstract class WireDatabase extends RoomDatabase {
    public abstract WireDao WireDao();

    private static volatile WireDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static WireDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WireDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WireDatabase.class, "wire_database")
                            .createFromAsset("wireseeker.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}