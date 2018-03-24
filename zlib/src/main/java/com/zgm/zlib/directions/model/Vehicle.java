

package com.zgm.zlib.directions.model;

import com.google.gson.annotations.SerializedName;

//import org.parceler.Parcel;


//@Parcel(parcelsIndex = false)
public class Vehicle {
    @SerializedName("icon")
    String iconUrl;
    String name;
    String type;

    public String getIconUrl() {
        return iconUrl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
