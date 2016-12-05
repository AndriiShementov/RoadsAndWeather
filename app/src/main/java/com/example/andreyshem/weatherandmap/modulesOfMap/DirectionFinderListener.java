package com.example.andreyshem.weatherandmap.modulesOfMap;

import java.util.List;

/**
 * Created by andreyshem on 10.11.2016.
 */
public interface DirectionFinderListener{
        void onDirectionFinderStart();
        void onDirectionFinderSuccess(List<Route> route);
}
