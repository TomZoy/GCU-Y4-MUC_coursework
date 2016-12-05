package com.gcu.zoltantompa.geocoral;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.*;
import android.provider.Settings;
import android.widget.Toast;

/**
 * Created by TomZoy on 2016-12-05.
 */

public class pcQueryUrlBuilder {

    Toast toast;

    private String finalURL;
    private static String baseURL = "http://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson";


    boolean isSDatePeriodEnabled = false;
    String periodFrom = null;
    String periodTill = null;

    boolean isSRadiusEnabled = false;
    String radiusKm = "100";

    String minMag = "1";
    String maxMag = "10";
    String minSig = "1";
    String maxSig = "1000";
    String resultLimit = "100";

    boolean isDayNightModeOn = false;

    pcSaveSettings saveSettings;
    SharedPreferences mySavedSettings;

    //constructor
    public pcQueryUrlBuilder(Context context) {
        loadSavedSettings(context);
        buildURL();
        toast = Toast.makeText(context, finalURL, Toast.LENGTH_LONG);
        toast.show();
    }

    public boolean isDayNightModeOn()
    {
        return isDayNightModeOn;
    }

    public String getFinalURL()
    {
        return finalURL;
    }

    private void buildURL(){

        finalURL = baseURL;

        if (isSDatePeriodEnabled){
            finalURL += "&starttime=" + periodFrom;
            finalURL += "&endtime=" + periodTill; //format: 2014-01-02
        }

        finalURL += "&limit=" + resultLimit;
        finalURL += "&minmagnitude=" + minMag;
        finalURL += "&maxmagnitude=" + maxMag;
        finalURL += "&minsig=" + minSig;
        finalURL += "&maxsig=" + maxSig;
        /*
        &latitude=
        &longitude=
        &maxradiuskm=
        */
        finalURL += "&orderby=time";

    }


    private void loadSavedSettings(Context context) {
        //getting back saved data
        mySavedSettings=PreferenceManager.getDefaultSharedPreferences(context);
        saveSettings=new pcSaveSettings(mySavedSettings);

        isDayNightModeOn = mySavedSettings.getBoolean("SisSDayNightEnabled",false);


        isSDatePeriodEnabled = mySavedSettings.getBoolean("SisSDatePeriodEnabled",false);
        periodFrom = (mySavedSettings.getString("SPeriodFrom",null));
        periodTill = (mySavedSettings.getString("SPeriodTill",null));


        isSRadiusEnabled = (mySavedSettings.getBoolean("SisSRadiusEnabled",false));
        radiusKm = Integer.toString(mySavedSettings.getInt("SRadiusKm", 100));

        minMag = Integer.toString(mySavedSettings.getInt("SminMag",1));
        maxMag = Integer.toString(mySavedSettings.getInt("SmaxMag",10));
        minSig = Integer.toString(mySavedSettings.getInt("SminSig",1));
        maxSig = Integer.toString(mySavedSettings.getInt("SmaxSig",1000));
        resultLimit = Integer.toString(mySavedSettings.getInt("SmaxResults",100));

        toast = Toast.makeText(context, "periodfrom; " + periodFrom, Toast.LENGTH_SHORT);
        toast.show();
    }
}


