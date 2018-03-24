package mp3ready.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Shared Prefrences Wraper
 * 
 * @author mhmd
 * 
 */
public class AppCache {

	Context ctx;

	public SharedPreferences mem;
	public SharedPreferences.Editor memEditor;
	public static final String MEM_KEY = "com.zgm.mp3ready";

	private static final String USER_ID = "USER_ID";

	public static final String IS_APP_AUTHED = "IS_APP_AUTHED";
	public static final String AUTH_KEY = "AUTH_KEY";

	public static final String EXIT_PLAY_TIME = "EXIT_PLAY_TIME";
	public static final String LAST_TRACK_PLAYED = "LAST_TRACK_PLAYED";

	private static final String TAG = AppCache.class.getName();;

	public AppCache(Context ctx1) {
		ctx = ctx1;

		if (mem == null) {
			mem = ctx.getSharedPreferences(MEM_KEY, 0);
		}
		if (mem != null) {
			memEditor = mem.edit();
		}
	}

	// ------------------------------------

	public boolean isAppAuthed() {
		return mem.getBoolean(IS_APP_AUTHED, false);
	}

	public void setAppAuthed(boolean is) {
		memEditor.putBoolean(IS_APP_AUTHED, is);
		memEditor.commit();
	}

	public String getAuthKey() {
		// 55c3DkhH13Zr6
		return mem.getString(AUTH_KEY, null);
	}

	public void setAuthKey(String authKey) {
		memEditor.putString(AUTH_KEY, authKey);
		memEditor.commit();
	}

	public String getLastTrackPlayed() {
		return mem.getString(LAST_TRACK_PLAYED, "");
	}

	public void setLastTrackPlayed(String songJson) {
		memEditor.putString(LAST_TRACK_PLAYED, songJson);
		memEditor.commit();
	}

	public void setExitPlayTime(Integer pt) {
		memEditor.putInt(EXIT_PLAY_TIME, pt);
		memEditor.commit();
	}

	public Integer getExitPlayTime() {
		return mem.getInt(EXIT_PLAY_TIME, 0);
	}

	public void setMeId(Integer meId) {
		memEditor.putInt(USER_ID, meId);
		memEditor.commit();
	}

	public Integer getMeId() {
		return mem.getInt(USER_ID, -1);
	}
}
