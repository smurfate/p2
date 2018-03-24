

package com.zgm.zlib.directions.model;

import com.google.gson.annotations.SerializedName;

//import org.parceler.Parcel;


//@Parcel(parcelsIndex = false)
public class TimeInfo {
    String text;
    @SerializedName("time_zone")
    String timeZone;
    String value;

    public String getText() {
        return text;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getValue() {
        return value;
    }
}
