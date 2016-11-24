package com.gcu.zoltantompa.geocoral;

/**
 * Created by TomZoy on 2016-11-24.
 * Object-definition for earthquakes
 */

public class EarthQ {

    //declaring variales as Obejct-type, so they can be nullable
    private String id;
    private Float mag;
    private String place;
    private Long time;
    private Long updated;
    private Integer tz;

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
