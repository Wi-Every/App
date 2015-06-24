package client.locationrequest;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;
import client.locationrequest.model.MobileInfo;
import client.locationrequest.model.CustomJSONObject.Bulider;
import client.locationrequest.model.WiFiInfo;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

public class YaGeoRequest {
	
	/**
	 * @author alxr
	 * <br>Сбор данных о wifi сетях не мгновенный. 
	 * <br>Завершение сбора сигнализируется броадкастом WifiManager.SCAN_RESULTS_AVAILABLE_ACTION 
	 */
	public interface OnCollectionCompleteListener{
		public void onComplete(YaGeoRequest mYaGeoRequest);
	}
	
	private OnCollectionCompleteListener mListener;
	private List<Couple> data;
	private WifiManager mWifiManager;
	private BroadcastReceiver receiver;
	
	public void collectData(Context context){
		if (context == null) return;
		this.context = context;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		boolean b = mWifiManager.isWifiEnabled();
		Log.d(TAG, "WifiEnabled " + b);
		if (!b) {
			b = mWifiManager.setWifiEnabled(true);
			Log.d(TAG, "WifiManager setWifiEnabled result is " + b);		
		}
		
		registerScanReciever(context);
		mWifiManager.startScan();
	}
	
	private void registerScanReciever(Context context) {
		if (context == null) return;
		if (receiver != null) context.unregisterReceiver(receiver);
		receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				if (mListener != null) mListener.onComplete(YaGeoRequest.this);
				if (YaGeoRequest.this.context != null) YaGeoRequest.this.context.unregisterReceiver(this);
			}
		};
		context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}

	private static final String API = "http://api.lbs.yandex.net/geolocation";
	private static final String DEV_KEY = "AGdEh1UBAAAAZEgPQgIAa8Jl_NG0Syz_U4m41xKGTygGAs4AAAAAAAAAAABF9WwRI5MIeZ8RfAG0EIypESYR2Q==";
	public static final MediaType PLAIN = MediaType.parse("plain/text; charset=utf-8");
	private static final String TAG = YaGeoRequest.class.getSimpleName();
	
	private Context context;
	
	public YaGeoRequest(OnCollectionCompleteListener mListener) {
		this.mListener = mListener;
	}

	public String api(){
		return API;
	}
	
	private List<Couple> getData(){
		if (data == null){
			data = new ArrayList<YaGeoRequest.Couple>();
			data.add(getDefaultData());
			JSONArray mobile = getMobile();
			JSONArray wifi = getWiFi();
			data.add(new Couple("gsm_cells", mobile != null? mobile : new JSONArray()));
			data.add(new Couple("wifi_networks", wifi != null ? wifi : new JSONArray()));
			GetIpRequest mRequest = null;
			try {
				mRequest = GetIpRequest.get();
			} catch (NoInternetException e) {
				e.printStackTrace();
			}
			JSONObject ipJSON = mRequest != null ? mRequest.getJson() : new JSONObject();
			data.add(new Couple("ip", ipJSON));
		}
		return data;
	}
	
	private JSONArray getWiFi() {
		if (context == null) return null;
		if (mWifiManager == null) return null;
		List<ScanResult> list = mWifiManager.getScanResults();
		if (list == null) return null;
		WiFiInfo mWiFiInfo = null;
		JSONArray array = null;
		for (ScanResult mResult : list){
			try {
				mWiFiInfo = new WiFiInfo(mResult);
			} catch (NullPointerException e) {
				e.printStackTrace();
				continue;
			}
			JSONObject object = mWiFiInfo.toJSONObject();
			if (object == null) continue;
			if (array == null) array = new JSONArray();
			array.put(object);
		}
		return array;
	}

	private JSONArray getMobile() {
		if (context == null) return null;
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm == null) return null;
		List<NeighboringCellInfo> list = tm.getNeighboringCellInfo();
		if (list == null) return null;
		MobileInfo mGsmInfo = null;
		JSONArray array = null;
		for (NeighboringCellInfo mInfo : list){
			try {
				mGsmInfo = new MobileInfo(mInfo, tm);
			} catch (NullPointerException e) {
				e.printStackTrace();
				continue;
			}
			JSONObject object = mGsmInfo.toJSONObject();
			if (object == null) continue;
			if (array == null) array = new JSONArray();
			array.put(object);
			Log.d(TAG, "" + mInfo.toString() + " " + mInfo.getCid());
		}
		return array;
	}

	@Override
	public String toString(){
		List<Couple> data = getData();
		Bulider mBulider = new Bulider();
		for (Couple mCouple : data) mBulider.set(mCouple.key, mCouple.value);
		return mBulider.get().toString();
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