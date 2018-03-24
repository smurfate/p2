package com.zgm.zlib.http;

import com.zgm.zlib.http.HttpAPI;

import java.util.HashMap;

/**
 * Created by Saad on 11/18/2016.
 */
public interface CallbackNoCache
{
    void onFinished(String response,HashMap<String,String> headers);
    void onProblem(HttpAPI.ErrorMessage errorMessage);
}
