package mp3ready.http;

import android.util.Log;

public abstract class UpdateCallBack {

	public abstract void onUpdate(int result);

	public void onUpdateI(int result) {
		Log.e(UpdateCallBack.class.getName(), result + "/100");
	}

}
