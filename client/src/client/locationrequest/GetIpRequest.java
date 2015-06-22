package client.locationrequest;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class GetIpRequest extends JSONObject{
	
	private static final String API = "http://www.ip-api.com/json";
	
	public GetIpRequest(String body) throws JSONException {
		super(body);
	}

	public static GetIpRequest get(){
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
	      .url(API)
	      .build();
		Response response = null;
		String body = null;
		try {
			response = client.newCall(request).execute();
			body = response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			return new GetIpRequest(body);
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private String getIp(){
		try {
			return getString("query");
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public JSONObject getJson(){
		String ip = getIp();
		JSONObject ipJSON = new JSONObject();
		if (ip != null){
			try {
				ipJSON.put("address_v4", ip);
			} catch (JSONException e) {}
		}
		return ipJSON;
	}
}
/*
 *
 * 
 * {  
   "as":"AS8151 Uninet S.A. de C.V.",
   "city":"Cancún",
   "country":"Mexico",
   "countryCode":"MX",
   "isp":"Telmex",
   "lat":21.0167,
   "lon":-86.9313,
   "org":"Telmex",
   "query":"189.149.92.157",
   "region":"ROO",
   "regionName":"Quintana Roo",
   "status":"success",
   "timezone":"America/Cancun",
   "zip":"77500"
}

*/