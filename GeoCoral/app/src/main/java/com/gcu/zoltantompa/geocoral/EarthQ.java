package com.gcu.zoltantompa.geocoral;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Object-definition for earthquakes
 */

public class EarthQ implements Serializable{

    //declaring variables as Obejct-type, so they are nullable
    private String id;
    private Float mag;
    private String place;
    private Long time;
    private Integer tz;

    private Integer sig;
    private Long updated;
    private String alert;
    private Integer feltBy;
    private String magSources;
    private Double depth;
    private Double longitude;
    private Double latitude;

    SimpleDateFormat ft = new SimpleDateFormat("E yyyy.MM.dd 'at' HH:mm:ss");

    //constructor
    public EarthQ(String id)
    {
        this.id = id;
    }

    //getters + setters
    public Float getMag() {
        return mag;
    }

    public void setMag(Float mag) {
        this.mag = mag;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Long getTime() {
        return time;
    }

    public String getTimeString() {
        Date tmpDate = new Date(this.time);

        Float tz = Float.parseFloat(this.tz.toString())/60;
        String GMT = (tz > 0) ? "+"+tz : Float.toString(tz);

        return ft.format(tmpDate)+" (GMT"+GMT+")";

    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public Integer getTz() {
        return tz;
    }

    public void setTz(Integer tz) {
        this.tz = tz;
    }

    public String getId() {
        return id;
    }

    public Integer getSig() {
        return sig;
    }

    public void setSig(Integer sig) {
        this.sig = sig;
    }

    public String getAlert() {
        return alert;
    }

    public void setAlert(String alert) {
        this.alert = alert;
    }

    public Integer getFeltBy() {
        return feltBy;
    }

    public void setFeltBy(Integer feltBy) {
        this.feltBy = feltBy;
    }

    public String getMagSources() {
        return magSources;
    }

    public void setMagSources(String magSources) {
        this.magSources = magSources;
    }

    public Double getDepth() {
        return depth;
    }

    public void setDepth(Double depth) {
        this.depth = depth;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }





    /*
    *   type-description from the API
    * properties: {
        mag: Decimal,
        place: String,
        time: Long Integer,
        updated: Long Integer,
        tz: Integer,
        url: String,
        detail: String,
        felt:Integer,
        cdi: Decimal,
        mmi: Decimal,
        alert: String,
        status: String,
        tsunami: Integer,
        sig:Integer,
        net: String,
        code: String,
        ids: String,
        sources: String,
        types: String,
        nst: Integer,
        dmin: Decimal,
        rms: Decimal,
        gap: Decimal,
        magType: String,
        type: String
      },
      geometry: {
        type: "Point",
        coordinates: [
          longitude,
          latitude,
          depth
        ]
      },
      id: String*/

}
