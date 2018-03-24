package com.zgm.zlib.directions;

import com.zgm.zlib.directions.request.DirectionOriginRequest;

/**
 * Created by Saad on 4/10/2016.
 */
public class RoadAPI {
    public static DirectionOriginRequest withServerKey(String apiKey) {
        return new DirectionOriginRequest(apiKey);
    }

}
