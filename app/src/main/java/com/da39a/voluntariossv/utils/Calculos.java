package com.da39a.voluntariossv.utils;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class Calculos {

    public static double getDistancia(LatLng puntoA, LatLng puntoB){
        double metros = SphericalUtil.computeDistanceBetween(puntoA,puntoB);
        return Math.floor(metros);
    }

    public static int getEdadByMilisdate(long birthday)
    {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(birthday);
        int birthyear = calendar.get(Calendar.YEAR);
        int currentyear = new GregorianCalendar().get(Calendar.YEAR);
        return currentyear - birthyear;
    }

}
