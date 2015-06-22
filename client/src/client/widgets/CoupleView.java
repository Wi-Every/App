package client.widgets;

import ru.alxr.client.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CoupleView extends LinearLayout {
	
	private static final int KEY = R.id.couple_view_key;
	private static final int VALUE = R.id.couple_view_value;

	public CoupleView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	private void setupView(int id, String string){
		TextView view = (TextView) getView(id);
		if (view == null) return;
		view.setText(string);
	}
	
	public void setup(String key, Object value){
		setupView(KEY, key);
		setupView(VALUE, value.toString());
	}

	private View getView(int id) {
		return findViewById(id);
	}
}