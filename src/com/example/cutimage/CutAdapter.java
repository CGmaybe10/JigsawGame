package com.example.cutimage;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;

public class CutAdapter extends BaseAdapter {
	List<Bitmap> btmapList;
	Context context;
	LayoutInflater layoutInflater;
	int imgHeight;

	public CutAdapter(Context context, List<Bitmap> btmapList, int imgHeight) {
		this.btmapList = btmapList;
		this.context = context;
		this.imgHeight = imgHeight;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return btmapList.size();
	}

	@Override
	public Object getItem(int position) {
		return btmapList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		viewHolder viewHolder = new viewHolder();

		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.image, null);
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);

			LayoutParams layoutParmas = (LayoutParams) viewHolder.imageView
					.getLayoutParams();
			layoutParmas.height = imgHeight;
			viewHolder.imageView.setLayoutParams(layoutParmas);

			viewHolder.imageView.setImageBitmap(btmapList.get(position));

		} else {
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.imageView);
			viewHolder.imageView.setImageBitmap(btmapList.get(position));
		}

		return convertView;
	}

	public class viewHolder {
		ImageView imageView;
	}
}