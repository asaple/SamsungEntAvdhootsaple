/**
 * 
 */
package com.samsung.apps.restaurantscan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author Avdhoot Saple 
 * 
 * Created on Apr 25, 2015
 * 
 * This class represents the activity for the settings menu item
 */
public class SettingsActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsactiv);
	}

	/** Called when the user clicks the Send button */
	public void sendMessage(View view) {
		
		EditText editText = (EditText) findViewById(R.id.edit_message);
		
		try {
			int range = Integer.parseInt( editText.getText().toString());
			Intent i = new Intent();
			i.setClass(SettingsActivity.this, MainActivity.class);
			i.putExtra(RestaurantConstants.RANGE, editText.getText().toString());
			startActivity(i);
		} catch (NumberFormatException e) {
			// display message to user to enter only numeric value
			Toast.makeText(this, "Please enter numberic value",
					Toast.LENGTH_SHORT).show();
		}

	}

}
