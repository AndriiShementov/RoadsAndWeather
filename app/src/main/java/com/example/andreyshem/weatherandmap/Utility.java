/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.andreyshem.weatherandmap;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.SimpleDateFormat;

public class Utility {


    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_units_key),
                context.getString(R.string.pref_units_metric))
                .equals(context.getString(R.string.pref_units_metric));
    }

    public static String formatTemperature(Context context, String temperature) {
        int tempFormat;
        Double temperature1 = Double.valueOf(temperature);

        if (!isMetric(context)) {
            temperature1 = (temperature1 * 1.8) + 32;
            tempFormat = R.string.format_temperatureF;
        }else{
            tempFormat = R.string.format_temperature;
        }
        return String.format(context.getString(tempFormat), temperature1);
    }

    public static String getFormatPressure(Context context, String pressure) {
        int pressureFormat;
        Double pressure1 = Double.valueOf(pressure);

        if (!isMetric(context)) {
            pressureFormat = R.string.format_pressureI;
            pressure1 = pressure1 * 0.750062;
        }else{
            pressureFormat = R.string.format_pressureM;
        }
        return String.format(context.getString(pressureFormat), pressure1);
    }

    static String formatDate(long time) {
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("dd MMMM EEEE");
        return shortenedDateFormat.format(time);
    }

    public static String getFormattedWind(Context context, String windSpeed, String degrees1) {
        int windFormat;
        String direction = "Unknown";

        float windSpeed1 = Float.valueOf(windSpeed);
        float degrees = Float.valueOf(degrees1);

        if (Utility.isMetric(context)) {
            windFormat = R.string.format_wind_kmh;
        } else {
            windFormat = R.string.format_wind_mph;
            windSpeed1 = .621371192237334f * windSpeed1;
        }

        if (degrees >= 337.5 || degrees < 22.5) {
            direction = context.getString(R.string.directN);
        } else if (degrees >= 22.5 && degrees < 67.5) {
            direction = context.getString(R.string.directNE);
        } else if (degrees >= 67.5 && degrees < 112.5) {
            direction = context.getString(R.string.directE);
        } else if (degrees >= 112.5 && degrees < 157.5) {
            direction = context.getString(R.string.directSE);
        } else if (degrees >= 157.5 && degrees < 202.5) {
            direction = context.getString(R.string.directS);
        } else if (degrees >= 202.5 && degrees < 247.5) {
            direction = context.getString(R.string.directSW);
        } else if (degrees >= 247.5 && degrees < 292.5) {
            direction = context.getString(R.string.directW);
        } else if (degrees >= 292.5 && degrees < 337.5) {
            direction = context.getString(R.string.directNW);
        }
        return String.format(context.getString(windFormat), windSpeed1, direction);
    }

    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }

    public static int getArtResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.art_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.art_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.art_rain;
        } else if (weatherId == 511) {
            return R.drawable.art_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.art_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.art_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.art_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.art_storm;
        } else if (weatherId == 800) {
            return R.drawable.art_clear;
        } else if (weatherId == 801) {
            return R.drawable.art_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.art_clouds;
        }
        return -1;
    }
}