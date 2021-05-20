package com.da39a.voluntariossv.utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;


public class Calculos {

    public static double getDistancia(LatLng puntoA, LatLng puntoB){
        double metros = SphericalUtil.computeDistanceBetween(puntoA,puntoB);
        return Math.floor(metros);
    }

}
