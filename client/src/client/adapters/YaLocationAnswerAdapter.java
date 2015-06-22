package client.adapters;

import java.util.List;

import client.locationrequest.model.YaLocationAnswer;
import client.widgets.YaLocationAnswerView;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

public class YaLocationAnswerAdapter extends BaseAdapter implements ListAdapter{
	
	public static YaLocationAnswerAdapter get(Context context){
		if (context == null) return null;
		return new YaLocationAnswerAdapter(context);
	}
	
	private YaLocationAnswerAdapter (Context context){
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	private LayoutInflater getLayoutInflater(ViewGroup parent){
		if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
		return mLayoutInflater;
	}
	
	private List <YaLocationAnswer> list;
	private LayoutInflater mLayoutInflater;
	
	@Override
	public int getCount() {
		return list != null ? list.size():0;
	}

	@Override
	public YaLocationAnswer getItem(int position) {
		if (list == null) return null;
		YaLocationAnswer mSession = null;
		try {
			mSession = list.get(position);
		} catch (IndexOutOfBoundsException e) {}
		return mSession;
	}

	@Override
	public long getItemId(int position) {
		return View.NO_ID;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (parent == null) return null;
		YaLocationAnswer mYaLocationAnswer = getItem(position);
		YaLocationAnswerView mView;
		if (convertView == null) mView = (YaLocationAnswerView) getLayoutInflater(parent).inflate(YaLocationAnswerView.RESOURCE_LAYOUT, parent, false);
		else mView = (YaLocationAnswerView) convertView;
		mView.setmAnswer(mYaLocationAnswer);
		return mView;
	}
	
	public void setData(List <YaLocationAnswer> list){
		this.list = list;
	}

}