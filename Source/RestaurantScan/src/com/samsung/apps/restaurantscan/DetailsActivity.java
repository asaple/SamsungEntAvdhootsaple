/**
 * 
 */
package com.samsung.apps.restaurantscan;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.samsung.apps.restaurantscan.model.Detailview;
import com.samsung.apps.restaurantscan.model.ListHelper;

/**
 * @author Avdhoot Saple 
 * 
 * Created on Apr 25, 2015
 * 
 *         This class implments the activity for displaying the details about a
 *         selected restaurant
 */
public class DetailsActivity extends Activity {

	TextView tvName;
	TextView tvAddress;
	TextView tvPhone;
	TextView tvopenNow;
	TextView tvpriceLevel;
	TextView tvTypes;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detailpage);

		try {

			// Text fields
			tvName = (TextView) findViewById(R.id.textViewName);
			tvAddress = (TextView) findViewById(R.id.textViewaddr);
			tvPhone = (TextView) findViewById(R.id.textViewPhone);

			tvopenNow = (TextView) findViewById(R.id.textopenNow);
			tvpriceLevel = (TextView) findViewById(R.id.textViewprice);
			tvTypes = (TextView) findViewById(R.id.textViewTypes);

			// Get position to display
			Intent i = getIntent();
			String placeId = i.getStringExtra(RestaurantConstants.PLACEEID);
			Log.e(RestaurantConstants.INFO, "PLACE ID IS " + placeId);
			Detailview details = ListHelper.getDetails(placeId);
			// text elements
			tvName.setText(i.getStringExtra(RestaurantConstants.NAME));
			tvAddress.setText(RestaurantConstants.ADDRESS_LABEL
					+ details.getAddress());
			tvPhone.setText(RestaurantConstants.PHONE_LABEL
					+ details.getPhone());
			tvopenNow.setText(RestaurantConstants.OPENNOW_LABEL
					+ details.getOpenNow());
			tvpriceLevel.setText(RestaurantConstants.PRICELEVEL_LABEL
					+ details.getPriceLevel());
			tvTypes.setText(RestaurantConstants.TYPE_LABEL + details.getTypes());

			// get photo to display on details page
			ImageView detailsPhoto = (ImageView) findViewById(R.id.imageButtonrestaurant);
			Log.e(RestaurantConstants.INFO,
					"Photo Reference is " + details.getPhotoReference());

			new DownloadImageTask(detailsPhoto)
					.execute("https://maps.googleapis.com/maps/api/place/photo?maxwidth=200&photoreference="
							+ details.getPhotoReference()
							+ "&key="
							+ RestaurantConstants.GOOGLE_KEY);
		}

		catch (Exception ex) {
			Log.e(RestaurantConstants.ERROR, "Loading exception");
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void sendEmailMessage(View view) {
		Log.e(RestaurantConstants.INFO, "Inside email");

		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto", "", null));
		intent.putExtra(Intent.EXTRA_SUBJECT, "Details for " + tvName.getText());
		intent.putExtra(
				Intent.EXTRA_TEXT,
				"Address: " + tvAddress.getText() + "\nPhone: "
						+ tvPhone.getText() + "\nOpen Now: "
						+ tvopenNow.getText() + "\nPrice Level: "
						+ tvpriceLevel.getText() + "\nType: "
						+ tvTypes.getText());
		startActivity(Intent.createChooser(intent, "Choose an Email client :"));
	}

	public void mapLocation(View view) {
		Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + tvAddress.getText());
		Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
		mapIntent.setPackage("com.google.android.apps.maps");
		startActivity(mapIntent);
	}

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
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

}
