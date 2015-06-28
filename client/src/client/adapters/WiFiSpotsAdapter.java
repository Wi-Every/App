package client.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import client.locationrequest.model.WiFiInfo;
import client.widgets.WiFiInfoView;

public class WiFiSpotsAdapter extends BaseAdapter implements ListAdapter{

	public static WiFiSpotsAdapter get(Context context){
		if (context == null) return null;
		return new WiFiSpotsAdapter(context);
	}
	
	private WiFiSpotsAdapter (Context context){
		mLayoutInflater = LayoutInflater.from(context);
	}
	
	private LayoutInflater getLayoutInflater(ViewGroup parent){
		if (mLayoutInflater == null) mLayoutInflater = LayoutInflater.from(parent.getContext());
		return mLayoutInflater;
	}
	
	private List <WiFiInfo> list;
	private LayoutInflater mLayoutInflater;
	
	@Override
	public int getCount() {
		return list != null ? list.size():0;
	}

	@Override
	public WiFiInfo getItem(int position) {
		if (list == null) return null;
		WiFiInfo mSession = null;
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
		WiFiInfo mWiFiInfo = getItem(position);
		WiFiInfoView mView;
		if (convertView == null) mView = (WiFiInfoView) getLayoutInflater(parent).inflate(WiFiInfoView.LAYOUT, parent, false);
		else mView = (WiFiInfoView) convertView;
		mView.setWiFiInfo(mWiFiInfo);
		return mView;
	}
	
	public void setData(List <WiFiInfo> list){
		this.list = list;
	}
	
	public void dropData(){
		this.list = null;
		notifyDataSetChanged();
	}
}