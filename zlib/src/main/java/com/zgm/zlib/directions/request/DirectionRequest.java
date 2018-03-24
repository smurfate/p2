package com.zgm.zlib.directions.request;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.zgm.zlib.http.CallbackNoCache;
import com.zgm.zlib.http.HttpAPI;
import com.zgm.zlib.directions.constant.DirectionUrl;
import com.zgm.zlib.directions.model.Direction;

import java.util.HashMap;

public class DirectionRequest {
    protected DirectionRequestParam param;

    public DirectionRequest(String apiKey, LatLng origin, LatLng destination) {
        param = new DirectionRequestParam().setApiKey(apiKey).setOrigin(origin).setDestination(destination);
    }

    public DirectionRequest transportMode(String transportMode) {
        param.setTransportMode(transportMode);
        return this;
    }

    public DirectionRequest language(String language) {
        param.setLanguage(language);
        return this;
    }

    public DirectionRequest unit(String unit) {
        param.setUnit(unit);
        return this;
    }

    public DirectionRequest avoid(String avoid) {
        String oldAvoid = param.getAvoid();
        if (oldAvoid != null && !oldAvoid.isEmpty()) {
            oldAvoid += "|";
        } else {
            oldAvoid = "";
        }
        oldAvoid += avoid;
        param.setAvoid(oldAvoid);
        return this;
    }

    public DirectionRequest transitMode(String transitMode) {
        String oldTransitMode = param.getTransitMode();
        if (oldTransitMode != null && !oldTransitMode.isEmpty()) {
            oldTransitMode += "|";
        } else {
            oldTransitMode = "";
        }
        oldTransitMode += transitMode;
        param.setTransitMode(oldTransitMode);
        return this;
    }

    public DirectionRequest alternativeRoute(boolean alternative) {
        param.setAlternatives(alternative);
        return this;
    }

    public DirectionRequest departureTime(String time) {
        param.setDepartureTime(time);
        return this;
    }

    private String getMapsApiDirectionsUrl(LatLng origin, LatLng destination)
    {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(origin.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(origin.longitude));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&sensor=false&mode=driving&alternatives=true");
//        urlString.append("&key=YOUR_API_KEY");
        return urlString.toString();
    }

    private String getMapsApiDirectionsUrl(double sourcelat, double sourcelog, double destlat, double destlog)
    {
        StringBuilder urlString = new StringBuilder();
        urlString.append("http://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString.append(Double.toString(sourcelog));

        urlString.append("&destination=");// to
        urlString.append(Double.toString(destlat));
        urlString.append(",");
        urlString.append(Double.toString(destlog));

        urlString.append("&sensor=false&mode=driving&alternatives=true");
//        urlString.append("&key=YOUR_API_KEY");
        return urlString.toString();
    }

    private String getMapsApiDirectionsUrl(double originLatitude, double originLongitude, double destinationLatitude, double destinationLongitude,
                                           String transportMode,String transitMode,String avoidType,String wayPoints)
    {
        StringBuilder urlString = new StringBuilder();
        urlString.append(DirectionUrl.MAPS_API_URL);
        urlString.append(DirectionUrl.DIRECTION_API_URL);
        urlString.append("?origin=");// from
        urlString.append(Double.toString(originLatitude));
        urlString.append(",");
        urlString.append(Double.toString(originLongitude));

        urlString.append("&destination=");// to
        urlString.append(Double.toString(destinationLatitude));
        urlString.append(",");
        urlString.append(Double.toString(destinationLongitude));

        if(transportMode != null)
        {
            urlString.append("&mode=");
            urlString.append(transportMode);
        }

        if(transitMode != null)
        {
            urlString.append("&transit_mode=");
            urlString.append(transitMode);
        }

        if(avoidType != null)
        {
            urlString.append("&avoid=");
            urlString.append(avoidType);
        }

        if(wayPoints != null)
        {
            urlString.append("&waypoints=");
            urlString.append(wayPoints);
        }

        urlString.append("&sensor=false&mode=driving&alternatives=true");
//        urlString.append("&key=YOUR_API_KEY");
        return urlString.toString();
    }

    public void execute(Context context, final DirectionsCallback callback) {

        CallbackNoCache tmp = new CallbackNoCache() {
            @Override
            public void onFinished(String response, HashMap<String, String> headers) {
                Direction direction = new Gson().fromJson(response,Direction.class);
                callback.onFinished(direction);
            }

            @Override
            public void onProblem(HttpAPI.ErrorMessage errorMessage) {
                callback.onProblem(errorMessage);
            }
        };
        HttpAPI api = new HttpAPI(context);
        String url = getMapsApiDirectionsUrl(param.origin.latitude, param.origin.longitude, param.destination.latitude, param.destination.longitude,param.transportMode, param.transitMode, param.avoid, null);
        api.get(url, tmp);

    }
}
