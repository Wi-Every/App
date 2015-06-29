package client.collectors;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class WiFiCollector {
	
	private static final String TAG = WiFiCollector.class.getSimpleName();

	public WiFiCollector (){}
	
	/**
	 * @author alxr
	 * <br>Сбор данных о wifi сетях не мгновенный. 
	 * <br>Завершение сбора сигнализируется броадкастом WifiManager.SCAN_RESULTS_AVAILABLE_ACTION 
	 */
	public interface OnCollectionCompleteListener{
		public void onComplete(WiFiCollector mWiFiCollector);
		public Context getContext();
	}
	
	private OnCollectionCompleteListener mListener;
	
	private WifiManager mWifiManager;
	private BroadcastReceiver receiver;
	
	public void collectData(OnCollectionCompleteListener mListener){
		Context context;
		if (mListener == null || (context = mListener.getContext()) == null) return;
		this.mListener = mListener;
		mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		if (!mWifiManager.isWifiEnabled()) mWifiManager.setWifiEnabled(true);
		registerScanReciever(context);
		mWifiManager.startScan();
	}
	
	private void registerScanReciever(Context context) {
		Log.d(TAG, "registerScanReciever");
		if (context == null) return;
		if (receiver != null) context.unregisterReceiver(receiver);
		receiver = new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				Log.d(TAG, "registerScanReciever onReceive");
				if (mListener != null) mListener.onComplete(WiFiCollector.this);
				if (mListener != null && mListener.getContext() != null) mListener.getContext().unregisterReceiver(this);
			}
		};
		context.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
	}
	
	public void stop(){
		if (mListener != null && mListener.getContext() != null && receiver != null)
			try {
				mListener.getContext().unregisterReceiver(receiver);
			} catch (IllegalArgumentException e) {}
		mListener = null;
		mWifiManager = null;
		receiver = null;
	}

	public List<ScanResult> getScanResults() {
		if (mWifiManager == null) return null;
		return mWifiManager.getScanResults();
	}
	
	public WifiInfo getActive(){
		if (mWifiManager == null) return null;
		return mWifiManager.getConnectionInfo();
	}
}