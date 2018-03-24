package com.zgm.zlib.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * This class enables us to use the Shared Preferences easily any where in the app
 * in order for it to work the function createSharedPreference should be called at least once in the app
 * Created by Saad on 1/5/2016.
 */

public class PrefUtil {
    private static SharedPreferences sharedPreferences;

    public static void createSharedPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getPackageName(),Context.MODE_PRIVATE);
    }

    public static void setSharedPreferences(Context context,String name)
    {
        sharedPreferences = context.getSharedPreferences(name,Context.MODE_PRIVATE);
    }

    public static void setStringPreference(String key,String value)
    {
        sharedPreferences.edit().putString(key,value).apply();
    }
    public static void setIntegerPreference(String key,int value)
    {
        sharedPreferences.edit().putInt(key, value).apply();
    }
    public static String getStringPreference(String key)
    {
        return sharedPreferences.getString(key,"");
    }

    public static int getIntegerPreference(String key)
    {
        return sharedPreferences.getInt(key, 0);
    }

    public static void deletePreference(String key)
    {
        sharedPreferences.edit().remove(key).apply();
    }

    public static void clearPreference()
    {
        sharedPreferences.edit().clear().apply();
    }


}
