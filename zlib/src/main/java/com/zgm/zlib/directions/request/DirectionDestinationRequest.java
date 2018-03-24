package com.zgm.zlib.directions.request;

import com.google.android.gms.maps.model.LatLng;

public class DirectionDestinationRequest {
    String apiKey;
    LatLng origin;

    public DirectionDestinationRequest(String apiKey, LatLng origin) {
        this.apiKey = apiKey;
        this.origin = origin;
    }

    public DirectionRequest to(LatLng destination) {
        return new DirectionRequest(apiKey, origin, destination);
    }
}
