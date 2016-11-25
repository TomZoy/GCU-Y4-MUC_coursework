package com.gcu.zoltantompa.geocoral;

import java.io.Serializable;

/**
 * Created by TomZoy on 2016-11-24.
 * Object-definition for earthquakes
 */

public class EarthQ implements Serializable{

    //declaring variales as Obejct-type, so they can be nullable
    private String id;
    private Float mag;
    private String place;
    private Long time;
    private Integer tz;

    private Integer sig;
    private Long updated;
    private String altert;
    private Integer feltBy;
    private String magSources;
    private Double depth;
    private Double longitude;
    private Double latitude;

    public EarthQ(String id)
    {
        this.id = id;
    }

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

    public String getAltert() {
        return altert;
    }

    public void setAltert(String altert) {
        this.altert = altert;
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
    *
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
