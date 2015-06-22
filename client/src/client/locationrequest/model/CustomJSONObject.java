package client.locationrequest.model;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomJSONObject extends JSONObject {
	
	static final String TAG = CustomJSONObject.class.getSimpleName();

	public static CustomJSONObject getCustom(JSONObject source){
		if (source == null) return null;
		try {
			CustomJSONObject mObject = new CustomJSONObject(source.toString());
			return mObject;
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	
	public CustomJSONObject(String source) throws JSONException{
		super(source);
	}
	
	@Override
	public String getString(String key){
		String object = null;
		try {
			object = super.getString(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public double getDouble(String key){
		double d = 0;
		try {
			d = super.getDouble(key);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return d;
	}
}