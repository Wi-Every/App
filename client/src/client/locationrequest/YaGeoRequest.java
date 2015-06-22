package client.locationrequest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

public class YaGeoRequest {
	
	private static final String API = "http://api.lbs.yandex.net/geolocation";
	private static final String DEV_KEY = "AGdEh1UBAAAAZEgPQgIAa8Jl_NG0Syz_U4m41xKGTygGAs4AAAAAAAAAAABF9WwRI5MIeZ8RfAG0EIypESYR2Q==";
	public static final MediaType PLAIN = MediaType.parse("plain/text; charset=utf-8");
	
	public String api(){
		return API;
	}
	
	public static YaGeoRequest getDefault(){
		return new YaGeoRequest();
	}
	
	private List<Couple> data;
	
	private List<Couple> getData(){
		if (data == null){
			data = new ArrayList<YaGeoRequest.Couple>();
			data.add(getDefaultData());
			data.add(new Couple("gsm_cells", new JSONArray()));
			data.add(new Couple("wifi_networks", new JSONArray()));
			GetIpRequest mRequest = GetIpRequest.get();
			JSONObject ipJSON = mRequest != null ? mRequest.getJson() : new JSONObject();
			data.add(new Couple("ip", ipJSON));
			
		}
		return data;
	}
	
	@Override
	public String toString(){
		List<Couple> data = getData();
		JSONObject object = new JSONObject();
		for (Couple mCouple : data){
			try {
				object.put(mCouple.key, mCouple.value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return object.toString();
	}
	
	
	private static class Couple{
		String key;
		Object value;
		public Couple (String key, Object value){
			this.key = key;
			this.value = value;
		}
	}
	
	private static Couple getDefaultData(){
		JSONObject mObject= new JSONObject();
		try {
			mObject.put("version", "1.0");
			mObject.put("api_key", DEV_KEY);
		} catch (JSONException e) {}
		return new Couple("common", mObject);
	}
	
	public RequestBody getRequestBody(){
		return RequestBody.create(PLAIN, "json=" + toString());
	}
	
}
/*Обязательное поле
 * В случае, если параметры gsm_cells, wifi_networks, ip отсутствуют в запросе, 
 * а также если предоставлены неверные данные, Яндекс.Локатор определяет местоположение 
 * по IP-адресу отправителя, взятому из заголовка IP-пакета. Этот адрес может быть подменен прокси-сервером, 
 * через который прошел IP-пакет, в результате чего местоположение может определиться неправильно.
 * "common": {
      "version": "1.0",
      "api_key": "ABM6WU0BAAAANfFuIQIAV1pUEYIBeogyUNvVbhNaJPWeM-AAAAAAAAAAAACRXgDsaYNpZWpBczn4Lq6QmkwK6g=="
   }
   
   
   "ip": {
     "address_v4": "178.247.233.32"
   }
 * */