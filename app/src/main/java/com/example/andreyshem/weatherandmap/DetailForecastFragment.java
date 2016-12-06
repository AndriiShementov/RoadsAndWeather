package com.example.andreyshem.weatherandmap;


import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import static com.example.andreyshem.weatherandmap.ListForecastFragment.humidityTO;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.pressureTO;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.selectionedDate;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.temperature_maxTO;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.temperature_minTO;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.weather_idTO;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.windDirectionTO;
import static com.example.andreyshem.weatherandmap.ListForecastFragment.windSpeedTO;
import static com.example.andreyshem.weatherandmap.MainActivity.cityName;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailForecastFragment extends Fragment{

    private ShareActionProvider mShareActionProvider;
    private String mForecastStr;

    public DetailForecastFragment() {
        // Required empty public constructor
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ImageView mIconView;
        TextView mSelectDate;
        TextView mHighTempView;
        TextView mLowTempView;
        TextView mHumidityView;
        TextView mWindView;
        TextView mPressureView;
        TextView mCity;

        View rootView = inflater.inflate(R.layout.fragment_detail_forecast, container, false);

        // Inflate the layout for this fragment
        mIconView = (ImageView) rootView.findViewById(R.id.imageIcon);
        mHighTempView = (TextView) rootView.findViewById(R.id.temp_max);
        mLowTempView = (TextView) rootView.findViewById(R.id.temp_min);
        mHumidityView = (TextView) rootView.findViewById(R.id.humidity);
        mWindView = (TextView) rootView.findViewById(R.id.wind_speed);
        mSelectDate = (TextView) rootView.findViewById(R.id.mDate);
        mPressureView = (TextView) rootView.findViewById(R.id.pressure);
        mCity = (TextView) rootView.findViewById(R.id.city);

        mCity.setText(cityName);

        int weatherId = Integer.valueOf(weather_idTO);
        // Use weather art image
        mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        mSelectDate.setText(Utility.formatDate(selectionedDate));

        mHighTempView.setText(getString(R.string.maxValueOfTemperature)+ " " + Utility.formatTemperature(getActivity(),temperature_maxTO));
        mLowTempView.setText(getString(R.string.minValueOfTemperature) + " " + Utility.formatTemperature(getActivity(),temperature_minTO));

        mHumidityView.setText(getString(R.string.humidityText)+ humidityTO + " %");

        mPressureView.setText(Utility.getFormatPressure(getActivity(), pressureTO));

        mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedTO, windDirectionTO));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_share, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.minValueOfTemperature) + " "
                + temperature_minTO + "; " + getString(R.string.maxValueOfTemperature)+ " " +
                temperature_maxTO + "; " + getString(R.string.pressureText)+ " " +
                pressureTO + "; " + getString(R.string.humidityText)+ " " +
                humidityTO+ "; " + getString(R.string.windSpeedText)+ " " +
                windSpeedTO+ "; " + getString(R.string.windDirectionText)+ " " + windDirectionTO);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
//        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }
}
