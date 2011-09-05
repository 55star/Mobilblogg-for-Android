package com.fivestar.mobilblogg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Mobilblogg extends Activity {

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MobilbloggApp app = ((MobilbloggApp)getApplicationContext());
		app.startServices();
		if(!app.getLoggedInStatus()) {
			Intent intent = new Intent(this,LoginView.class);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this,MainMenuView.class);
			startActivity(intent);
		}
		finish();
	}
}