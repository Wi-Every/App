package client;

import ru.alxr.client.R;
import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	private static final int LAYOUT = R.layout.main_activity;
	
	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setContentView(LAYOUT);
		
	}
}