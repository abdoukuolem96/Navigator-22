package com.esi.navigator_22;

import org.osmdroid.util.GeoPoint;

public class StationDetails extends Station {
    double distanceTo;
    double timeTo;


    public StationDetails(String nomFr, String nomAr, double distanceTo,double timeTo) {
        this.nomFr = nomFr;
        this.nomAr = nomAr;
        this.distanceTo = distanceTo;
        this.timeTo=timeTo;
    }

    public StationDetails() {
    }


    @Override
    public String toString() {
        return "StationDetails{" +
                ", nomFr='" + nomFr + '\'' +
                ", nomAr='" + nomAr + '\'' +
                "distanceTo='" + distanceTo + '\'' +
                "timeTo='" + timeTo + '\'' +

                '}';
    }
}