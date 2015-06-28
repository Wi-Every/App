package client;

import java.util.ArrayList;
import java.util.List;

import ru.alxr.client.R;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import client.adapters.WiFiSpotsAdapter;
import client.collectors.WiFiCollector;
import client.collectors.WiFiCollector.OnCollectionCompleteListener;
import client.locationrequest.model.WiFiInfo;

public class WiFiSpotsObserver extends ActionBarActivity {
	
	private static final int LAYOUT = R.layout.activity_wifi_spots_observer;
	private static final int BUTTON = R.id.wifi_spots_observer_activity_collect;
	private static final int LIST_VIEW = R.id.wifi_spots_observer_activity_list_view;
	protected static final String TAG = WiFiSpotsObserver.class.getSimpleName();
	
	private WiFiCollector mWiFiCollector;
	
	public void onCreate(Bundle saved){
		super.onCreate(saved);
		setContentView(LAYOUT);
		setupButton();
		setupListView(null);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		switch (item.getItemId()) {
		case R.id.show_location: 
			startActivity(new Intent(this, MainActivity.class));
			return true;
			default: break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void setupListView(List<WiFiInfo> list) {
		ListView mListView = (ListView) findViewById(LIST_VIEW);
		if (mListView == null) return;
		WiFiSpotsAdapter mAdapter;
		if ((mAdapter = (WiFiSpotsAdapter) mListView .getAdapter()) == null) {
			mAdapter = WiFiSpotsAdapter.get(WiFiSpotsObserver.this);
			mListView .setAdapter(mAdapter);
		}
		if (list == null) {
			mAdapter.dropData();
			return;
		}
		mAdapter.setData(list);
		mAdapter.notifyDataSetChanged();
		
	}

	private void setupButton() {
		View view = findViewById(BUTTON);
		if (view == null) return;
		view.setOnClickListener(listener);
	}
	
	private View.OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Log.d(TAG, "BUTTON clicked");
			ListView mListView = (ListView) findViewById(LIST_VIEW);
			if (mListView != null && mListView.getAdapter() != null) ((WiFiSpotsAdapter)mListView.getAdapter()).dropData();
			if (mWiFiCollector != null) mWiFiCollector.stop();
			else mWiFiCollector = new WiFiCollector();
			mWiFiCollector.collectData(new OnCollectionCompleteListener() {
				
				@Override
				public void onComplete(WiFiCollector mWiFiCollector) {
					Log.d(TAG, "onComplete");
					if (mWiFiCollector == null) return;
					List<ScanResult> list = mWiFiCollector.getScanResults();
					if (list == null) return;
					
					WiFiInfo mInfo = null;
					List<WiFiInfo> mWiFiInfos = new ArrayList<WiFiInfo>();
					WifiInfo mWifiInfo = mWiFiCollector.getActive();
					for (ScanResult mResult : list) {
						try {
							mInfo = new WiFiInfo(mResult);
						} catch (NullPointerException e) {
							e.printStackTrace();
							continue;
						}
						if (mWifiInfo != null && mWifiInfo.getMacAddress() != null){
							Log.d(TAG, "Connected to " + mWifiInfo.getBSSID() + " comparing with " + mInfo.getMac());
							mInfo.setCurrent(mWifiInfo.getBSSID().equals(mInfo.getMac()));
						}
						mWiFiInfos .add(mInfo);
					}
					setupListView(mWiFiInfos);
					mWiFiCollector.stop();
				}
				
				@Override
				public Context getContext() {
					return WiFiSpotsObserver.this;
				}
			});
		}
	};
}