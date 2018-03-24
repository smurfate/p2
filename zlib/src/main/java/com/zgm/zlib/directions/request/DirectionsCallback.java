package com.zgm.zlib.directions.request;

import com.zgm.zlib.http.HttpAPI;
import com.zgm.zlib.directions.model.Direction;


/**
 * Created by Saad on 11/18/2016.
 */

public interface DirectionsCallback {
    void onFinished(Direction response);
    void onProblem(HttpAPI.ErrorMessage errorMessage);

}
