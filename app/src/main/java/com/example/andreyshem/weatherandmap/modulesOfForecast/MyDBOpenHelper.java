package com.example.andreyshem.weatherandmap.modulesOfForecast;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBOpenHelper extends SQLiteOpenHelper {
    private static  final int DB_VERSION = 1;

    public MyDBOpenHelper(Context context) {
        super(context, StringsOfDB.WeatherEntry.TABLE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + StringsOfDB.WeatherEntry.TABLE_NAME + " (" +
                StringsOfDB.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                // information about location
                StringsOfDB.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT NOT NULL, " +
                StringsOfDB.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, " +
                StringsOfDB.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, " +
                StringsOfDB.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, " +
                // information about weather
                StringsOfDB.WeatherEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL," +
                StringsOfDB.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_HUMIDITY + " INTEGER NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, " +
                StringsOfDB.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL " +
                ");";

        db.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StringsOfDB.LocationEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + StringsOfDB.WeatherEntry.TABLE_NAME);
        onCreate(db);
    }
}
