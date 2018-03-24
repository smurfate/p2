

package com.zgm.zlib.directions.model;

import com.google.android.gms.maps.model.LatLng;

import com.google.gson.annotations.SerializedName;
import com.zgm.zlib.directions.util.DirectionConverter;

//import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;


//@Parcel(parcelsIndex = false)
public class Leg {
    @SerializedName("arrival_time")
    TimeInfo arrivalTime;
    @SerializedName("departure_time")
    TimeInfo departureTime;
    Info distance;
    Info duration;
    @SerializedName("duration_in_traffic")
    Info durationInTraffic;
    @SerializedName("end_address")
    String endAddress;
    @SerializedName("end_location")
    Coordination endLocation;
    @SerializedName("start_address")
    String startAddress;
    @SerializedName("start_location")
    Coordination startLocation;
    @SerializedName("steps")
    List<Step> stepList;
    @SerializedName("via_waypoint")
    List<Waypoint> viaWaypointList;

    public TimeInfo getArrivalTime() {
        return arrivalTime;
    }

    public TimeInfo getDepartureTime() {
        return departureTime;
    }

    public Info getDistance() {
        return distance;
    }

    public Info getDuration() {
        return duration;
    }

    public Info getDurationInTraffic() {
        return durationInTraffic;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public Coordination getEndLocation() {
        return endLocation;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public Coordination getStartLocation() {
        return startLocation;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    public List<Waypoint> getViaWaypointList() {
        return viaWaypointList;
    }

    public ArrayList<LatLng> getDirectionPoint() {
        return DirectionConverter.getDirectionPoint(stepList);
    }

    public ArrayList<LatLng> getSectionPoint() {
        return DirectionConverter.getSectionPoint(stepList);
    }
}
