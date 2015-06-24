package client.locationrequest.model;

import org.json.JSONObject;

import client.locationrequest.model.CustomJSONObject.Bulider;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

/**
 * @author alxr
 * GSM supported only
 */
public class MobileInfo {
	
	private enum Type{GSM, CDMA, UNKNOWN, NONE, UMTS}

	private static final String TAG = MobileInfo.class.getSimpleName();
	
	public MobileInfo(NeighboringCellInfo mInfo, TelephonyManager tm) throws NullPointerException{
		if (mInfo == null || tm == null) throw new NullPointerException("I need NeighboringCellInfo and TelephonyManager objects");
		Type type = getRadioType(mInfo.getNetworkType());
		if (type == Type.GSM) gsmImplementation(mInfo, tm);
		if (type == Type.CDMA) new NullPointerException("Unsupported cellular network");
		if (type == Type.UMTS) umtsImplementation(mInfo, tm);
		if (type == Type.NONE || type == Type.UNKNOWN) throw new NullPointerException("Unsupported cellular network " + type + " " + mInfo.getNetworkType());
    }
	
	private void gsmImplementation(NeighboringCellInfo mInfo, TelephonyManager tm) {
		this.lac = mInfo.getLac();
		this.cellid = mInfo.getCid();
		this.signalStrength = mInfo.getRssi();
		
		String currentRegisteredOperator = tm.getNetworkOperator();
		String mobileCountryCode = null;
		String mobileNetworkCode = null;
        if (currentRegisteredOperator != null && currentRegisteredOperator.length() > 3) {
        	mobileCountryCode = currentRegisteredOperator.substring(0, 3);
        	mobileNetworkCode = currentRegisteredOperator.substring(3);
        }
        this.countrycode = mobileCountryCode != null ? Integer.valueOf(mobileCountryCode):0;
        this.operatorid = mobileNetworkCode != null ? Integer.valueOf(mobileNetworkCode):0; 
		
	}

	private void umtsImplementation(NeighboringCellInfo mInfo, TelephonyManager tm) {
		Log.d(TAG, "umtsImplementation NeighboringCellInfo.getPsc() = " + mInfo.getPsc());
		throw new NullPointerException("Unsupported cellular network");
	}

	public JSONObject toJSONObject(){
		Bulider mBulider = new Bulider();
		mBulider.set("countrycode", countrycode);
		mBulider.set("operatorid", operatorid);
		mBulider.set("cellid", cellid);
		mBulider.set("lac", lac);
		mBulider.set("signal_strength", signalStrength);
		return mBulider.get();
	}
	
	/**
	 * Код страны (MCC, Mobile Country Code)
	 */
	private int countrycode;
	/**
	 * Код сети мобильной связи (MNC, Mobile Network Code)
	 */
	private int operatorid;
	/**
	 * Идентификатор соты (CID, Cell Identifier)
	 */
	private int cellid;
	/**
	 * Код местоположения (LAC, Location area code)
	 */
	private int lac;
	/**
	 * Уровень сигнала, измеренный в месте нахождения мобильного устройства. 
	 * Отрицательное число, выраженное в «децибелах к милливатту» — dBm.
	 */
	private int signalStrength;
	
	private Type getRadioType(int networkType) {
        switch (networkType) {
            case -1:
                return Type.NONE;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_UMTS: return Type.UMTS;
            
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
             return Type.GSM;
            
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_1xRTT: return Type.CDMA;
            
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default: return Type.UNKNOWN;
        }
    }
}