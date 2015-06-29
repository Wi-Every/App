package client.widgets;

import ru.alxr.client.R;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import client.locationrequest.model.WiFiInfo;

public class WiFiInfoView extends LinearLayout{
	
	public static final int LAYOUT = R.layout.item_wi_fi_info_view;
	
	private static final int NAME = R.id.item_wi_fi_info_view_name;
	private static final int MAC = R.id.item_wi_fi_info_view_mac;
	private static final int CAPABILITIES = R.id.item_wi_fi_info_view_capabilities;
	private static final int STRENGTH = R.id.item_wi_fi_info_view_strength;
	private static final int FREQUENCY = R.id.item_wi_fi_info_view_frequency;
	
	public WiFiInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private WiFiInfo mWiFiInfo;
	
	public void setWiFiInfo(WiFiInfo mWiFiInfo) {
		setupCouple(NAME, WiFiInfo.FIELD_NAME_NAME, mWiFiInfo.getNetworkName());
		setupCouple(MAC, WiFiInfo.FIELD_NAME_MAC, mWiFiInfo.getMac());
		setupCouple(CAPABILITIES, WiFiInfo.FIELD_NAME_CAPABILITIES, mWiFiInfo.getCapabilities());
		setupCouple(STRENGTH, WiFiInfo.FIELD_NAME_STRENGTH, mWiFiInfo.getSignalStrength());
		setupCouple(FREQUENCY, WiFiInfo.FIELD_NAME_FREQUENCY, mWiFiInfo.getFrequency());
		if (mWiFiInfo.isCurrent()) setBackgroundColor(getResources().getColor(R.color.light_green));
		else setBackgroundColor(Color.WHITE);
		this.mWiFiInfo = mWiFiInfo;
	}

	public WiFiInfo getWiFiInfo() {
		return mWiFiInfo;
	}

	private void setupCouple(int id, String key, Object value){
		CoupleView mView = (CoupleView) findViewById(id);
		if (mView == null || value == null) return;
		mView.setup(key, value);
	}
}
