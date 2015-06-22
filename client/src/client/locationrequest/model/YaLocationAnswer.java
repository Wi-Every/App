package client.locationrequest.model;

import android.content.Context;
import android.util.Log;
import client.database.DatabaseManager;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable (tableName = YaLocationAnswer.TABLE_NAME)
public class YaLocationAnswer {
	
	public static final String TABLE_NAME = "yandex_location";
	private static final String TAG = YaLocationAnswer.class.getSimpleName();
	
	public YaLocationAnswer(){}
	
	public YaLocationAnswer(CustomJSONObject mObject) {
		Log.d(TAG, mObject + "");
		setAltitude(mObject.getDouble(KEY_ALTITUDE));
		setAltitudePrecision(mObject.getDouble(KEY_ALTITUDE_PRECISION));
		setLatitude(mObject.getDouble(KEY_LATITUDE));
		setLongitude(mObject.getDouble(KEY_LONGITUDE));
		setPrecision(mObject.getDouble(KEY_PRECISION));
		setType(mObject.getString(KEY_TYPE));
	}

	// Yandex answer fields
	
	public static final String FIELD_LATITUDE = "latitude";
	public static final String FIELD_LONGITUDE = "longitude";
	public static final String FIELD_ALTITUDE = "altitude";
	public static final String FIELD_PRECISION = "precision";
	public static final String FIELD_ALTITUDE_PRECISION = "altitude_precision";
	public static final String FIELD_TYPE = "type";
	
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGITUDE = "longitude";
	public static final String KEY_ALTITUDE = "altitude";
	public static final String KEY_PRECISION = "precision";
	public static final String KEY_ALTITUDE_PRECISION = "altitude_precision";
	public static final String KEY_TYPE = "type";
	
	//local fields
	/**
	 * Время, когда был получен ответ. 
	 * Строка, содержащая часовой пояс.
	 */
	public static final String FIELD_TIME = "time";
	
	@DatabaseField (columnName = FIELD_LATITUDE) private double latitude;
	@DatabaseField (columnName = FIELD_LONGITUDE) private double longitude;
	@DatabaseField (columnName = FIELD_ALTITUDE) private double altitude;
	@DatabaseField (columnName = FIELD_PRECISION) private double precision;
	@DatabaseField (columnName = FIELD_ALTITUDE_PRECISION) private double altitudePrecision;
	@DatabaseField (columnName = FIELD_TYPE) private String type;
	@DatabaseField (columnName = FIELD_TIME) private String time;
	
	private void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	private void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	private void setAltitude(double altitude) {
		this.altitude = altitude;
	}
	private void setPrecision(double precision) {
		this.precision = precision;
	}
	private void setAltitudePrecision(double altitudePrecision) {
		this.altitudePrecision = altitudePrecision;
	}
	private void setType(String type) {
		this.type = type;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public double getLatitude() {
		return latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public double getAltitude() {
		return altitude;
	}
	public double getPrecision() {
		return precision;
	}
	public double getAltitudePrecision() {
		return altitudePrecision;
	}
	public String getType() {
		return type;
	}
	
	public static YaLocationAnswer get(CustomJSONObject mObject){
		if (mObject == null) return null;
		return new YaLocationAnswer(mObject);
	}
	
	public void create(Context context){
		Log.d(TAG, "create " + getType());
		DatabaseManager dm = DatabaseManager.get(context);
		if (dm == null) return;
		dm.createObject(this);
	}
	
	

}
/*
 * {
  "position": {
    "latitude": 55.743675,
    "longitude": 37.5646301,
    "altitude": 0.0, 
    "precision": 701.71643,
    "altitude_precision": 30.0, 
    "type": "gsm"
  }
}
 * */