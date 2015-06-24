package client;

import java.io.IOException;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import ru.alxr.client.R;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import client.adapters.YaLocationAnswerAdapter;
import client.database.DatabaseManager;
import client.locationrequest.YaGeoRequest;
import client.locationrequest.YaGeoRequest.OnCollectionCompleteListener;
import client.locationrequest.model.CustomJSONObject;
import client.locationrequest.model.YaLocationAnswer;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

/**
 * @author alxr
 * «абавно, что сервис http://www.ip-api.com/json дает большую точность по ip, чем yandex
 */
public class MainActivity extends Activity {
	
	private static final int LAYOUT = R.layout.main_activity;
	private static final int LIST_VIEW = R.id.main_activity_listview;
	private static final int BUTTON = R.id.main_activity_get_location_widget;
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private AsyncTask<YaGeoRequest, String, String> mTask;
	private AsyncTask<Void, List <YaLocationAnswer>, List <YaLocationAnswer>> listViewSetup;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(LAYOUT);
		buttonSetup();
		listViewSetup();
	}
	
	private void listViewSetup() {
		if (listViewSetup != null) listViewSetup.cancel(true);
		listViewSetup = new AsyncTask<Void, List <YaLocationAnswer>, List <YaLocationAnswer>>() {
			
			@SuppressWarnings("unchecked")
			@Override
			protected List<YaLocationAnswer> doInBackground(Void... params) {
				DatabaseManager dm = DatabaseManager.get(MainActivity.this);
				if (dm == null) return null;
				List <Object> list = dm.getAll(YaLocationAnswer.class);
				if (list == null) return null;
				Log.d(TAG, "listViewSetup " + list.size());
				return (List<YaLocationAnswer>)(List<?>) list;
			}
			
			@Override 
			public void onPostExecute(List <YaLocationAnswer> list){
				if (isCancelled()) return;
				ListView mListView = (ListView) findViewById(LIST_VIEW);
				if (mListView == null || list == null) return;
				YaLocationAnswerAdapter mAdapter = (YaLocationAnswerAdapter) mListView.getAdapter();
				if (mAdapter == null) {
					mAdapter = YaLocationAnswerAdapter.get(MainActivity.this);
					mListView.setAdapter(mAdapter);
				}
				mAdapter.setData(list);
				mAdapter.notifyDataSetChanged();
			}
		};
		listViewSetup.execute();
	}

	private void buttonSetup() {
		View view = findViewById(BUTTON);
		if (view == null) return;
		view.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				start();
			}
		});
	}

	private void start(){
		if (mTask != null) mTask.cancel(true);
		YaGeoRequest mRequest = new YaGeoRequest(new OnCollectionCompleteListener() {
			
			@Override
			public void onComplete(YaGeoRequest mYaGeoRequest) {
				mTask = new AsyncTask<YaGeoRequest, String, String>() {
					
					@Override
					protected String doInBackground(YaGeoRequest... params) {
						YaGeoRequest mGeoRequest = params[0];
						OkHttpClient client = new OkHttpClient();
						Request request = new Request.Builder()
						      .url(mGeoRequest.api())
						      .post(mGeoRequest.getRequestBody())
						      .build();

						Response response = null;
						String body = null;
						Log.d(TAG, mGeoRequest.toString());
						
						try {
							response = client.newCall(request).execute();
							body = response.body().string();
						} catch (IOException e) {
							e.printStackTrace();
							return null;
						}
						
						try {
							JSONObject object = new JSONObject(body);
							JSONObject mObject = object.getJSONObject("position");
							YaLocationAnswer mAnswer = YaLocationAnswer.get(CustomJSONObject.getCustom(mObject));
							if (mAnswer == null) Log.d(TAG, "YaLocationAnswer mAnswer == null!");
							else mAnswer.create(MainActivity.this);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return body;
					}
					
					@Override 
					public void onPostExecute(String mString){
						Log.d(TAG, "" + mString);
						if (isCancelled()) return;
						if (mString == null) Toast.makeText(MainActivity.this, "Some problem has occurred during location receiving. See Log.", Toast.LENGTH_LONG).show();
						else listViewSetup();
					}
					
					
				};
				mTask.execute(mYaGeoRequest);
				
			}
		});
		
		mRequest.collectData(this);
	}
}