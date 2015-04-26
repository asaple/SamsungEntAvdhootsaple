/**
 * 
 */
package com.samsung.apps.restaurantscan;

import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.apps.restaurantscan.model.Overview;

/**
 * @author Avdhoot Saple
 * 
 * Created on Apr 25, 2015
 * 
 * This class represents the list adapter to be attached for list of restaurants within the vicinity
 */
public class BinderData extends BaseAdapter {

	LayoutInflater inflater;
	ImageView thumbView;
	List<Overview> restaurantCollection;
	ViewHolder viewHolder;

	public BinderData() {

	}

	public BinderData(Activity act, List<Overview> retaurantList) {

		this.restaurantCollection = retaurantList;

		inflater = (LayoutInflater) act
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return this.restaurantCollection.size();
	}

	@Override
	public Object getItem(int position) {

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {

			view = inflater.inflate(R.layout.list_row, null);
			viewHolder = new ViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.restName); 
			viewHolder.vicinity = (TextView) view.findViewById(R.id.restVici); 
			viewHolder.image = (ImageView) view.findViewById(R.id.list_image); 
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		// Setting all values in listview

		viewHolder.name.setText(restaurantCollection.get(position).getName());
		viewHolder.vicinity.setText(restaurantCollection.get(position)
				.getVicinity());
		new DownloadImageTask(viewHolder.image).execute(restaurantCollection.get(
				position).getIconUrl());
		return view;
	}

	/**
	 * The view holder class
	 * */
	static class ViewHolder {

		TextView name;
		TextView vicinity;
		ImageView image;

	}

	/**
	 * This class is utilized to set the image based on the url
	 * */
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e(RestaurantConstants.ERROR, e.getMessage());

			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
}
