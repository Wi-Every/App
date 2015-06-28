package client.locationrequest.model;

import org.json.JSONObject;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import android.net.wifi.ScanResult;

@DatabaseTable (tableName = WiFiInfo.TABLE_NAME)
public class WiFiInfo {
	
	public WiFiInfo(){}
	
	public static final String TABLE_NAME = "wifi_info";
	public static final String FIELD_NAME_MAC = "mac";
	public static final String FIELD_NAME_STRENGTH = "strength";
	public static final String FIELD_NAME_CAPABILITIES = "capabilities";
	public static final String FIELD_NAME_NAME = "networkName";
	public static final String FIELD_NAME_FREQUENCY = "frequency";
	
	public WiFiInfo(ScanResult mResult) throws NullPointerException{
		if (mResult == null) throw new NullPointerException("You sholdn't pass null ScanResult object");
		this.mac = mResult.BSSID;
		this.signalStrength = mResult.level;
		this.capabilities = mResult.capabilities;
		this.networkName = mResult.SSID; 
		this.frequency = mResult.frequency;
	}
	
	@DatabaseField (generatedId = true) private int index;
	@DatabaseField (columnName = FIELD_NAME_CAPABILITIES) private String capabilities;
	@DatabaseField (columnName = FIELD_NAME_NAME) private String networkName;
	@DatabaseField (columnName = FIELD_NAME_FREQUENCY) private int frequency;
	
	/**
	 * MAC-адрес в символьном представлении. 
	 * Байты могут разделяться дефисом, точкой, двоеточием или указываться слитно без разделителя, 
	 * например: «12-34-56-78-9A-BC», «12:34:56:78:9A:BC», «12.34.56.78.9A.BC», «123456789ABC»
	 */
	@DatabaseField (columnName = FIELD_NAME_MAC) private String mac;
	/**
	 * Уровень сигнала, измеренный в месте нахождения мобильного устройства. 
	 * Отрицательное число, выраженное в «децибелах к милливатту» — dBm.
	 */
	@DatabaseField (columnName = FIELD_NAME_STRENGTH) private int signalStrength;
	
	/**
	 * Время в миллисекундах, прошедшее с момента получения данных через программный интерфейс мобильного устройства.
	 * Пока что я не понял зачем это поле, как его использовать. Поэтому не сохраняю.
	 */
	private long age;
	
	/**
	 * true if this is current connected network;
	 */
	private boolean current;
	
	public boolean isCurrent() {
		return current;
	}

	public void setCurrent(boolean current) {
		this.current = current;
	}

	/**
	 * @return yandex required json
	 */
	public JSONObject toJSONObject(){
		return new CustomJSONObject	.Bulider()
									.set("mac", mac)
									.set("signal_strength", signalStrength)
									.set("age", age)
									.get();
	}

	public String getCapabilities() {
		return capabilities;
	}

	public String getNetworkName() {
		return networkName;
	}

	public int getFrequency() {
		return frequency;
	}

	public String getMac() {
		return mac;
	}

	public int getSignalStrength() {
		return signalStrength;
	}
}