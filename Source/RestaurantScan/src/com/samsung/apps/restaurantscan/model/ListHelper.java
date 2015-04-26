/**
 * 
 */
package com.samsung.apps.restaurantscan.model;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.samsung.apps.restaurantscan.RestaurantConstants;

/**
 * @author Avdhoot Saple 
 * 
 * Created on Apr 25, 2015
 * 
 *         This is a helper class primarily for invoking google places api and
 *         parsing the json response
 */
public class ListHelper {

	public static List<Overview> getRestaurantList(String latitude,
			String longitude, String range) {
		Log.i(RestaurantConstants.INFO, " INSIDE getRestaurantList latitude "
				+ latitude + "longitude" + longitude + "Range is " + range);

		String jsonResponse = makeCall("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
				+ latitude
				+ ","
				+ longitude
				+ "&radius="
				+ range
				+ "&types=food&key=" + RestaurantConstants.GOOGLE_KEY);
		ArrayList<Overview> overViewList = parseJsonResponseForRestaurantList(jsonResponse);

		return overViewList;
	}

	public static Detailview getDetails(String placeId) {
		Log.i(RestaurantConstants.INFO, " INSIDE place Id " + placeId);
		String jsonResponse = makeCall("https://maps.googleapis.com/maps/api/place/details/json?placeid="
				+ placeId + "&key=" + RestaurantConstants.GOOGLE_KEY);
		return parseJsonResponseForRestaurantDetails(jsonResponse);
	}

	
	private static ArrayList<Overview> parseJsonResponseForRestaurantList(
			final String response) {
		
		ArrayList<Overview> overViewList = new ArrayList<Overview>();
		try {
			JSONObject jsonObject = new JSONObject(response);
			if (jsonObject.has(RestaurantConstants.RESULTS)) {
				JSONArray jsonArray = jsonObject.getJSONArray(RestaurantConstants.RESULTS);
				Overview overview;
				for (int i = 0; i < jsonArray.length(); i++) {
					overview = new Overview();
					if (jsonArray.getJSONObject(i).has(RestaurantConstants.NAME)) {
						overview.setName(jsonArray.getJSONObject(i).optString(
								RestaurantConstants.NAME));
						overview.setVicinity(jsonArray.getJSONObject(i)
								.optString(RestaurantConstants.VICINITY));
						overview.setPlaceId(jsonArray.getJSONObject(i)
								.optString(RestaurantConstants.PLACE_ID));
						overview.setIconUrl(jsonArray.getJSONObject(i)
								.optString(RestaurantConstants.ICON));
						}
					overViewList.add(overview);
				}
			}
		} catch (Exception e) {
			Log.e(RestaurantConstants.ERROR, e.getMessage());
			return overViewList;
		}
		return overViewList;
	}

	private static Detailview parseJsonResponseForRestaurantDetails(
			final String response) {
		
		Detailview detailsview = new Detailview();
		try {

			JSONObject jsonObject = new JSONObject(response);

			if (jsonObject.has(RestaurantConstants.RESULT)) {
				JSONObject jsonObjectresult = jsonObject
						.getJSONObject(RestaurantConstants.RESULT);
				detailsview.setAddress(jsonObjectresult
						.optString(RestaurantConstants.FORMATTED_ADDRESS));
				detailsview.setPhone(jsonObjectresult
						.optString(RestaurantConstants.FORMATTED_PHONE));

				if (jsonObjectresult.has(RestaurantConstants.OPENING_HOURS)) {
					if (jsonObjectresult.getJSONObject(RestaurantConstants.OPENING_HOURS).has(
							RestaurantConstants.OPEN_NOW)) {
						if (jsonObjectresult.getJSONObject(RestaurantConstants.OPENING_HOURS)
								.getString(RestaurantConstants.OPEN_NOW).equals(RestaurantConstants.TRUE)) {
							detailsview.setOpenNow(RestaurantConstants.YES);
						} else {
							detailsview.setOpenNow(RestaurantConstants.NO);
						}
					}
				} else {
					detailsview.setOpenNow(RestaurantConstants.NOT_KNOWN);
				}

				if (jsonObjectresult.has(RestaurantConstants.TYPES)) {
					JSONArray typesArray = jsonObjectresult
							.getJSONArray(RestaurantConstants.TYPES);
					StringBuilder stringBuilder = new StringBuilder();
					for (int j = 0; j < typesArray.length(); j++) {
						if (j == typesArray.length() - 1) {
							stringBuilder.append(typesArray.getString(j));
						} else {
							stringBuilder
									.append(typesArray.getString(j) + ", ");
						}

					}
					String types = stringBuilder.toString().replace(RestaurantConstants.UNDERSCORE, RestaurantConstants.SPACE);
					detailsview.setTypes(types);
				}
				// get first available photo of location
				if (jsonObjectresult.has(RestaurantConstants.PHOTOS)) {
					JSONArray photoTypeArray = jsonObjectresult
							.getJSONArray(RestaurantConstants.PHOTOS);
					for (int j = 0; j < photoTypeArray.length(); j++) {
						JSONObject photoObject = photoTypeArray
								.getJSONObject(j);
						if (photoObject.has(RestaurantConstants.PHOTOREFERENCE)) {
							detailsview.setPhotoReference(photoObject
									.optString(RestaurantConstants.PHOTOREFERENCE));
							Log.i(RestaurantConstants.INFO,"Photo Reference is "
									+ detailsview.getPhotoReference());
						
							break;
						}
					}
				}

				if (jsonObjectresult.has(RestaurantConstants.PRICE_LEVEL)) {
					try {
						int optInt = jsonObjectresult.optInt(RestaurantConstants.PRICE_LEVEL);
						if (optInt >= 1) {
							String pricelevel = "";
							for (int k = 0; k < optInt; k++) {
								pricelevel += RestaurantConstants.SPACE + RestaurantConstants.DOLLAR;
							}
							detailsview.setPriceLevel(pricelevel);
						} else {
							detailsview.setPriceLevel(RestaurantConstants.NOT_KNOWN);
						}

					} catch (Exception e) {
						detailsview.setPriceLevel(RestaurantConstants.NOT_KNOWN);
					}
				}
				if (detailsview.getPriceLevel() == null) {
					detailsview.setPriceLevel(RestaurantConstants.NOT_KNOWN);
				}

				
			}
		} catch (Exception e) {
			e.printStackTrace();

		}
		return detailsview;

	}

	

	public static String makeCall(String url) {

		// string buffers the url
		StringBuffer buffer_string = new StringBuffer(url);
		String replyString = "";

		// instanciate an HttpClient
		HttpClient httpclient = new DefaultHttpClient();
		// instanciate an HttpGet
		HttpGet httpget = new HttpGet(buffer_string.toString());

		try {
			// get the responce of the httpclient execution of the url
			HttpResponse response = httpclient.execute(httpget);
			InputStream is = response.getEntity().getContent();

			// buffer input stream the result
			BufferedInputStream bis = new BufferedInputStream(is);
			ByteArrayBuffer baf = new ByteArrayBuffer(20);
			int current = 0;
			while ((current = bis.read()) != -1) {
				baf.append((byte) current);
			}
			// the result as a string is ready for parsing
			replyString = new String(baf.toByteArray());
		} catch (Exception e) {
			Log.i(RestaurantConstants.ERROR, e.getMessage());
			e.printStackTrace();
		}
		
		return replyString.trim();
	}
}
