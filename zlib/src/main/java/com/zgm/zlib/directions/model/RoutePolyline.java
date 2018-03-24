

package com.zgm.zlib.directions.model;

import com.zgm.zlib.directions.util.DirectionConverter;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

//import org.parceler.Parcel;

import java.util.List;


//@Parcel(parcelsIndex = false)
public class RoutePolyline {
    @SerializedName("points")
    String rawPointList;

    public String getRawPointList() {
        return rawPointList;
    }

    public List<LatLng> getPointList() {
        return DirectionConverter.decodePoly(rawPointList);
    }
}
