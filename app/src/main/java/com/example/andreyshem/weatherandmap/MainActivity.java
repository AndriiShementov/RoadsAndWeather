package com.example.andreyshem.weatherandmap;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.andreyshem.weatherandmap.a_ShowMapFragment.fourthPont;
import static com.example.andreyshem.weatherandmap.a_ShowMapFragment.secondPoint;
import static com.example.andreyshem.weatherandmap.a_ShowMapFragment.firstPoint;
import static com.example.andreyshem.weatherandmap.a_ShowMapFragment.thirdPoint;

public class MainActivity extends AppCompatActivity  {

    private AutoCompleteTextView autoCompView_Start;
    private AutoCompleteTextView autoCompView_Dist;
    private TextView textViewFROM;
    private TextView textViewTO;
    private String origin;
    private String destination;

    private boolean mTwoPane;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    public static double latitude;
    public static double longlatitude;
    public static String cityName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (findViewById(R.id.detail_container) != null) {
            // the device is a tablet
            mTwoPane = true;
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }

        // Set adapters for AutoCompleteTextView
        autoCompView_Start = (AutoCompleteTextView) findViewById(R.id.autoComplTxtVwStartPoint);
        autoCompView_Start.setAdapter(new a_GooglePlacesAutocompleteAdapter(this, R.layout.a_list_item));
        textViewFROM = (TextView) findViewById(R.id.textViewFROM);

        autoCompView_Dist = (AutoCompleteTextView) findViewById(R.id.autoComplTxtVwEndPoint);
        autoCompView_Dist.setAdapter(new a_GooglePlacesAutocompleteAdapter(this, R.layout.a_list_item));
        textViewTO = (TextView) findViewById(R.id.textViewTO);
    }

    protected void fillStringViewLocalWeather(AutoCompleteTextView autoCompView, final TextView textView){
        String str = autoCompView.getText().toString();
        textView.setText(getString(R.string.txtFrom) + " " + str);
    }

    protected void CheckStringView (){
        if (origin.isEmpty()) {
            Toast.makeText(this, R.string.toastEmptyOrig, Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, R.string.toastEmptyDist, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickBtnFindPath(View view){

        fillStringViewLocalWeather(autoCompView_Start,textViewFROM);
        fillStringViewLocalWeather(autoCompView_Dist,textViewTO);

        a_ShowMapFragment showMap = new a_ShowMapFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.detail_container, showMap);
        ft.commit();

        origin = autoCompView_Start.getText().toString();
        destination = autoCompView_Dist.getText().toString();
        CheckStringView ();
        showMap.sendRequest(origin,destination);
    }

    public void onClickTxtViewGetLocalWeather (View view){

        switch (view.getId()){
            case R.id.textViewFROM:
                if (textViewFROM.getText().toString().equals(getString(R.string.txtFrom))) {
                    Toast.makeText(this, R.string.toastEmptytextViewFROM, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (firstPoint == null || secondPoint == null){
                    a_ShowMapFragment showMap = new a_ShowMapFragment();
                    origin = autoCompView_Start.getText().toString();
                    destination = autoCompView_Dist.getText().toString();
                    CheckStringView ();
                    showMap.sendRequest(origin,destination);
                }
                latitude = firstPoint;
                longlatitude = secondPoint;
                if(mTwoPane){
                    TextView showCity = (TextView) findViewById(R.id.showCityName);
                    //showCity2.setVisibility();
                    showCity.setText(getString(R.string.showNameOfCity) + " " + origin);
                    cityName = autoCompView_Start.getText().toString();
                }else {
                    cityName = autoCompView_Start.getText().toString();
                }

                break;
            case R.id.textViewTO:
                if (textViewTO.getText().toString().equals(getString(R.string.txtFrom))) {
                    Toast.makeText(this, R.string.toastEmptytextViewTO, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (thirdPoint == null || fourthPont == null){
                    a_ShowMapFragment showMap = new a_ShowMapFragment();
                    origin = autoCompView_Start.getText().toString();
                    destination = autoCompView_Dist.getText().toString();
                    CheckStringView ();
                    showMap.sendRequest(origin,destination);
                }
                latitude = thirdPoint;
                longlatitude = fourthPont;
                if(mTwoPane){
                    TextView showCity2 = (TextView) findViewById(R.id.showCityName);
                    showCity2.setText(getString(R.string.showNameOfCity) + " " + destination);
                    cityName = autoCompView_Dist.getText().toString();
                }else{
                    cityName = autoCompView_Dist.getText().toString();
                }

                break;
        }
        startForecastWeatherActivity();
    }

    public void startForecastWeatherActivity(){
        if (mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.forecast_list_container, new ListForecastFragment())
                    .commit();
        }
        else {
            Intent intent = new Intent(this, ListForecastActivity.class);
            startActivity(intent);
        }
    }

}


