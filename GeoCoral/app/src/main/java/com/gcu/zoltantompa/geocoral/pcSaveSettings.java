package com.gcu.zoltantompa.geocoral;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * code taken and modified from Lab 8
 * This class stores values for the user-settings
 */

public class pcSaveSettings extends Activity {


    // *********************************************
    // Declare variables etc.
    // *********************************************

    SharedPreferences pcSharedSettings;

    private boolean isSDayNightEnabled;
    private boolean isSDatePeriodEnabled;
    private Date SPeriodFrom;
    private Date SPeriodTill;
    private boolean isSRadiusEnabled;
    private int SRadiusKm;

    private int minMagnitude;
    private int maxMagnitude;
    private int minSignificance;
    private int maxSignificance;
    private int limitResultsTo;


    //region Declare getters and setters etc.

    public int getMinSignificance() {
        return minSignificance;
    }

    public void setMinSignificance(int minSignificance) {
        this.minSignificance = minSignificance;
    }

    public int getLimitResultsTo() {
        return limitResultsTo;
    }

    public void setLimitResultsTo(int limitResultsTo) {
        this.limitResultsTo = limitResultsTo;
    }

    public int getMaxMagnitude() {
        return maxMagnitude;
    }

    public void setMaxMagnitude(int maxMagnitude) {
        this.maxMagnitude = maxMagnitude;
    }

    public int getMaxSignificance() {
        return maxSignificance;
    }

    public void setMaxSignificance(int maxSignificance) {
        this.maxSignificance = maxSignificance;
    }

    public int getMinMagnitude() {
        return minMagnitude;
    }

    public void setMinMagnitude(int minMagnitude) {
        this.minMagnitude = minMagnitude;
    }

    public boolean getSDayNightEnabled() {
        return isSDayNightEnabled;
    }

    public void setisSDayNightEnabled(boolean SDayNightEnabled) {
        this.isSDayNightEnabled = SDayNightEnabled;
    }

    public boolean getisSDatePeriodEnabled() {
        return isSDatePeriodEnabled;
    }

    public void setSDatePeriodEnabled(boolean SDatePeriodEnabled) {
        this.isSDatePeriodEnabled = SDatePeriodEnabled;
    }

    public Date getSPeriodFrom() {
        return SPeriodFrom;
    }

    public void setSPeriodFrom(Date SPeriodFrom) {
        this.SPeriodFrom = SPeriodFrom;
    }

    public Date getSPeriodTill() {
        return SPeriodTill;
    }

    public void setSPeriodTill(Date SPeriodTill) {
        this.SPeriodTill = SPeriodTill;
    }

    public boolean getisSRadiusEnabled() {
        return isSRadiusEnabled;
    }

    public void setSRadiusEnabled(boolean SRadiusEnabled) {
        this.isSRadiusEnabled = SRadiusEnabled;
    }

    public int getSRadiusKm() {
        return SRadiusKm;
    }

    public void setSRadiusKm(int SRadiusKm) {
        this.SRadiusKm = SRadiusKm;
    }
    //endregion

// **************************************************
// Declare constructor
// **************************************************

    public pcSaveSettings (SharedPreferences mcSDPrefs){
        setisSDayNightEnabled(true);
        setSDatePeriodEnabled(false);
        setSPeriodFrom(new Date());  //todo this needs to default current date - 7days
        setSPeriodTill(new Date());
        setSRadiusEnabled(false);
        setSRadiusKm(100);
        setMinMagnitude(-1);
        setMaxMagnitude(10);
        setMinSignificance(0);
        setMaxSignificance(1000);
        setLimitResultsTo(100);

        try {
            this.pcSharedSettings = mcSDPrefs;
        }
        catch (Exception e)
        {
            Log.e("n","Pref Manager is NULL" );
        }

    }

    public void saveSettings(String key, boolean value) {
        SharedPreferences.Editor editor = pcSharedSettings.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void saveSettings(String key, String value) {
        SharedPreferences.Editor editor = pcSharedSettings.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveSettings(String key, int value) {
        SharedPreferences.Editor editor = pcSharedSettings.edit();
        editor.putInt(key, value);
        editor.commit();
    }


}
