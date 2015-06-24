package client.locationrequest.model;

import org.json.JSONObject;

import android.net.wifi.ScanResult;

public class WiFiInfo {
	
	public WiFiInfo(ScanResult mResult) throws NullPointerException{
		if (mResult == null) throw new NullPointerException("You sholdn't pass null ScanResult object");
		this.mac = mResult.BSSID;
		this.signalStrength = mResult.level;
	}
	
	/**
	 * MAC-адрес в символьном представлении. 
	 * Байты могут разделяться дефисом, точкой, двоеточием или указываться слитно без разделителя, 
	 * например: «12-34-56-78-9A-BC», «12:34:56:78:9A:BC», «12.34.56.78.9A.BC», «123456789ABC»
	 */
	private String mac;
	/**
	 * Уровень сигнала, измеренный в месте нахождения мобильного устройства. 
	 * Отрицательное число, выраженное в «децибелах к милливатту» — dBm.
	 */
	private int signalStrength;
	/**
	 * Время в миллисекундах, прошедшее с момента получения данных через программный интерфейс мобильного устройства.
	 */
	private long age;
	
	public JSONObject toJSONObject(){
		return new CustomJSONObject	.Bulider()
									.set("mac", mac)
									.set("signal_strength", signalStrength)
									.set("age", age)
									.get();
	}
}