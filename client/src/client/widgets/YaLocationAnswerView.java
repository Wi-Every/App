package client.widgets;

import ru.alxr.client.R;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import client.locationrequest.model.YaLocationAnswer;

public class YaLocationAnswerView extends LinearLayout{
	
	public static final int RESOURCE_LAYOUT = R.layout.ya_view;
	private static final int TYPE = R.id.ya_view_type;
	private static final int LAT = R.id.ya_view_latitude;
	private static final int LONG = R.id.ya_view_longitude;
	private static final int PRECISION = R.id.ya_view_precision;
	
	private YaLocationAnswer mAnswer;

	public YaLocationAnswerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public YaLocationAnswer getmAnswer() {
		return mAnswer;
	}

	public void setmAnswer(YaLocationAnswer mAnswer) {
		this.mAnswer = mAnswer;
		setup(mAnswer);
	}
	
	private void setup(YaLocationAnswer mAnswer){
		if (mAnswer == null) return;
		setupCouple(LAT, YaLocationAnswer.KEY_LATITUDE, mAnswer.getLatitude());
		setupCouple(LONG, YaLocationAnswer.KEY_LONGITUDE, mAnswer.getLongitude());
		setupCouple(TYPE, YaLocationAnswer.KEY_TYPE, mAnswer.getType());
		setupCouple(PRECISION, YaLocationAnswer.KEY_PRECISION, mAnswer.getPrecision());
	}
	
	private void setupCouple(int id, String key, Object value){
		CoupleView mView = (CoupleView) findViewById(id);
		if (mView == null || value == null) return;
		mView.setup(key, value);
	}
}