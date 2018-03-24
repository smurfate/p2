

package com.zgm.zlib.directions.model;

import com.zgm.zlib.directions.constant.RequestResult;
import com.google.gson.annotations.SerializedName;

//import org.parceler.Parcel;

import java.util.List;


//@Parcel(parcelsIndex = false)
public class Direction {
    @SerializedName("geocoded_waypoints")
    List<GeocodedWaypoint> geocodedWaypointList;
    @SerializedName("routes")
    List<Route> routeList;
    String status;
    @SerializedName("error_message")
    String errorMessage;

    public List<GeocodedWaypoint> getGeocodedWaypointList() {
        return geocodedWaypointList;
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    public String getStatus() {
        return status;
    }

    public boolean isOK() {
        return status.equals(RequestResult.OK);
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
