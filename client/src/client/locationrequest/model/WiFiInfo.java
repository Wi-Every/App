package client.locationrequest.model;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.util.Log;
import client.database.DatabaseManager;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable (tableName = WiFiInfo.TABLE_NAME)
public class WiFiInfo {
	
	public WiFiInfo(){}
	
	public static final String TABLE_NAME = "wifi_info";
	public static final String FIELD_NAME_MAC = "mac";
	public static final String FIELD_NAME_STRENGTH = "strength";
	public static final String FIELD_NAME_CAPABILITIES = "capabilities";
	public static final String FIELD_NAME_NAME = "networkName";
	public static final String FIELD_NAME_FREQUENCY = "frequency";
	
	private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final String ALOGORITHM = "AES";
	private static final String ENCODING = "UTF-8";
	
	public static final String FIELD_NAME_PASSWORD = "password";
	
	private static final String TAG = "WiFiInfo";
	
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
	 * http://stackoverflow.com/questions/339004/java-encrypt-decrypt-user-name-and-password-from-a-configuration-file
	 */
	@DatabaseField (columnName = FIELD_NAME_PASSWORD, dataType = DataType.SERIALIZABLE) private byte [] password;
	
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
	
	public void connect(Context context){
		if (context == null) return;
		WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiConfiguration wc = new WifiConfiguration();
		wc.SSID = "\"" + getNetworkName() + "\"";
		wc.preSharedKey  = "\"95465475F0\"";//AeSimpleSHA1 m;
		wc.hiddenSSID = true;
		wc.status = WifiConfiguration.Status.ENABLED;        
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
		wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
		wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
		wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
		wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
		int res = wifi.addNetwork(wc);
		Log.d("WifiPreference", "add Network returned " + res );
		boolean b = wifi.enableNetwork(res, true);        
		Log.d("WifiPreference", "enableNetwork returned " + b );
	}
	
	private SecretKeySpec getSecretKeySpec(Context context){
		byte [] keyArray = null;
		String uniq = getUniqDeviceId(context);
		try {
			keyArray = uniq.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			return null;
		} catch (NullPointerException e){
			return null;
		}
		if (keyArray.length != 16){
			byte[] array = new byte[16];
			for (int i = 0; i < 16; i++) if (i < keyArray.length) array[i] = keyArray[i];
			keyArray = array;
		}
		return new SecretKeySpec(keyArray, ALOGORITHM);
	}
	
	/**
	 * @param source
	 * Сохранять сериализованный объект byte[]
	 */
	public void setPassword(Context context, String source)  {
		SecretKeySpec key = getSecretKeySpec(context);
		this.password = encryptMsg(source, key);
		Log.d(TAG, "setPassword " + source);
	}
	
	public String getPassword(Context context){
		SecretKeySpec key = getSecretKeySpec(context);
		String decripted = decryptMsg(this.password, key);
		Log.d(TAG, "getPassword " + decripted);
		return decripted;
	}
	
	@SuppressLint("TrulyRandom")
	private byte[] encryptMsg(String message, SecretKey secret){
		Cipher cipher = null;
	    try {
			cipher = Cipher.getInstance(TRANSFORMATION);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		}
	    try {
			cipher.init(Cipher.ENCRYPT_MODE, secret);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}
	    byte[] res = null;
	    try {
	    	res = cipher.doFinal(message.getBytes(ENCODING));
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return res;
	}

	
	private String decryptMsg(byte[] cipherText, SecretKey secret) {
		Cipher cipher = null;
	    try {
	    	cipher = Cipher.getInstance(TRANSFORMATION);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
			return null;
		}
	    try {
	    	cipher.init(Cipher.DECRYPT_MODE, secret);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			return null;
		}
	    String resString = null;
	    byte[] res = null;
	    try {
	    	res = cipher.doFinal(cipherText);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} 
	    try {
			resString = new String(res, ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	    return resString;
	}
	
	private String getUniqDeviceId(Context context) {
		ContentResolver contentResolver;
		if (context == null || (contentResolver = context.getContentResolver()) == null) return null;
		StringBuilder builder = new StringBuilder();
		String androidId = Secure.getString(contentResolver, Secure.ANDROID_ID);
		if (androidId != null) builder.append(androidId);
		if (androidId == null) return null;
		return builder.toString();
	}

	public void createOrUpdate(Context mContext) {
		DatabaseManager dm  = DatabaseManager.get(mContext);
		if (dm == null) return;
		Object o = dm.getFirst(WiFiInfo.class, new Object[]{FIELD_NAME_MAC, getMac()});
		update(o);
		if (o == null) dm.createObject(this);
		else dm.updateObject(this);
	}
	
	private void update(Object object){
		if (object == null) return;
		if (object.getClass() != WiFiInfo.class) return;
		WiFiInfo saved = (WiFiInfo) object;
		this.index = saved.index;
	}
}