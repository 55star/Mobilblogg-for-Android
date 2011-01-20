/**
 * 
 */
package com.fivestar.mobilblogg;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class mainMenuView extends Activity {

	private MobilbloggApp app;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainmenu);
		app = ((MobilbloggApp)getApplicationContext());
	}

	public void mainMenuClickHandler(View view) {
		switch(view.getId()) {
		case R.id.Button01:
			System.out.println("Goto my blogg");
			Intent mbIntent = new Intent(view.getContext(), BloggView.class);
			mbIntent.putExtra("username", app.getUserName());
			startActivityForResult(mbIntent, 0);
			break;
		case R.id.Button02:
			System.out.println("Goto my start page");
			Intent spIntent = new Intent(view.getContext(), StartPageView.class);
			startActivityForResult(spIntent, 0);
			break;
			
		case R.id.Button05:
			System.out.println("Goto firstpage");
			Intent fpIntent = new Intent(view.getContext(), FirstPageView.class);
			startActivityForResult(fpIntent, 0);
			break;

		case R.id.Button03:
			System.out.println("Goto camera");
			Intent cIntent = new Intent(view.getContext(), CameraView.class);
			startActivityForResult(cIntent, 0);
			break;

		case R.id.Button04:
			System.out.println("Logout");
			app.com.shutdownHttpClient();
			app.setUserName("");
			app.setLoggedInStatus(false);
			finish();
			break;
		}
	}
}