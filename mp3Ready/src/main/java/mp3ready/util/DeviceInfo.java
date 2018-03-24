package mp3ready.util;



import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;

public class DeviceInfo {

	// NOTE: null value should has vale like : "null"

	// public String AndroidId = "";
	public String Manufacture = "null";
	public String Model = "";
	public String MacAddress = "null";
	public String IMEI = "null";
	public String IMSI = "null";
	public String  OS = "null";
	public String  ipAddress = "null";
	
	 

	//

	public static DeviceInfo GetInfo(Context context) {
		DeviceInfo info = new DeviceInfo();
		info.Manufacture = android.os.Build.BRAND;
		 info.Model = android.os.Build.MODEL;
		 info.OS="Android";
		// info.AndroidId = Secure
		// .getString(context.getContentResolver(), Secure.ANDROID_ID);

		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			info.IMSI = mTelephonyMgr.getSubscriberId();
			info.IMEI = mTelephonyMgr.getDeviceId();// "352005049954343";//"357070007814389";//
			// ZLog.d("imei", info.IMEI);
			// ZLog.d("imsi", info.IMSI);

		} catch (Exception e) {
			e.printStackTrace();
			//Zlog.d("mTelephonyMgr", "Fix me please ", op.info);
		}
		// should be check out of the catch cause the exception is not always
		// thrown
		if (info.IMEI == null) {
			info.IMEI = Secure.getString(context.getContentResolver(),
					Secure.ANDROID_ID);
		}
		try {

			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo wInfo = wifiManager.getConnectionInfo();
			info.MacAddress = wInfo.getMacAddress();
			int ip =  wInfo.getIpAddress();
			info.ipAddress = String.format("%d.%d.%d.%d", (ip & 0xff),(ip >> 8 & 0xff),(ip >> 16 & 0xff),(ip >> 24 & 0xff));

		} catch (Exception e) {
			// Log.e("MacAddress: Can not get MacAddress val",e.getMessage());
		}
		return info;
	}

}
