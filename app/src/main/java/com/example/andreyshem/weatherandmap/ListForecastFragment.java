package com.example.andreyshem.weatherandmap;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentTransaction;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.andreyshem.weatherandmap.modulesOfForecast.MyDBOpenHelper;
import com.example.andreyshem.weatherandmap.modulesOfForecast.StringsOfDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static com.example.andreyshem.weatherandmap.MainActivity.latitude;
import static com.example.andreyshem.weatherandmap.MainActivity.longlatitude;

/**
 * A simple {@link Fragment} subclass.
 */

public class ListForecastFragment extends Fragment {

    public static String weather_idTO;
    public static String temperature_minTO;
    public static String temperature_maxTO;
    public static String humidityTO;
    public static String pressureTO;
    public static String windSpeedTO;
    public static String windDirectionTO;
    public static long selectionedDate;

    MyDBOpenHelper getDB;
    SQLiteDatabase mDB;

    public final String LOG_TAG = ListForecastFragment.class.getSimpleName();
    ArrayAdapter<String> mForecastListAdapter;

    public ListForecastFragment(){ }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
        getDB = new MyDBOpenHelper(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mForecastListAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.example_list_item,
                R.id.exampleTextView,
                new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main_list_forecast, container, false);

        ListView mListView = (ListView) rootView.findViewById(R.id.listView_forecast);
        mListView.setAdapter(mForecastListAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                long dateTime;
                Time dayTime = new Time();
                dayTime.setToNow();
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                dateTime = dayTime.setJulianDay(julianStartDay + position);
                mDB = getDB.getReadableDatabase();
                Cursor cursor = mDB.query(StringsOfDB.WeatherEntry.TABLE_NAME,
                        // name of column
                        new String[] {StringsOfDB.WeatherEntry.COLUMN_WEATHER_ID,
                                StringsOfDB.WeatherEntry.COLUMN_MIN_TEMP,
                                StringsOfDB.WeatherEntry.COLUMN_MAX_TEMP,
                                StringsOfDB.WeatherEntry.COLUMN_HUMIDITY,
                                StringsOfDB.WeatherEntry.COLUMN_PRESSURE,
                                StringsOfDB.WeatherEntry.COLUMN_WIND_SPEED,
                                StringsOfDB.WeatherEntry.COLUMN_DEGREES},
                        // condition of selection
                        StringsOfDB.WeatherEntry.COLUMN_DATE + " = ?",
                        new String[]{Long.toString(dateTime)},
                        null,null,null);

                if (cursor.moveToFirst()){
                    selectionedDate = dateTime;
                    weather_idTO = cursor.getString(0);
                    temperature_minTO = cursor.getString(1);
                    temperature_maxTO = cursor.getString(2);
                    humidityTO = cursor.getString(3);
                    pressureTO = cursor.getString(4);
                    windSpeedTO = cursor.getString(5);
                    windDirectionTO = cursor.getString(6);
                }
                cursor.close();
                mDB.close();

                DetailForecastFragment detailView = new DetailForecastFragment();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.replace(R.id.detail_container, detailView);
                ft.commit();

//                Intent intent = new Intent(getActivity(), DetaiForecastlActivity.class);
//                startActivity(intent);
            }
        });
        return rootView;
    }

    public void updateWeather(){
        MyThread weather = new MyThread();
        weather.execute(Double.toString(latitude),Double.toString(longlatitude));
    }

    @Override
    public void onStart() {
        super.onStart();
        mDB = getDB.getWritableDatabase();
        updateWeather();
    }

    public class MyThread extends AsyncTask<String, Void, String[]> {

        public final String LOG_TAG_ = MyThread.class.getSimpleName();

        public String formatHighLows(double high, double low, String unitType) {

            if (unitType.equals(getString(R.string.pref_units_imperial))) {
                high = (high * 1.8) + 32;
                low = (low * 1.8) + 32;
            } else if (!unitType.equals(getString(R.string.pref_units_metric))) {
                Log.d(LOG_TAG, "Unit type not found: " + unitType);
            }

            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);
            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        // convert milliseconds to date format
        private String getReadableDateString(long time) {
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        // reading data from forecastJsonStr and convert to readable format
        private String[] getWeatherDataFromJson(String forecastJsonStr)
                throws JSONException {
            String[] resultStrs = new String[7];

            final String OWM_CITY = "city";
            final String OWN_CITY_ID = "id";
            final String OWM_CITY_NAME = "name";
            final String OWM_COORD = "coord";
            // Location coordinate
            final String OWM_LATITUDE = "lat";
            final String OWM_LONGITUDE = "lon";
            // Weather information.  Each day's forecast info is an element of the "list" array.
            final String OWM_LIST = "list";

            final String OWM_PRESSURE = "pressure";
            final String OWM_HUMIDITY = "humidity";
            final String OWM_WINDSPEED = "speed";
            final String OWM_WIND_DIRECTION = "deg";
            // All temperatures are children of the "temp" object.
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";

            final String OWM_WEATHER = "weather";
            final String OWM_DESCRIPTION = "main";
            final String OWM_WEATHER_ID = "id";

            try {
                JSONObject forecastJson = new JSONObject(forecastJsonStr);
                JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

                JSONObject cityJson = forecastJson.getJSONObject(OWM_CITY);
                String cityName = cityJson.getString(OWM_CITY_NAME);
                String locationQuery = cityJson.getString(OWN_CITY_ID);

                JSONObject cityCoord = cityJson.getJSONObject(OWM_COORD);
                double cityLongitude = cityCoord.getDouble(OWM_LONGITUDE);
                double cityLatitude = cityCoord.getDouble(OWM_LATITUDE);

                Time dayTime = new Time();
                dayTime.setToNow();
                // we start at the day returned by local time. Otherwise this is a mess.
                int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

                // now we work exclusively in UTC
                dayTime = new Time();

                for (int i = 0; i < weatherArray.length(); i++) {

                    long dateTime;
                    double pressure;
                    int humidity;
                    double windSpeed;
                    double windDirection;
                    double high;
                    double low;
                    String description;
                    int weatherId;

                    // JSON object
                    JSONObject dayForecast = weatherArray.getJSONObject(i);
                    // UTC time
                    dateTime = dayTime.setJulianDay(julianStartDay + i);
                    //parameters
                    pressure = dayForecast.getDouble(OWM_PRESSURE);
                    humidity = dayForecast.getInt(OWM_HUMIDITY);
                    windSpeed = dayForecast.getDouble(OWM_WINDSPEED);
                    windDirection = dayForecast.getDouble(OWM_WIND_DIRECTION);
                    // short description
                    JSONObject weatherObject =
                            dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                    description = weatherObject.getString(OWM_DESCRIPTION);
                    weatherId = weatherObject.getInt(OWM_WEATHER_ID);
                    // Temperatures
                    JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                    high = temperatureObject.getDouble(OWM_MAX);
                    low = temperatureObject.getDouble(OWM_MIN);

                    ContentValues weatherValue = new ContentValues();
                    // information about location
                    weatherValue.put(StringsOfDB.LocationEntry.COLUMN_LOCATION_SETTING, locationQuery);
                    weatherValue.put(StringsOfDB.LocationEntry.COLUMN_CITY_NAME, cityName);
                    weatherValue.put(StringsOfDB.LocationEntry.COLUMN_COORD_LAT, cityLatitude);
                    weatherValue.put(StringsOfDB.LocationEntry.COLUMN_COORD_LONG, cityLongitude);
                    // information about weather
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_DATE, dateTime);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_SHORT_DESC, description);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_WEATHER_ID, weatherId);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_MAX_TEMP, high);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_MIN_TEMP, low);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_HUMIDITY, humidity);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_PRESSURE, pressure);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_WIND_SPEED, windSpeed);
                    weatherValue.put(StringsOfDB.WeatherEntry.COLUMN_DEGREES, windDirection);

                    mDB.insert(StringsOfDB.WeatherEntry.TABLE_NAME , null, weatherValue);

                String unitType = getString(R.string.pref_units_metric);
                String highAndLow = formatHighLows(high, low, unitType);
                    resultStrs[i] = getReadableDateString(dateTime) + " - " + description + " " +  getString(R.string.valueOfTemperature)  + highAndLow;
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG_, e.getMessage(), e);
                e.printStackTrace();
            }
            return resultStrs;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String forecastJsonStr = null;
            if (params.length == 0) {
                return null;
            }

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {

                // URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");
                //34.98333, 48.45
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?lat=34.9833&lon=48.45&mode=json&units=metric&cnt=7";
                // String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

                final String FORECAST_BASE_URL =
                        "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String LAT_PARAM = "lat";
                final String LON_PARAM = "lon";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(LAT_PARAM, params[0])
                        .appendQueryParameter(LON_PARAM, params[1])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(APPID_PARAM, BuildConfig.OPEN_WEATHER_MAP_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.v(LOG_TAG_, "Built URI " + builtUri.toString());

                // Create the request to OpenWeatherMap
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG_, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;

        }

        //return received data to adapter and update the list view
        @Override
        protected void onPostExecute(String[] entrance) {
            if (entrance != null) {
                mForecastListAdapter.clear();
                for(String dayForecastStr : entrance) {
                    mForecastListAdapter.add(dayForecastStr);
                }
            }
            mDB.close();
        }
    }

}



