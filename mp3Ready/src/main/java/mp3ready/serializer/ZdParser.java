package mp3ready.serializer;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class ZdParser {
	
	public int code ; 
	public String response ; 
	
	public ZdParser(String JsonString ){
//		if(JsonString.contains("<!DOCTYPE"))
//			throw new JSONException("<!DOCTYPE") ;
		try{
			JSONObject jSONObject = new JSONObject(new JSONTokener(JsonString));
			if (jSONObject.has("code"))
				code =  jSONObject.getInt("code") ;
			if (jSONObject.has("response"))
				response =  jSONObject.getString("response")  ;

		}catch (Exception e)
		{
			code = 0;
			response = "";
			Log.d(getClass().getSimpleName(), "ZdParser: failed to parse "+JsonString);
		}

	}

}
