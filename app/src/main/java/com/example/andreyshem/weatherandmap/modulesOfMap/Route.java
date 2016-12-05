package com.example.andreyshem.weatherandmap.modulesOfMap;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by andreyshem on 10.11.2016.
 */
public class Route {
    public com.example.andreyshem.weatherandmap.modulesOfMap.Distance distance;
    public com.example.andreyshem.weatherandmap.modulesOfMap.Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public double latStartAdress;
    public double lonStartAdress;
    public double latEndAdress;
    public double lonEndAdress;


    public List<LatLng> points;
}
