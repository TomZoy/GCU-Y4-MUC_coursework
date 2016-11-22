package com.gcu.zoltantompa.geocoral;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

/**
 * code taken and modified from Lab 8
 */

public class pcSaveSettings extends Activity {


    // *********************************************
    // Declare variables etc.
    // *********************************************

    SharedPreferences pcSharedSettings;

    private boolean isSAudioEnabled;
    private boolean isSDayNightEnabled;
    private boolean isSDatePeriodEnabled;
    private Date SPeriodFrom;
    private Date SPeriodTill;
    private boolean isSRadiusEnabled;
    private  int SRadiusKm;


    //region Declare getters and setters etc.
    public boolean getSAudioEnabled() {
        return isSAudioEnabled;
    }

    public void setisSAudioEnabled(boolean SAudioEnabled) {
        this.isSAudioEnabled = SAudioEnabled;
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
        setisSAudioEnabled(false);
        setisSDayNightEnabled(true);
        setSDatePeriodEnabled(false);
        setSPeriodFrom(new Date());  //todo this needs to default current date - 7days
        setSPeriodTill(new Date());
        setSRadiusEnabled(false);
        setSRadiusKm(100);

        try {
            this.pcSharedSettings = mcSDPrefs;
        }
        catch (Exception e)
        {
            Log.e("n","Pref Manager is NULL" );
        }

        //setDefaultSettings();  //todo not sure why this is here???
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

    public void setDefaultSettings(){
        saveSettings("SisSAudioEnabled", false);
        saveSettings("SisSDayNightEnabled", false);
        saveSettings("SisSDatePeriodEnabled", false);
        saveSettings("SPeriodFrom", null);
        saveSettings("SPeriodTill", null);
        saveSettings("SisSRadiusEnabled", false);
        saveSettings("SRadiusKm", 150);
    }



}
