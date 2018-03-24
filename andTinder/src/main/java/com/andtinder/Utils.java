/**
 * AndTinder v0.1 for Android
 *
 * @Author: Enrique López Mañas <eenriquelopez@gmail.com>
 * http://www.lopez-manas.com
 *
 * TAndTinder is a native library for Android that provide a
 * Tinder card like effect. A card can be constructed using an
 * image and displayed with animation effects, dismiss-to-like
 * and dismiss-to-unlike, and use different sorting mechanisms.
 *
 * AndTinder is compatible with API Level 13 and upwards
 *
 * @copyright: Enrique López Mañas
 * @license: Apache License 2.0
 */

package com.andtinder;


import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class Utils {

	public static float functionNormalize(int max, int min, int value) {
		int intermediateValue = max - min;
		value -= intermediateValue;
		float var = Math.abs((float)value/(float)intermediateValue);
		return Math.abs((float)value/(float)intermediateValue);
	}
	public static int[] getScreenSizePixels(Context context)
	{
		int widthHeightInPixels[] = {0,0};
	    Resources resources = context.getResources();
	    Configuration config = resources.getConfiguration();
	    DisplayMetrics dm = resources.getDisplayMetrics();
	    // Note, screenHeightDp isn't reliable
	    // (it seems to be too small by the height of the status bar),
	    // but we assume screenWidthDp is reliable.
	    // Note also, dm.widthPixels,dm.heightPixels aren't reliably pixels
	    // (they get confused when in screen compatibility mode, it seems),
	    // but we assume their ratio is correct.
	    double screenWidthInPixels = (double)config.screenWidthDp * dm.density;
	    double screenHeightInPixels = screenWidthInPixels * dm.heightPixels / dm.widthPixels;
	    widthHeightInPixels[0] = (int)(screenWidthInPixels + .5);
	    widthHeightInPixels[1] = (int)(screenHeightInPixels + .5);
	    return widthHeightInPixels ;
	}
}
